package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.dto.FollowerDTO;
import com.socialmedia.socialmedia.dto.FollowingDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.Follower;
import com.socialmedia.socialmedia.entities.Following;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.entities.UserBlock;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.FollowerRepository;
import com.socialmedia.socialmedia.repositories.FollowingRepository;
import com.socialmedia.socialmedia.repositories.UserBlockRepository;
import com.socialmedia.socialmedia.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final FollowerRepository followerRepository;

    private final FollowingRepository followingRepository;

    private final UserBlockRepository userBlockRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Long getUserIdFromUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user.get().getId();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

//-------------------------------------------------------------------------------------------------------

//    @Override
//    public List<Follower> followedUsers(UserDetails userDetails) {
//        User authenticatedUser = getAuthenticatedUser(userDetails);
//        Long userId = authenticatedUser.getId();
//        List<Follower> followedUsers = followerRepository.findUserByFollowerId(userId);
//        if (followedUsers == null || followedUsers.isEmpty()) {
//            throw new EntityNotFoundException("No users followed by user with id: " + userId);
//        }
//        return followedUsers;
//    }

    @Override
    public List<UserDTO> getFollowedUserDTOs(UserDetails userDetails) {
        User authenticatedUser = getAuthenticatedUser(userDetails);
        Long userId = authenticatedUser.getId();
        List<Follower> followers = followerRepository.findUserByFollowerId(userId);

        if (followers == null || followers.isEmpty()) {
            throw new EntityNotFoundException("No users followed by user with id: " + userId);
        }

        return followers.stream()
                .map(follower -> {
                    User user = follower.getUser();
                    return UserDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .profilePic(user.getProfilePic())
                            .bio(user.getBio())
                            .build();
                })
                .collect(Collectors.toList());
    }


//    @Override
//    public List<Following> followingUsers(UserDetails userDetails) {
//        User authenticatedUser = getAuthenticatedUser(userDetails);
//        Long userId = authenticatedUser.getId();
//        List<Following> followingUsers = followingRepository.findUserByFollowingId(userId);
//        if (followingUsers == null || followingUsers.isEmpty()) {
//            throw new EntityNotFoundException("No users followed by user with id: " + userId);
//        }
//        return followingUsers;
//    }

    @Override
    public List<UserDTO> getFollowingUserDTOs(UserDetails userDetails) {
        User authenticatedUser = getAuthenticatedUser(userDetails);
        Long userId = authenticatedUser.getId();
        List<Following> followingUsers = followingRepository.findUserByFollowingId(userId);

        if (followingUsers == null || followingUsers.isEmpty()) {
            throw new EntityNotFoundException("No users followed by user with id: " + userId);
        }

        return followingUsers.stream()
                .map(following -> {
                    User user = following.getUser();
                    return UserDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .profilePic(user.getProfilePic())
                            .bio(user.getBio())
                            .build();
                })
                .collect(Collectors.toList());
    }




//-------------------------------------------------------------------------------------------------------


    @Override
    public User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new SecurityException("User is not authenticated");
        }
        Long id = getUserIdFromUserDetails(userDetails);
        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        return user;
    }


    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("User with ID: " + userId + " is an admin and access is restricted.");
        }
        return user;
    }


    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user) {
        List<FollowerDTO> followers = user.getFollowers().stream()
                .map(follower -> FollowerDTO.builder()
                        .id(follower.getId())
                        .name(follower.getUser().getName())
                        .email(follower.getUser().getEmail())
                        .profilePic(follower.getUser().getProfilePic())
                        .build())
                .collect(Collectors.toList());

        List<FollowingDTO> following = user.getFollowing().stream()
                .map(followingUser -> FollowingDTO.builder()
                        .id(followingUser.getId())
                        .name(followingUser.getUser().getName())
                        .email(followingUser.getUser().getEmail())
                        .profilePic(followingUser.getUser().getProfilePic())
                        .build())
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .bio(user.getBio())
                .userRole(user.getUserRole())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .googleSignIn(user.isGoogleSignIn())
                .isBlockedByAdmin(user.isBlockedByAdmin())
                .createdDate(user.getCreatedDate())
                .followers(followers)
                .following(following)
                .build();
    }


    @Override
    public List<User> searchUser(String query) {
        return userRepository.findByNameContainingOrEmailContaining(query, query).stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findUserByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + user.getEmail()));
        return updateExistingUser(user, existingUser);
    }

    private User updateExistingUser(User user, User existingUser) {
        // Only update email if it is different
        if (!existingUser.getEmail().equals(user.getUsername())) {
            existingUser.setEmail(user.getEmail());
        }

        // Update password only if it's different
        if (!bCryptPasswordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        // Save and return the updated user
        return userRepository.save(existingUser);
    }


    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot delete an admin user.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User followUser(Long userId1, Long userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);

        // Check if already following
        boolean isAlreadyFollowing = user1.getFollowing().stream()
                .anyMatch(following -> following.getFollowingId().equals(user2.getId()));

        if (isAlreadyFollowing) {
            throw new IllegalStateException("User is already following this user.");
        }

        Following following = new Following();
        following.setUser(user1);
        following.setFollowingId(user2.getId());

        Follower follower = new Follower();
        follower.setUser(user2);
        follower.setFollowerId(user1.getId());

        user1.getFollowing().add(following);
        user2.getFollowers().add(follower);

        userRepository.save(user1);
        userRepository.save(user2);

        return user1;
    }



    //-------------------------------BLOCK / UNBLOCK SECTION--------------------------------------
    @Override
    public void blockUser(Long blockerId, Long blockedId) {
        User blocker = userRepository.findById(blockerId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User blocked = userRepository.findById(blockedId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserBlock userBlock = new UserBlock();
        userBlock.setBlocker(blocker);
        userBlock.setBlocked(blocked);

        userBlockRepository.save(userBlock);
    }

    @Override
    public void unblockUser(Long blockerId, Long blockedId) {
        UserBlock userBlock = userBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new EntityNotFoundException("Block record not found"));
        userBlockRepository.delete(userBlock);
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

    @Override
    public boolean isBlockedBy(Long userId, Long otherUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getBlockedByUsers().stream()
                .anyMatch(block -> block.getBlocker().getId().equals(otherUserId));
    }

    @Override
    public boolean hasBlocked(Long userId, Long otherUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getBlockedUsers().stream()
                .anyMatch(block -> block.getBlocked().getId().equals(otherUserId));
    }

    @Override
    public boolean isAdminBlockedUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.isBlockedByAdmin();
    }

    @Override
    public Long findUserIdByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getId();
    }

}
