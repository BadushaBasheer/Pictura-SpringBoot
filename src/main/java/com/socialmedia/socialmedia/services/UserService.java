package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    User findUserById(Long userId);

    List<User> searchUser(String query);

    User updateUser(User user);

    void deleteUser(Long userId);

    User followUser(Long userId1, Long userId2);

    void blockUser(Long blockerId, Long blockedId);

    void unblockUser(Long blockerId, Long blockedId);

    boolean isBlockedBy(Long userId, Long otherUserId);

    boolean hasBlocked(Long userId, Long otherUserId);

    boolean isAdminBlockedUser(Long userId);

    Long findUserIdByEmail(String email);

    void unblockUserByAdmin(Long userId);

    void blockUserByAdmin(Long userId);

    Long getUserIdFromUserDetails(UserDetails userDetails);

    User findUserByUsername(String username);
}
