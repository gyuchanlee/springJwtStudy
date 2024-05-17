package com.dodo.jwtreactspring.repository;

import com.dodo.jwtreactspring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 회원 이름으로 찾기
    Optional<User> findByUsername(String username);

    // 동일한 username 확인
    boolean existsByUsername(String username);
}
