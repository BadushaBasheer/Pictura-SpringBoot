package com.socialmedia.socialmedia.dto;

import com.socialmedia.socialmedia.entities.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostDTO {

    private Post post;
    private String caption;
    private UserDTO user;
    private String image;
    private List<CommentDTO> comments;
    private List<UserDTO> liked;
    private LocalDateTime createdAt;

}
