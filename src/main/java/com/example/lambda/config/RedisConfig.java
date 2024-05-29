package com.example.lambda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)    // Redis Repository 활성화, Index도 같이 TTL 적용되어 삭제
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;   // Redis Host 명

    @Value("${spring.data.redis.password}")
    private String redisPassword;   // Redis Password

    @Value("${spring.data.redis.port}")
    private int redisPort;  // Redis Port 번호

    /* Redis DB 연결 */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);  // host 설정
        redisConfiguration.setPassword(redisPassword);  // 비밀번호 설정
        redisConfiguration.setPort(redisPort);  // 포트 설정

        return new LettuceConnectionFactory(redisConfiguration);
    }

    /* Key - Value 형태를 직렬화 */
    @Bean
    public RedisTemplate<Long, Object> redisTemplate() {
        RedisTemplate<Long, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
