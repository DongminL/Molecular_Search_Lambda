package com.example.lambda.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public JsonMapper jsonMapper() {
        return new JacksonMapper(objectMapper);
    }
}
