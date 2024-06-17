package com.commerce.queue_for_reserve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveHashOperations<String, String, Object> reactiveRedisOperations(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(new GenericJackson2JsonRedisSerializer())
                .key(stringRedisSerializer).value(serializer)
                .hashKey(serializer)
                .hashValue(serializer).build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext).opsForHash();
    }
}
