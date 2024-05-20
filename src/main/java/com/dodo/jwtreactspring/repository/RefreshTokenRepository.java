package com.dodo.jwtreactspring.repository;

import com.dodo.jwtreactspring.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 리프레시 토큰으로 검색
    RefreshToken findByRefreshToken(String refreshToken);
    // 존재 여부
    Boolean existsByRefreshToken(String refreshToken);
    // 삭제
    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
