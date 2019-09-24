package com.example.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class RedisCacheService implements CacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String key, String hashKey, String value) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(hashKey, value);
    }

    @Override
    public void save(String key, Map<String, String> values) {
        stringRedisTemplate.opsForHash().putAll(key, values);
    }

    @Override
    public String getHashMap(String key, String hashKey) {
        return (String) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Map getHashMap(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    @Override
    public void disposeCache(String key) {
        stringRedisTemplate.opsForHash().delete(keyGenerator(key));
    }

    @Override
    public void removeHashKey(String key, String hashKey) {
        stringRedisTemplate.opsForHash().delete(keyGenerator(key), hashKey);
    }

    private String keyGenerator(String deviceId) {
        return "Aggregation-" + deviceId;
    }
}