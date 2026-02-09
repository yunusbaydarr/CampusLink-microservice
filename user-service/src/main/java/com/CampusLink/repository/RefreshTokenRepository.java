package com.CampusLink.repository;

import com.CampusLink.entity.RefreshToken;
import com.CampusLink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByToken(String token);

    @Transactional
    void deleteAllByExpiryDateBefore(java.time.Instant now);
}