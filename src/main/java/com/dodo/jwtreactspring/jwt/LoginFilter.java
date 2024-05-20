package com.dodo.jwtreactspring.jwt;

import com.dodo.jwtreactspring.dto.CustomUserDetails;
import com.dodo.jwtreactspring.entity.RefreshToken;
import com.dodo.jwtreactspring.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * UsernamePasswordAuthenticationFilter -> AuthenticationManager 로 인증정보가 전달되서 검증이 진행됨.
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("username: {}", username);
        log.info("password: {}", password);

        // 검증을 위한 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 토큰에 담은 검증 정보를 검증 매니저에 전달
        return authenticationManager.authenticate(authenticationToken);
    }

    // 성공
    @Override
    @Transactional
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // userDetails 뽑아내기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // username
        String username = userDetails.getUsername();
        // role
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
//        log.info("role = {}", role);

        // token 발급
        String accessToken = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L); // 10분 만료
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L); // 24시간 후 만료

        // + RTR 사용 -> 리프레시 토큰을 서버측 저장소에 저장
        addRefreshTokenRepository(username, refreshToken, 24 * 60 * 60 * 1000L);

        // 응답 설정
        // access token : header에 담아서 응답 -> RFC 7235 정의로 무조건 Authorization에 담아서 보낼거면 Bearer 를 붙여보내도록 함 (받아서 로컬스토리지 저장)
        response.setHeader("Authorization", "Bearer " + accessToken);
        // refresh token : 쿠키로 보내서 httpOnly 쿠키에 저장해서 사용.
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpServletResponse.SC_OK);

    }

    private void addRefreshTokenRepository(String username, String refreshToken, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refresh = RefreshToken.builder()
                .username(username)
                .refreshToken(refreshToken)
                .expiration(date.toString())
                .build();

        refreshTokenRepository.save(refresh);
    }

    // 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // 로그인 실패 시, 401 에러 반환
        response.setStatus(401);
    }

    // 쿠키 생성
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
//        cookie.setSecure(true); // http ssl 적용시
//        cookie.setPath("/"); // 쿠키가 적용될 경로 범위 지정
        cookie.setHttpOnly(true);

        return cookie;
    }
}
