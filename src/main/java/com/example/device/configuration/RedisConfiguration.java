package com.example.device.configuration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;

@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {

    private String KEY_SEPERATOR = "#";

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            public Object generate(Object target, Method method, Object... params) {

                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getSimpleName());
                sb.append(KEY_SEPERATOR);
                sb.append(method.getName());
                sb.append(KEY_SEPERATOR);
                for (Object param : params) {
                    sb.append(param.toString());
                    sb.append(KEY_SEPERATOR);
                }
                String str = sb.toString();
                return str.substring(0, str.length() - 1);
            }
        };
    }

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        final StringRedisTemplate template = new StringRedisTemplate(factory);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("127.0.0.1:6399")
                .sentinel("127.0.0.1", 26579);

        return new LettuceConnectionFactory(sentinelConfig);
    }


}