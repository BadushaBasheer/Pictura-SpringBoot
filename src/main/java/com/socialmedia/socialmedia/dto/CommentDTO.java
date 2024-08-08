package com.socialmedia.socialmedia.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String text;
    private Long parentCommentId;
    private List<CommentDTO> replies;
    private LocalDateTime createdAt;
}
