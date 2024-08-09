package com.socialmedia.socialmedia.controller;

import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.Follower;
import com.socialmedia.socialmedia.entities.Following;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserDTO>> allUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
//
//    @GetMapping("/userDetail")
//    public ResponseEntity<User> authenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
//        return ResponseEntity.ok(currentUser);
//    }

    @GetMapping("/userDetail")
    public ResponseEntity<User> getUserDetails(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findUserByUsername(userDetails.getUsername());
        System.out.println("user: " + user.toString());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public User getAuthenticatedUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getAuthenticatedUser(userDetails);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        try {
            User user = userService.findUserById(userId);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUser(@RequestParam("query") String query) {
        List<User> users = userService.searchUser(query);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        try {
            updatedUser.setId(userId);
            User user = userService.updateUser(updatedUser);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/follow/{userId}")
    public ResponseEntity<User> followUserHandler(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long userId) {
        Long loggedInUserId = userService.getUserIdFromUserDetails(userDetails);
        try {
            User updatedUser = userService.followUser(loggedInUserId, userId);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/block/{blockedId}")
    public ResponseEntity<?> blockUser(@PathVariable("blockedId") Long blockedId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long blockerId = userService.findUserIdByEmail(userDetails.getUsername());
        userService.blockUser(blockerId, blockedId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock/{blockedId}")
    public ResponseEntity<?> unblockUser(@PathVariable("blockedId") Long blockedId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long blockerId = userService.findUserIdByEmail(userDetails.getUsername());
        userService.unblockUser(blockerId, blockedId);
        return ResponseEntity.ok().build();
    }


//    @GetMapping("/followers")
//    public ResponseEntity<List<UserDTO>> getFollowed(@AuthenticationPrincipal UserDetails userDetails) {
//        if (userDetails == null) {
//            return ResponseEntity.badRequest().body(Collections.emptyList());
//        }
//
//        List<Follower> followers;
//        try {
//            followers = userService.followedUsers(userDetails);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
//        }
//
//        List<UserDTO> followerDTOs = followers.stream()
//                .map(follower -> {
//                    User user = follower.getUser();
//                    return UserDTO.builder()
//                            .id(user.getId())
//                            .name(user.getName())
//                            .email(user.getEmail())
//                            .profilePic(user.getProfilePic())
//                            .bio(user.getBio())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(followerDTOs);
//    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserDTO>> getFollowed(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        try {
            List<UserDTO> followerDTOs = userService.getFollowedUserDTOs(userDetails);
            return ResponseEntity.ok(followerDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


//    @GetMapping("/following")
//    public ResponseEntity<List<UserDTO>> getFollowing(@AuthenticationPrincipal UserDetails userDetails) {
//        if (userDetails == null) {
//            return ResponseEntity.badRequest().body(Collections.emptyList());
//        }
//
//        List<Following> following;
//        try {
//            following = userService.followingUsers(userDetails);
//        } catch (Exception e) {
//            // Log the exception and return an appropriate error response
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
//        }
//
//        List<UserDTO> followingDTOs = following.stream()
//                .map(followings -> {
//                    User user = followings.getUser();
//                    return UserDTO.builder()
//                            .id(user.getId())
//                            .name(user.getName())
//                            .email(user.getEmail())
//                            .profilePic(user.getProfilePic())
//                            .bio(user.getBio())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(followingDTOs);
//    }

    @GetMapping("/following")
    public ResponseEntity<List<UserDTO>> getFollowing(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        try {
            List<UserDTO> followingDTOs = userService.getFollowingUserDTOs(userDetails);
            return ResponseEntity.ok(followingDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
