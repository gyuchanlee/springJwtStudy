package com.dodo.jwtreactspring.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 상태 코드 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 원하는 커스텀 메시지를 작성합니다.
        String errorMessage = "다시 로그인 해주세요!!!";

        // JSON 형식으로 에러 메시지를 반환합니다.
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}
