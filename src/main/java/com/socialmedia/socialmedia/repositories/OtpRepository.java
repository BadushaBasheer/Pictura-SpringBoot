package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Otp;
import com.socialmedia.socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByOtp(String otp);

    @Transactional
    @Modifying
    @Query("DELETE FROM Otp t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);

    Optional<Otp> findByUser(User user);

    Optional<Otp> findFirstByUserOrderByCreatedAtDesc(User user);
}
