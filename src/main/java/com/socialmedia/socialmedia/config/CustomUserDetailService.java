package com.socialmedia.socialmedia.config;

import com.socialmedia.socialmedia.entities.Admin;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.AdminRepository;
import com.socialmedia.socialmedia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> adminOpt = adminRepository.findAdminByEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("admin" + admin);
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(UserRole.ADMIN.name()));
            return new org.springframework.security.core.userdetails.User(admin.getUsername(), admin.getPassword(), authorities);
        }
        Optional<User> userOpt = userRepository.findUserByEmail(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(UserRole.USER.name()));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        }

        throw new UsernameNotFoundException("Username " + username + " not found");
    }
}

