package com.dodo.jwtreactspring.controller;

import com.dodo.jwtreactspring.dto.CustomUserDetails;
import com.dodo.jwtreactspring.dto.UserJoinDto;
import com.dodo.jwtreactspring.entity.User;
import com.dodo.jwtreactspring.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public List<User> getAllUsers() {
        return null;
    }

    @GetMapping("/info")
    public Map<String, Object> getUserInfo() {

        log.info("auth = {}", SecurityContextHolder.getContext().getAuthentication());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("username = {}", username);
        log.info("userDetails = {}", userDetails);
        GrantedAuthority auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next();
        log.info("auth = {}", auth);

        Map<String, Object> map = new HashMap<>();

        map.put("username", username);
        map.put("role", auth.getAuthority());
        map.put("userInfo", userDetails.getUser());

        return map;
    }

    @PostMapping("")
    public ResponseEntity<?> join(@RequestBody UserJoinDto dto) {

        boolean join = userService.join(dto);

        return new ResponseEntity<>(join, HttpStatus.OK);
    }
}