package com.alibaba.cloud.examples.common.provider;

import com.google.gson.Gson;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.cloud.examples.common.util.SpringBeanUtil;

/**
 * 缓存提供类
 */
public class CacheProvider {

    //由于当前class不在spring boot框架内（不在web项目中）所以无法使用autowired，使用此种方法进行注入 @Resource(name="redisTemplate")
    private static RedisTemplate<String, String> redisTemplate=(RedisTemplate<String, String>) SpringBeanUtil.getBean("redisTemplate");

    public static <T> boolean set(String key, T value) {

        Gson gson = new Gson();

        return set(key, gson.toJson(value),7200);
    }

    public static boolean set(String key, String value, long validTime) {

        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                connection.expire(serializer.serialize(key), validTime);
                return true;
            }
        });
        return result;
    }

    public static <T> T get(String key, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(get(key), clazz);
    }

    public static String get(String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    public static boolean del(String key) {
        return redisTemplate.delete(key);
    }
}
