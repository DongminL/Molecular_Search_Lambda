package com.example.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.function.Function;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.lambda.jwt"})
@RequiredArgsConstructor
public class LambdaApplication {

    private final JwtService jwtService;

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> lambdaApiGatewayFunctionBean() {
        return jwtService;
    }

    public static void main(String[] args) {
        SpringApplication.run(LambdaApplication.class, args);
    }

}
