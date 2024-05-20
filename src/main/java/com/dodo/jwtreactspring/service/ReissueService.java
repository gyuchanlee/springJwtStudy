package com.dodo.jwtreactspring.service;

import com.dodo.jwtreactspring.entity.RefreshToken;
import com.dodo.jwtreactspring.jwt.JWTUtil;
import com.dodo.jwtreactspring.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueService {
    /**
     * Refresh Token Rotate
     * - 개요 : 재발급 엔드포인트에서 Refresh로 Access를 갱신할 때, Refresh 토큰도 같이 재발급 (refresh 토큰은 결국 일회용)
     * - 장점 : 1. refresh 토큰 교체로 보안성 강화   2. 로그인 지속시간 길어짐
     * - 구현 : 발급했던 Refresh 토큰을 모두 기억한 뒤, Rotate 이전의 Refresh 토큰은 사용하지 못하도록 로직 구현
     *
     * - 추가 주의 로직 : 이전에 등록했던 refresh 토큰을 기억해두는 블랙리스트가 있어야 함 -> 세션 아이디 기억하듯, 그전의 리프레시 토큰은 못쓰도록 로직 구현이 필요
     *   근데 이럴거면 세션 쓰지.. 이거 왜 씀..  이라고 하지만 어쩔 수 없이 보안이 중요하고 jwt를 굳이 써야하는 상황이라면 해야지뭐.
     */

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키 리스트에서 HttpOnly 쿠키인 refresh token 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // 리프레시 토큰이 있는 지 검증
        if (refresh == null) {
            // 응답
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }

        // expire 만료 체크 -> refresh 토큰도 만료면 그냥 재로그인해야됨
        if (jwtUtil.isExpired(refresh)) {
            return new ResponseEntity<>("refresh token is expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh 토큰인지 검증
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("refresh token is invalid", HttpStatus.BAD_REQUEST);
        }

        // 기존의 refresh 토큰이 RTR 저장소에 존재하는 지 확인.
        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            return new ResponseEntity<>("refresh token does not exist", HttpStatus.BAD_REQUEST);
        }
        // ------ 검증 끝

        // refresh 토큰에서 새로 발급한 access 토큰에 넣을 값들을 꺼내서 넣고 생성
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // RTR : 리프레시 토큰도 함께 재발급
        String newAccess = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        // RTR 저장소에 기존의 refresh 토큰을 삭제하고, 새로 발급한 refresh 토큰 저장 (여기에 저장소로 Redis 쓰면 좋을듯, TTL을 통한 장점이 있음)
        refreshTokenRepository.deleteByRefreshToken(refresh);
        addRefreshTokenRepository(username, refreshToken, 24 * 60 * 60 * 1000L);

        // 응답에 넣어서 반환
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", refreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 리프레시 토큰을 담을 쿠키 생성
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
//        cookie.setSecure(true); // http ssl 적용시
//        cookie.setPath("/"); // 쿠키가 적용될 경로 범위 지정
        cookie.setHttpOnly(true);

        return cookie;
    }

    // 리프레시토큰 저장소 추가 메서드
    private void addRefreshTokenRepository(String username, String refreshToken, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refresh = RefreshToken.builder()
                .username(username)
                .refreshToken(refreshToken)
                .expiration(date.toString())
                .build();

        refreshTokenRepository.save(refresh);
    }
}
