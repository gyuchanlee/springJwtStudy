package com.dodo.jwtreactspring.controller;

import com.dodo.jwtreactspring.jwt.JWTUtil;
import com.dodo.jwtreactspring.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    // refresh token으로 access token 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        return reissueService.reissue(request, response);
    }
}
