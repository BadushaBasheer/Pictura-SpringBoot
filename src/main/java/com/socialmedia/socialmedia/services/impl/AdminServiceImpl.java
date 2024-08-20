package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.UserRepository;
import com.socialmedia.socialmedia.services.AdminService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
        return user;
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepository.findByNameContainingOrEmailContaining(query, query).stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        } else
            userRepository.delete(user);
    }

    @Override
    public boolean isAdminBlockedUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.isBlockedByAdmin();
    }

    @Override
    public void blockUserByAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBlockedByAdmin(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUserByAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBlockedByAdmin(false);
        userRepository.save(user);
    }


}
