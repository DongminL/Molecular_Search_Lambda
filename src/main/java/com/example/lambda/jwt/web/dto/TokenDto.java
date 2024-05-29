package com.example.lambda.jwt.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {

    private String accessToken;  // 갱신 토큰
    private String grantType;   // 토큰 타입
    private Long expiredAt; // 남은 시간
}
