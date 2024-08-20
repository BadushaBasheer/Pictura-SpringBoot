package com.socialmedia.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowDTO {

    private Long id;
    private Long followedId;
    private String followedUsername;
    private Long followerId;
    private String followerUsername;
}
