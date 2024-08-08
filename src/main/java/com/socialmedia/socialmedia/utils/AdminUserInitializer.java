package com.socialmedia.socialmedia.utils;

import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;
    @PostConstruct
    public void createAnAdminAccount() {
        Optional<User> isAdmin = userRepository.findUserByUserRole(UserRole.ADMIN);
        if (isAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setUserRole(UserRole.ADMIN);
            admin.setAccountLocked(false);
            admin.setEnabled(true);
            admin.setCreatedDate(LocalDateTime.now());
            userRepository.save(admin);
            log.info("Admin created successfully!.");
        } else {
            log.info("Admin already exists!.");
        }
    }
}
