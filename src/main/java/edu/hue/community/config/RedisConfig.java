package edu.hue.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author 47552
 * @date 2021/09/20
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置key的序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式
        redisTemplate.setValueSerializer(RedisSerializer.json());
        // 设置Hash key的序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置 Hash value的序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        // 使设置生效
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
