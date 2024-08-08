package com.socialmedia.socialmedia.config;

import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Retrieve user by email
        Optional<User> userOptional = this.userRepository.findFirstByEmail(email);

        // If user not found, throw exception
        User user = userOptional.orElseThrow(() -> {
            System.out.println("User not found");
            return new UsernameNotFoundException("No user found with email: " + email);
        });

        // Return the UserDetails implementation
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}

