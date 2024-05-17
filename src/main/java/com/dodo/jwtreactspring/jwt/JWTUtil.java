package com.dodo.jwtreactspring.jwt;

import com.dodo.jwtreactspring.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT의 발급과 검증을 담당하는 클래스 - jwt 0.12.3 버전
 *
 *  - 토큰 Payload 정보
 *      1. 회원 정보 (민감하지 않은 필수 정보들) -> username, role
 *      2. 생성일
 *      3. 만료일
 *
 * - 메서드
 *  1. 토큰 생성 메서드
 *  2. 토큰 정보를 검증할 메서드
 */
@Slf4j
@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        // 암호화 객체 키 생성
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey) // 시크릿 키를 넣어 검증 진행 (우리 서버에서 생성된 것인지 확인)
                .build()
                .parseSignedClaims(token) // claims 확인
                .getPayload() // 페이로드 지정
                .get("username", String.class); // 가져올 특정 필드 지정
    }

    public String getRole(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey) // 시크릿 키를 넣어 검증 진행 (우리 서버에서 생성된 것인지 확인)
                .build()
                .parseSignedClaims(token) // claims 확인
                .getPayload() // 페이로드 지정
                .get("role", String.class); // 가져올 특정 필드 지정
    }

    public Claims getPayload(String token) {

        Claims payload = Jwts
                .parser()
                .verifyWith(secretKey) // 시크릿 키를 넣어 검증 진행 (우리 서버에서 생성된 것인지 확인)
                .build()
                .parseSignedClaims(token) // claims 확인
                .getPayload();

        log.info("payload: {}", payload);

        return payload;
    }

    public Boolean isExpired(String token) {

        boolean isExpired = false;

        try {
            isExpired = Jwts
                    .parser()
                    .verifyWith(secretKey) // 시크릿 키를 넣어 검증 진행 (우리 서버에서 생성된 것인지 확인)
                    .build()
                    .parseSignedClaims(token) // claims 확인
                    .getPayload()
                    .getExpiration()
                    .before(new Date());// 현재 날 기준 만료일 이전인지 확인
        } catch (ExpiredJwtException e) {
            isExpired = true;
        }

        return isExpired;
    }

    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 현재 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey)
                .compact();
    }

}
