package com.example.device.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public interface CacheService {

    void save(String key, String hashKey, String value);

    void save(String key, Map<String, String> values);

    String getHashMap(String key, String hashKey);

    Map getHashMap(String key);

    void disposeCache(String key);

    void removeHashKey(String key, String hashKey);
}
