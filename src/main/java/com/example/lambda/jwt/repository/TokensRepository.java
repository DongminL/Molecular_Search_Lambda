package com.example.lambda.jwt.repository;

import com.example.lambda.jwt.domain.Tokens;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokensRepository extends CrudRepository<Tokens, Long> {

    Optional<Tokens> findByAccessToken(String accessToken);   // Access Token 값으로 Refresh Token 정보 불러오기

    void deleteById(Long id);   // Token Key 값으로 Tokens 값 삭제

    boolean existsByAccessToken(String accessToken);    // DB에 해당 Access Token 값이 있는지 검사
}
