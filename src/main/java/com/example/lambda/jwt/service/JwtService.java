package com.example.lambda.jwt.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.dto.ErrorDto;
import com.example.lambda.jwt.domain.Tokens;
import com.example.lambda.jwt.repository.TokensRepository;
import com.example.lambda.jwt.web.JwtProvider;
import com.example.lambda.jwt.web.dto.TokenDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final JwtProvider jwtProvider;
    private final TokensRepository tokensRepository;

    /* Access Token 갱신 */
    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent request) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        // Header 부분 가져오기
        Map<String, String> headers = request.getHeaders();
        if (headers == null) {
            log.error("헤더가 없음");
            return createError(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        String headerToken = headers.get("Authorization");  // 토큰 가져오기

        if (headerToken == null || !(headerToken.startsWith("Bearer "))) {
            log.error("잘못된 토큰 명시 에러");
            return createError(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        String accessToken = headerToken.substring(7);

        // Access Token이 유효할 때
        if (jwtProvider.checkToken(accessToken)) {
            if (tokensRepository.existsByAccessToken(accessToken)) {
                Tokens tokens = getToken(accessToken);  // Token 값들 가져오기

                if (tokens == null) {
                    return createError(HttpStatus.UNAUTHORIZED, "재로그인이 필요합니다.");
                }

                String newAccessToken = jwtProvider.checkRefreshToken(tokens);  // Access Token 갱신

                // Redis 값도 갱신
                tokens.updateAccessToken(newAccessToken);
                tokensRepository.save(tokens);

                // 토큰 갱신이 안 될때
                if (newAccessToken == null) {
                    return createError(HttpStatus.UNAUTHORIZED, "재로그인이 필요합니다.");
                }

                // Client에게 전달할 값
                TokenDto tokenInfo = TokenDto.builder()
                        .accessToken(newAccessToken)
                        .grantType("Bearer")
                        .expiredAt(jwtProvider.getExpiration(newAccessToken))
                        .build();

                response.setStatusCode(200);
                response.setHeaders(Map.of("Content-Type", "application/json"));
                try {
                    response.setBody(new ObjectMapper().writeValueAsString(tokenInfo));
                } catch (JsonProcessingException e) {
                    log.error("Json 변환 에러");
                }

                log.info("Access Token 갱신, New Access Token: {}, timestemp: {}", newAccessToken, LocalDateTime.now());

                return response;
            }
        }

        return createError(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    }

    /* 에러 응답 생성 */
    private APIGatewayProxyResponseEvent createError(HttpStatus httpStatus, String msg) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent(); // 응답값 생성

        // 에러 응답 Body
        ErrorDto error = ErrorDto.builder()
                .status(httpStatus.value())
                .error(httpStatus.name())
                .message(msg)
                .build();

        response.setStatusCode(httpStatus.value()); // 응답 코드 설정
        response.setHeaders(Map.of("Content-Type", "application/json"));    // 응답 타입 JSON으로 설정
        try {
            response.setBody(new ObjectMapper().writeValueAsString(error)); // ErrorDto -> JSON
        } catch (JsonProcessingException e) {
            log.error("Json 변환 에러");
        }
        return response;
    }

    /* Access Token 값으로 Refresh Token 정보도 가져오기 */
    public Tokens getToken(String accessToken) {
        return tokensRepository.findByAccessToken(accessToken).orElse(null);    // 없으면 null
    }
}
