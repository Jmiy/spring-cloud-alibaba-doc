package com.alibaba.cloud.examples.common.config;

import com.alibaba.cloud.examples.customer.Person;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StopWatch;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;

/**
 * 缓存配置-使用Lettuce客户端，自动注入配置的方式
 */
@Configuration
@EnableCaching //启用缓存
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 自定义缓存key的生成策略。默认的生成策略是看不懂的(乱码内容) 通过Spring 的依赖注入特性进行自定义的配置注入并且此类是一个配置类可以更多程度的自定义配置
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        System.out.println("====keyGenerator=======");
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(":");
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 缓存配置管理器
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory factory) {

        System.out.println("====cacheManager=======");

        //以锁写入的方式创建RedisCacheWriter对象
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
        /*
        设置CacheManager的Value序列化方式为JdkSerializationRedisSerializer,
        但其实RedisCacheConfiguration默认就是使用
        StringRedisSerializer序列化key，
        JdkSerializationRedisSerializer序列化value,
        所以以下注释代码就是默认实现，没必要写，直接注释掉
         */
        // RedisSerializationContext.SerializationPair pair = RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.getClass().getClassLoader()));
        // RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        //创建默认缓存配置对象
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheManager cacheManager = new RedisCacheManager(writer, config);
        return cacheManager;
    }

    /**
     * 获取缓存操作助手对象
     *
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory factory) {

        System.out.println("====redisTemplate=======");

        //创建Redis缓存操作助手RedisTemplate对象
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);

        //以下代码为将RedisTemplate的Value序列化方式由JdkSerializationRedisSerializer更换为Jackson2JsonRedisSerializer
        //此种序列化方式结果清晰、容易阅读、存储字节少、速度快，所以推荐更换
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;//StringRedisTemplate是RedisTempLate<String, String>的子类
    }

//    @Autowired
//    RedisConnectionFactory factory;
//
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory();
//    }

    /**
     * Configures a {@link ReactiveRedisTemplate} with {@link String} keys and a typed
     * {@link Jackson2JsonRedisSerializer}.
     */
    @Bean
    public ReactiveRedisTemplate<String, Person> reactiveJsonPersonRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer<Person>(Person.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Person> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext serializationContext = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    /**
     * Configures a {@link ReactiveRedisTemplate} with {@link String} keys and {@link GenericJackson2JsonRedisSerializer}.
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveJsonObjectRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
                .newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext serializationContext = builder
                .value(new GenericJackson2JsonRedisSerializer("_type")).build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

//    static final RedisSentinelConfiguration SENTINEL_CONFIG = new RedisSentinelConfiguration().master("mymaster") //
////            .sentinel("localhost", 26379) //
////            .sentinel("localhost", 26380) //
////            .sentinel("localhost", 26381)
//            .sentinel("192.168.152.128", 6379)
//            ;

//    static final RedisSentinelConfiguration SENTINEL_CONFIG = new RedisSentinelConfiguration().master("Egx") //
////            .sentinel("localhost", 26379) //
////            .sentinel("localhost", 26380) //
////            .sentinel("localhost", 26381)
//            .sentinel("192.168.152.128", 6379)
//            ;
//
//    @Bean
//    public StringRedisTemplate redisTemplate() {
//
//        return new StringRedisTemplate(connectionFactory());
//    }
//
//    @Bean
//    public RedisConnectionFactory connectionFactory() {
//        return new LettuceConnectionFactory(sentinelConfig(), LettuceClientConfiguration.defaultConfiguration());
//    }
//
//    public @Bean RedisSentinelConfiguration sentinelConfig() {
//        return SENTINEL_CONFIG;
//    }

//    /**
//     * Clear database before shut down.
//     */
//    public @PreDestroy
//    void flushTestDb() {
//        factory.getConnection().flushDb();
//    }
//
//    private static void startStopWatchIfNotRunning(StopWatch stopWatch) {
//
//        if (!stopWatch.isRunning()) {
//            stopWatch.start();
//        }
//    }
//
//    private static void printBackFromErrorStateInfoIfStopWatchIsRunning(StopWatch stopWatch) {
//
//        if (stopWatch.isRunning()) {
//            stopWatch.stop();
//            System.err.println("INFO: Recovered after: " + stopWatch.getLastTaskInfo().getTimeSeconds());
//        }
//    }
}