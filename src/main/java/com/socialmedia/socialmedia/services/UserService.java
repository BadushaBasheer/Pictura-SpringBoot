package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.Follower;
import com.socialmedia.socialmedia.entities.Following;
import com.socialmedia.socialmedia.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

//    List<Follower> followedUsers(UserDetails userDetails);

//    List<Following> followingUsers(UserDetails userDetails);

    User getAuthenticatedUser(UserDetails userDetails);

    User findUserById(Long userId);

    List<User> searchUser(String query);

    User updateUser(User user);

    void deleteUser(Long userId);

    User followUser(Long userId1, Long userId2);

//    List<User> followedUsers(UserDetails userDetails, Follower follower);

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

    List<UserDTO> getFollowedUserDTOs(UserDetails userDetails);

    List<UserDTO> getFollowingUserDTOs(UserDetails userDetails);

}
