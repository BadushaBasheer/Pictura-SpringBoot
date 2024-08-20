package com.socialmedia.socialmedia.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.Follow;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.entities.UserBlock;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.FollowRepository;
import com.socialmedia.socialmedia.repositories.UserBlockRepository;
import com.socialmedia.socialmedia.repositories.UserRepository;
import com.socialmedia.socialmedia.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Cloudinary cloudinary;

    private final UserRepository userRepository;

    private final FollowRepository followRepository;

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

    @Override
    public List<UserDTO> getFollowedUserDTOs(UserDetails userDetails) {
        User authenticatedUser = getAuthenticatedUser(userDetails);
        Long userId = authenticatedUser.getId();
        List<Follow> followedUsers = followRepository.findByFollowerId(userId);

        if (followedUsers == null || followedUsers.isEmpty()) {
            throw new EntityNotFoundException("No users followed by user with id: " + userId);
        }

        return followedUsers.stream()
                .map(follow -> {
                    User followedUser = follow.getFollowed();
                    return UserDTO.builder()
                            .id(followedUser.getId())
                            .name(followedUser.getName())
                            .email(followedUser.getEmail())
                            .profilePic(followedUser.getProfilePic())
                            .bio(followedUser.getBio())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFollowingUserDTOs(UserDetails userDetails) {
        User authenticatedUser = getAuthenticatedUser(userDetails);
        Long userId = authenticatedUser.getId();
        List<Follow> followingUsers = followRepository.findByFollowedId(userId);

        if (followingUsers == null || followingUsers.isEmpty()) {
            throw new EntityNotFoundException("No users are following the user with id: " + userId);
        }

        return followingUsers.stream()
                .map(follow -> {
                    User followerUser = follow.getFollower();
                    return UserDTO.builder()
                            .id(followerUser.getId())
                            .name(followerUser.getName())
                            .email(followerUser.getEmail())
                            .profilePic(followerUser.getProfilePic())
                            .bio(followerUser.getBio())
                            .build();
                })
                .collect(Collectors.toList());
    }

//    @Override
//    public User followUser(Long followerId, Long followedId) {
//        User follower = findUserById(followerId);
//        User followed = findUserById(followedId);
//
//        // Check if already following
//        boolean isAlreadyFollowing = follower.getFollowing().stream()
//                .anyMatch(follow -> follow.getFollowed().getId().equals(followedId));
//
//        if (isAlreadyFollowing) {
//            throw new IllegalStateException("User is already following this user.");
//        }
//
//        // Create the follow relationship
//        Follow follow = new Follow();
//        follow.setFollower(follower);
//        follow.setFollowed(followed);
//
//        // Add the follow relationship to both users
//        follower.getFollowing().add(follow);
//        followed.getFollowers().add(follow);
//
//        // Save the follow entity (if using cascading, saving follower might suffice)
//        followRepository.save(follow);
//
//        // Save the changes
//        userRepository.save(follower);
//        userRepository.save(followed);
//
//        return follower;
//    }

    public User followUser(Long followerId, Long followedId) {
        User follower = findUserById(followerId);
        User followed = findUserById(followedId);

        // Ensure the users are found
        if (follower == null || followed == null) {
            throw new EntityNotFoundException("User not found.");
        }

        // Check if already following
        boolean isAlreadyFollowing = follower.getFollowing().stream()
                .anyMatch(follow -> follow.getFollowed().getId().equals(followedId));

        if (isAlreadyFollowing) {
            throw new IllegalStateException("User is already following this user.");
        }

        // Create the follow relationship
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);

        // Add the follow relationship to both users
        follower.getFollowing().add(follow);
        followed.getFollowers().add(follow);

        // Save the follow entity (cascading save might be sufficient)
        try {
            followRepository.save(follow);
            userRepository.save(follower);
            userRepository.save(followed);
        } catch (Exception e) {
            log.error("Error saving follow relationship", e);
            throw e;  // Re-throw the exception to be caught in the controller
        }

        return follower;
    }


    @Override
    public User unfollowUser(Long followerId, Long followedId) {
        User follower = findUserById(followerId);
        User followed = findUserById(followedId);

        // Find the follow relationship
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
                .orElseThrow(() -> new IllegalStateException("User is not following this user."));

        // Remove the follow relationship from both users
        follower.getFollowing().remove(follow);
        followed.getFollowers().remove(follow);

        // Delete the follow entity from the database
        followRepository.delete(follow);

        // Save the changes to the users (if necessary)
        userRepository.save(follower);
        userRepository.save(followed);

        return follower;
}


    @Override
    public boolean checkIfFollowed(Long currentUserId, Long userIdToCheck) {
        return followRepository.existsByFollowerIdAndFollowedId(currentUserId, userIdToCheck);
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
        Set<Long> followerIds = user.getFollowers().stream()
                .map(follow -> follow.getFollower().getId())
                .collect(Collectors.toSet());

        Set<Long> followingIds = user.getFollowing().stream()
                .map(follow -> follow.getFollowed().getId())
                .collect(Collectors.toSet());

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .bio(user.getBio())
                .userRole(user.getUserRole())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .isGoogleSignIn(user.isGoogleSignIn())
                .isBlockedByAdmin(user.isBlockedByAdmin())
                .createdDate(user.getCreatedDate())
                .followerIds(followerIds)
                .followingIds(followingIds)
                .build();
    }

    @Override
    public List<UserDTO> getSuggestionUser(Long currentUserId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .filter(user -> user.getFollowing().stream()
                        .noneMatch(follow -> follow.getFollowed().getId().equals(currentUserId)))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<User> searchUser(String query) {
        return userRepository.findByNameContainingOrEmailContaining(query, query).stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .collect(Collectors.toList());
    }


    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findUserByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + user.getEmail()));

        updateUserFields(user, existingUser);

        return userRepository.save(existingUser);
    }

    @Override
    public User updateUserProfile(Long userId, User userUpdates, MultipartFile profilePic, MultipartFile backgroundImage) throws IOException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if (profilePic != null && !profilePic.isEmpty()) {
            String profilePicUrl = uploadImageToCloudinary(profilePic);
            existingUser.setProfilePic(profilePicUrl);
        }

        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            String backgroundImageUrl = uploadImageToCloudinary(backgroundImage);
            existingUser.setBackgroundImage(backgroundImageUrl);
        }

        updateUserFields(userUpdates, existingUser);

        return userRepository.save(existingUser);
    }


    // Helper method to update user fields
    private void updateUserFields(User user, User existingUser) {
        if (user.getName() != null && !user.getName().equals(existingUser.getName())) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null && !bCryptPasswordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        if (user.getBio() != null && !user.getBio().equals(existingUser.getBio())) {
            existingUser.setBio(user.getBio());
        }
    }

    // Helper method to upload images to Cloudinary
    private String uploadImageToCloudinary(MultipartFile image) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("resource_type", "image"));
        return (String) uploadResult.get("secure_url");
    }


    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.deleteById(userId);
    }


    //-------------------------------BLOCK / UNBLOCK SECTION--------------------------------------
    @Override
    public boolean blockUser(Long blockerId, Long blockedId) {
        User blocker = userRepository.findById(blockerId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User blocked = userRepository.findById(blockedId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserBlock userBlock = new UserBlock();
        userBlock.setBlocker(blocker);
        userBlock.setBlocked(blocked);

        userBlockRepository.save(userBlock);
        return true;
    }

    @Override
    public void unblockUser(Long blockerId, Long blockedId) {
        UserBlock userBlock = userBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new EntityNotFoundException("Block record not found"));
        userBlockRepository.delete(userBlock);
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
    public Long findUserIdByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getId();
    }

}
