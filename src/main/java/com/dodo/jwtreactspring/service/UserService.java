package com.dodo.jwtreactspring.service;

import com.dodo.jwtreactspring.dto.UserJoinDto;
import com.dodo.jwtreactspring.entity.Role;
import com.dodo.jwtreactspring.entity.User;
import com.dodo.jwtreactspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public boolean join(UserJoinDto dto) {

        // 중복 체크
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.info("username 이 이미 존재합니다");
            return false;
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        return saved.getId() != null;
    }
}
