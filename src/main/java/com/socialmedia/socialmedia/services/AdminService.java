package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.entities.User;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers();

    User findUserById(Long userId);

    List<User> searchUser(String query);

    void deleteUser(Long userId);

    boolean isAdminBlockedUser(Long userId);

    void blockUserByAdmin(Long userId);

    void unblockUserByAdmin(Long userId);
}
