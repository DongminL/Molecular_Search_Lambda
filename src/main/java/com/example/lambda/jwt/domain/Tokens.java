package com.example.lambda.jwt.domain;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RedisHash(value = "tokens") // Key의 prefix 설정
public class Tokens {

    @Id // Key (Auto Increase)
    private Long id;    // Primary Key

    private String refreshToken;  // Refresh Token 값

    @Indexed    // 검색허용
    private String accessToken;   // Access Token 값

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long ttl;   // 데이터 만료시간 (Refresh Token 만료 시간과 같음)

    @Builder
    public Tokens(Long id, String refreshToken, String accessToken, Long ttl) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.ttl = ttl;
    }
    
    /* Access Token만 갱신 */
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
