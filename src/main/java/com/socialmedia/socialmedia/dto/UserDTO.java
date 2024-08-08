package com.socialmedia.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialmedia.socialmedia.enums.UserRole;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    @Email
    private String email;

    private String bio;

    private String profilePic;

    private boolean isGoogleSignIn;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime modifiedDate;

    private boolean isEnabled;

    private UserRole userRole;

    private String token;

}
