package com.dodo.jwtreactspring.jwt;

import com.dodo.jwtreactspring.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

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
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 10L); // 1시간 만료 - 60 * 60 * 1000L
        String refreshToken = jwtUtil.createJwt(username, role, 30 * 24 * 60 * 60 * 1000L); // 30일 후 만료
        // header에 담아서 응답 -> RFC 7235 정의로 무조건 Authorization에 담아서 보낼거면 Bearer 를 붙여보내도록 함
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("Refresh", "Bearer " + refreshToken);
    }

    // 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // 로그인 실패 시, 401 에러 반환
        response.setStatus(401);
    }
}
