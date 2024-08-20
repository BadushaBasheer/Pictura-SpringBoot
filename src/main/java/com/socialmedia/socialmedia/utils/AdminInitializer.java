package com.socialmedia.socialmedia.utils;

import com.socialmedia.socialmedia.entities.Admin;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.AdminRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @PostConstruct
    public void initializeAdmin() {
        if (adminRepository.findAdminByEmail(adminEmail).isEmpty()) {
            String encodedPassword = passwordEncoder.encode(adminPassword);
            Admin newAdmin = new Admin( adminEmail, encodedPassword, UserRole.ADMIN);
            adminRepository.save(newAdmin);
        } else {
            log.info("Admin already exists");
        }
    }
}
