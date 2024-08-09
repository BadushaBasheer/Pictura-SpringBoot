package com.socialmedia.socialmedia.dto;

import com.socialmedia.socialmedia.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String profilePic;
    private String bio;
    private boolean enabled;
    private UserRole userRole;
    private boolean accountLocked;
    private boolean googleSignIn;
    private boolean isBlockedByAdmin;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<FollowerDTO> followers;
    private List<FollowingDTO> following;

    private String token;

}
