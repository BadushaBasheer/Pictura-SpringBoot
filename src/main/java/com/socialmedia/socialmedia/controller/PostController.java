package com.socialmedia.socialmedia.controller;

import com.socialmedia.socialmedia.entities.Post;
import com.socialmedia.socialmedia.response.ApiResponse;
import com.socialmedia.socialmedia.services.PostService;
import com.socialmedia.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);


    private final PostService postService;

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Post>> findAllPost() {
        try {
            List<Post> posts = postService.findAllPosts();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> findUsersPost(@PathVariable Long userId) {
        List<Post> posts = postService.findPostByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> findPostByIdHandler(@PathVariable Long postId) {
        Post post = postService.findPostById(postId);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }

    @PutMapping("/save/{postId}/user/{userId}")
    public ResponseEntity<Post> savePostByIdHandler(@PathVariable Long postId, @PathVariable Long userId) throws Exception {
        Post post = postService.savePost(postId, userId);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }

    @PutMapping("/like/{postId}/user/{userId}")
    public ResponseEntity<Post> likePostByIdHandler(@PathVariable Long postId, @PathVariable Long userId) throws Exception {
        Post post = postService.likePost(postId, userId);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }


    //----------------------------------------CREATE POST STARTS-----------------------------------------
//    @PostMapping("/post")
//    public ResponseEntity<Post> createPost(@RequestPart("post") String postJson,
//                                           @RequestPart("file") MultipartFile file,
//                                           @AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            logger.info("Received create post request with postJson: {}", postJson);
//            logger.info("Received file: {}", file.getOriginalFilename());
//            logger.info("File size: {} bytes", file.getSize());
//
//            // Convert JSON string to PostDTO object
//            ObjectMapper objectMapper = new ObjectMapper();
//            PostDTO postDTO = objectMapper.readValue(postJson, PostDTO.class);
//
//            Post post = postDTO.getPost();
//            String caption = postDTO.getCaption();
//            Long userId = userService.getUserIdFromUserDetails(userDetails);
//
//            // Check if file size exceeds the database column limit
//            long maxFileSize = 20 * 1024 * 1024; // 16MB
//            if (file.getSize() > maxFileSize) {
//                logger.error("File size exceeds the maximum limit of {} bytes", maxFileSize);
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//            }
//            Post createdPost = postService.createNewPost(post, userId, file, caption);
//            logger.info("Post created successfully with ID: {}", createdPost.getId());
//
//            return ResponseEntity.ok(createdPost);
//        } catch (IOException e) {
//            logger.error("IOException occurred: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } catch (Exception e) {
//            logger.error("Exception occurred: {}", e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PostMapping("/post")
    public ResponseEntity<Post> createPost(
            @RequestParam("file") MultipartFile file,
            @RequestParam("caption") String caption,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            logger.info("Received file: {}", file.getOriginalFilename());
            logger.info("File size: {} bytes", file.getSize());

            Long userId = userService.getUserIdFromUserDetails(userDetails);

            // Check if file size exceeds the database column limit
            long maxFileSize = 10 * 1024 * 1024;
            if (file.getSize() > maxFileSize) {
                logger.error("File size exceeds the maximum limit of {} bytes", maxFileSize);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Post createdPost = postService.createNewPost(userId, file, caption);
            logger.info("Post created successfully with ID: {}", createdPost.getId());

            return ResponseEntity.ok(createdPost);
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PostMapping("/post")
//    public ResponseEntity<Post> createPost(@RequestPart("post") String postJson,
//                                           @RequestPart("file") MultipartFile file,
//                                           @AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            logger.info("Received create post request with postJson: {}", postJson);
//            logger.info("Received file: {}", file.getOriginalFilename());
//            logger.info("File size: {} bytes", file.getSize());
//
//            // Convert JSON string to PostDTO object
//            ObjectMapper objectMapper = new ObjectMapper();
//            PostDTO postDTO = objectMapper.readValue(postJson, PostDTO.class);
//
//            Post post = postDTO.getPost();
//            String caption = postDTO.getCaption();
//            Long userId = userService.getUserIdFromUserDetails(userDetails);
//
//            // Check if file size exceeds the database column limit
//            long maxFileSize = 16 * 1024 * 1024; // 16MB
//            if (file.getSize() > maxFileSize) {
//                logger.error("File size exceeds the maximum limit of {} bytes", maxFileSize);
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//            }
//
//            Post createdPost = postService.createNewPost(post, userId, file, caption); // Pass caption
//            logger.info("Post created successfully with ID: {}", createdPost.getId());
//
//            return ResponseEntity.ok(createdPost);
//        } catch (IOException e) {
//            logger.error("IOException occurred: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } catch (Exception e) {
//            logger.error("Exception occurred: {}", e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }


    //----------------------------------------CREATE POST ENDS-----------------------------------------

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = userService.getUserIdFromUserDetails(userDetails);
            String message = postService.deletePost(postId, userId);
            ApiResponse response = new ApiResponse(message, true);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            String errorMessage = "Failed to delete the post: " + ex.getMessage();
            ApiResponse errorResponse = new ApiResponse(errorMessage, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
