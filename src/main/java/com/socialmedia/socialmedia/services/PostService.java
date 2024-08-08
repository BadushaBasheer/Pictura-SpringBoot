package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.entities.Comment;
import com.socialmedia.socialmedia.entities.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface PostService {

    List<Post> findAllPosts();

    Post findPostById(Long postId);

    List<Post> findPostByUserId(Long userId);

//    Post createNewPost(Post post, Long userId, MultipartFile file) throws IOException;

//    Post createNewPost(Post post, Long userId, MultipartFile file, String caption);
    Post createNewPost(Long userId, MultipartFile file, String caption);

    Post savePost(Long postId, Long userId);

    Post unsavePost(Long postId, Long userId);

    Post likePost(Long postId, Long userId);

    Post unlikePost(Long postId, Long userId);

    Post commentPost(Long postId, Long userId, String comment) throws Exception;

    Comment replyToComment(Long commentId, Long userId, String reply);

    String deletePost(Long postId, Long userId);

}
