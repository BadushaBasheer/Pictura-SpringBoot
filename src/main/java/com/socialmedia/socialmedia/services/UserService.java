package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    User unfollowUser(Long followerId, Long followedId);

    boolean checkIfFollowed(Long currentUserId, Long userIdToCheck);

    User getAuthenticatedUser(UserDetails userDetails);

    User findUserById(Long userId);

    List<UserDTO> getSuggestionUser(Long currentUserId);

    List<User> searchUser(String query);

    User updateUser(User user);

    User updateUserProfile(Long userId, User userUpdates, MultipartFile profilePic, MultipartFile backgroundImage) throws IOException;

    void deleteUser(Long userId);

    List<UserDTO> getFollowedUserDTOs(UserDetails userDetails);

    List<UserDTO> getFollowingUserDTOs(UserDetails userDetails);

    User followUser(Long followerId, Long followedId);

    boolean blockUser(Long blockerId, Long blockedId);

    void unblockUser(Long blockerId, Long blockedId);

    boolean isBlockedBy(Long userId, Long otherUserId);

    boolean hasBlocked(Long userId, Long otherUserId);

    Long findUserIdByEmail(String email);

    Long getUserIdFromUserDetails(UserDetails userDetails);

    User findUserByUsername(String username);

}
