package com.socialmedia.socialmedia.dto;

import com.socialmedia.socialmedia.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private UserRole userRole;
    private String profilePic;
    private String backgroundImage;
    private boolean enabled;
    private boolean accountLocked;
    private boolean isBlockedByAdmin;
    private boolean isGoogleSignIn;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Set<Long> followerIds;
    private Set<Long> followingIds;

    private String token;

}
