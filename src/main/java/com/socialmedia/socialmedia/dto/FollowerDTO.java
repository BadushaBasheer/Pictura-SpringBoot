package com.socialmedia.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerDTO {
    private Long id; // ID of the follower
    private String name;
    private String email;
    private String profilePic;
}

