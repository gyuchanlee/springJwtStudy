package com.dodo.jwtreactspring.jwt;

import com.dodo.jwtreactspring.dto.CustomUserDetails;
import com.dodo.jwtreactspring.entity.Role;
import com.dodo.jwtreactspring.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT를 검증해서 그 요청에 한해서 사용할 인증 정보와 세션을 시큐리티에 등록함
 */
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request에서 Authorization Header 추출
        String authorization = request.getHeader("Authorization");

        // --- Authorization 헤더 검증 시작
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("JWT Token is Null");
            // 비어있으므로 필터 종료 -> 다음 필터로 넘김
            filterChain.doFilter(request, response);
            return;
        }
        // Bearer - 삭제
        String token = authorization.split(" ")[1];
        log.info("token:'{}'", token);

        // 토큰 만료 시간 검증 -> 만료 시, 다음 필터로 넘기지 않고 만료 예외를 던져줌
        if (jwtUtil.isExpired(token)) {
            // 만료 응답
            response.getWriter().print("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // access토큰인지 확인 -> 아니면 다음 필터로 넘기지 않고 예외 던짐
        String category = jwtUtil.getCategory(token);
        log.info("category:'{}'", category);
        if (!category.equals("access")) {
            // 응답
            response.getWriter().print("invalid access token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // --- Authorization 헤더 검증 끝

        // 검증된 토큰을 가진 요청에 일시적인 시큐리티 세션 저장
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        Role auth = role.equals("ROLE_USER") ? Role.USER : Role.ADMIN;

        log.info("role = {}", role);
        log.info("auth = {}", auth);

        /**
         * JWT 토큰의 회원 정보로 하는 데, 나중에 pk값도 있으면 좋을거 같음.
         */
        User user = User.builder()
                .username(username)
                .password("tempPassword")
                .role(auth)
                .build();

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .user(user)
                .build();
        // 스프링 시큐리티 인증 토큰 생성
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("auth Token = {}", authenticationToken);

        filterChain.doFilter(request, response);
    }
}
