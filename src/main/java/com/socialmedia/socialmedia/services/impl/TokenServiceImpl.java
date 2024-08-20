package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.repositories.OtpRepository;
import com.socialmedia.socialmedia.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final OtpRepository otpRepository;

    @Scheduled(fixedRate = 3600000)
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running scheduled task to remove expired tokens at {}", now);
        otpRepository.deleteExpiredTokens(now);
    }

}