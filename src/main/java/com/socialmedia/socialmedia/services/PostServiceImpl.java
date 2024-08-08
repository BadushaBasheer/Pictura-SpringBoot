package com.socialmedia.socialmedia.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialmedia.socialmedia.entities.Comment;
import com.socialmedia.socialmedia.entities.Image;
import com.socialmedia.socialmedia.entities.Post;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.repositories.CommentRepository;
import com.socialmedia.socialmedia.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);


    private final UserService userService;
    private final PostRepository postRepository;
    private final Cloudinary cloudinary;
    private final CommentRepository commentRepository;


    @Override
    public List<Post> findAllPosts() {
        try {
//            return postRepository.findAll();
            return postRepository.findAllByOrderByCreatedAtDesc();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch all posts", ex);
        }
    }

    @Override
    public Post findPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new NoSuchElementException("Post not found with id : " + postId);
        }
        return post.get();
    }

    @Override
    public List<Post> findPostByUserId(Long userId) {
        return postRepository.findPostByUserId(userId);
    }

//    @Override
//    public Post createNewPost(Post post, Long userId, MultipartFile file) {
//        try {
//            User user = userService.findUserById(userId);
//            if (user != null) {
//                if (file.isEmpty()) {
//                    throw new RuntimeException("File is empty");
//                }
//                String contentType = file.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    throw new RuntimeException("Invalid file type: " + contentType);
//                }
//
//                System.out.println("File received: " + file.getOriginalFilename());
//                System.out.println("Content type: " + contentType);
//                System.out.println("File size: " + file.getSize());
//
//
//                // Upload image to Cloudinary
//                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//                String imageUrl = (String) uploadResult.get("url");
//                String publicId = (String) uploadResult.get("public_id");
//
//                System.out.println("Image url: " + imageUrl);
//                System.out.println("Public ID: " + publicId);
//                // Set image details
//                Image image = new Image();
//                image.setFileName(file.getOriginalFilename());
//                image.setFileType(contentType);
//                image.setUrl(imageUrl);
//                image.setPublicId(publicId);
//
//                // Save the post
//                Post newPost = new Post();
//                newPost.setUser(user);
//                newPost.setImage(image);
//                newPost.setCreatedAt(LocalDateTime.now());
//                return postRepository.save(newPost);
//            } else {
//                throw new UsernameNotFoundException("User not found");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error uploading file", e);
//        } catch (Exception e) {
//            throw new RuntimeException("Error creating post", e);
//        }
//    }


//    @Override
//    public Post createNewPost(Post post, Long userId, MultipartFile file, String caption) {
//        try {
//            User user = userService.findUserById(userId);
//            if (user != null) {
//                if (file.isEmpty()) {
//                    throw new RuntimeException("File is empty");
//                }
//                String contentType = file.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    throw new RuntimeException("Invalid file type: " + contentType);
//                }
//
//                System.out.println("File received: " + file.getOriginalFilename());
//                System.out.println("Content type: " + contentType);
//                System.out.println("File size: " + file.getSize());
//
//
//                // Upload image to Cloudinary
//                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//                String imageUrl = (String) uploadResult.get("url");
//                String publicId = (String) uploadResult.get("public_id");
//
//                System.out.println("Image url: " + imageUrl);
//                System.out.println("Public ID: " + publicId);
//                // Set image details
//                Image image = new Image();
//                image.setFileName(file.getOriginalFilename());
//                image.setFileType(contentType);
//                image.setUrl(imageUrl);
//                image.setPublicId(publicId);
//
//                // Save the post
//                Post newPost = new Post();
//                newPost.setUser(user);
//                newPost.setImage(image);
//                newPost.setCaption(caption);
//                newPost.setCreatedAt(LocalDateTime.now());
//                return postRepository.save(newPost);
//            } else {
//                throw new UsernameNotFoundException("User not found");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error uploading file", e);
//        } catch (Exception e) {
//            throw new RuntimeException("Error creating post", e);
//        }
//    }

    @Override
    public Post createNewPost(Long userId, MultipartFile file, String caption) {
        try {
            User user = userService.findUserById(userId);
            if (user != null) {
                if (file.isEmpty()) {
                    throw new RuntimeException("File is empty");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new RuntimeException("Invalid file type: " + contentType);
                }

                System.out.println("File received: " + file.getOriginalFilename());
                System.out.println("Content type: " + contentType);
                System.out.println("File size: " + file.getSize());


                // Upload image to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("url");
                String publicId = (String) uploadResult.get("public_id");

                System.out.println("Image url: " + imageUrl);
                System.out.println("Public ID: " + publicId);
                // Set image details
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(contentType);
                image.setUrl(imageUrl);
                image.setPublicId(publicId);

                // Save the post
                Post newPost = new Post();
                newPost.setUser(user);
                newPost.setImage(image);
                newPost.setCaption(caption);
                newPost.setCreatedAt(LocalDateTime.now());
                return postRepository.save(newPost);
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating post", e);
        }
    }


    @Override
    public Post savePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        if (post == null || user == null) {
            return null;
        }
        List<Post> savedPosts = user.getSavedPost();

        if (savedPosts.contains(post)) {
            savedPosts.remove(post);
        } else {
            savedPosts.add(post);
        }
        user.setSavedPost(savedPosts);
        userService.updateUser(user);
        return post;
    }

    @Override
    public Post unsavePost(Long postId, Long userId) {
        return savePost(postId, userId); // Reuse savePost for un-saving
    }

    @Override
    public Post likePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        List<User> likedUsers = post.getLiked();
        if (likedUsers == null) {
            likedUsers = new ArrayList<>();
        }
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
        } else {
            likedUsers.add(user);
        }
        post.setLiked(likedUsers);
        return postRepository.save(post);
    }

    @Override
    public Post unlikePost(Long postId, Long userId) {
        return likePost(postId, userId); // Reuse likePost for unliking
    }

    @Override
    public Post commentPost(Long postId, Long userId, String comment) throws Exception {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);

        Comment commentObj = new Comment();
        commentObj.setPost(post);
        commentObj.setUser(user);
        commentObj.setText(comment);
        commentRepository.save(commentObj);

        List<Comment> comments = post.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(commentObj);
        post.setComments(comments);
        return postRepository.save(post);
    }

    @Override
    public Comment replyToComment(Long commentId, Long userId, String reply) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        User user = userService.findUserById(userId);

        Comment replyComment = new Comment();
        replyComment.setParentComment(parentComment);
        replyComment.setUser(user);
        replyComment.setText(reply);
        commentRepository.save(replyComment);

        List<Comment> replies = parentComment.getReplies();
        if (replies == null) {
            replies = new ArrayList<>();
        }
        replies.add(replyComment);
        parentComment.setReplies(replies);
        commentRepository.save(parentComment);

        return replyComment;
    }

    @Override
    public String deletePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        if (!Objects.equals(post.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You can't delete another user's post");
        }
        postRepository.deleteById(postId);
        return "Post with ID " + postId + " has been deleted successfully";
    }


}
