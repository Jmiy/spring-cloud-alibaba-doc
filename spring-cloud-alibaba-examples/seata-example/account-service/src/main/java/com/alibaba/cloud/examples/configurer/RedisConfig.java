package com.alibaba.cloud.examples.configurer;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;


//@Configuration
//@EnableCaching
public class RedisConfig extends CachingConfigurerSupport{
	
//	@Bean
//	public KeyGenerator keyGenerator() {
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(target.getClass().getName());
//                sb.append(":");
//                sb.append(method.getName());
//                for (Object obj : params) {
//                    sb.append(obj.toString());
//                }
//                return sb.toString();
//            }
//        };
//    }
//
//    /**
//     * 缓存配置管理器
//     */
//    @Bean
//    public CacheManager cacheManager(LettuceConnectionFactory factory) {
//        //以锁写入的方式创建RedisCacheWriter对象
//        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
//        /*
//        设置CacheManager的Value序列化方式为JdkSerializationRedisSerializer,
//        但其实RedisCacheConfiguration默认就是使用
//        StringRedisSerializer序列化key，
//        JdkSerializationRedisSerializer序列化value,
//        所以以下注释代码就是默认实现，没必要写，直接注释掉
//         */
//        // RedisSerializationContext.SerializationPair pair = RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.getClass().getClassLoader()));
//        // RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
//        //创建默认缓存配置对象
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//        RedisCacheManager cacheManager = new RedisCacheManager(writer, config);
//        return cacheManager;
//    }
//
//    /**
//     * 获取缓存操作助手对象
//     *
//     * @return
//     */
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory factory) {
//        //创建Redis缓存操作助手RedisTemplate对象
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(factory);
//        //以下代码为将RedisTemplate的Value序列化方式由JdkSerializationRedisSerializer更换为Jackson2JsonRedisSerializer
//        //此种序列化方式结果清晰、容易阅读、存储字节少、速度快，所以推荐更换
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//        return template;//StringRedisTemplate是RedisTempLate<String, String>的子类
//    }
}