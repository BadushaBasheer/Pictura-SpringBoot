package com.socialmedia.socialmedia.controller;

import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getUser")
    public ResponseEntity<List<User>> searchUser(@RequestParam("query") String query) {
        List<User> users = userService.searchUser(query);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateUser")
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with the id: " + userId);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user");
        }
    }

    @PostMapping("/block-user/{id}")
    public ResponseEntity<?> blockUserByAdmin(@PathVariable("id") Long id) {
        userService.blockUserByAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock-user/{id}")
    public ResponseEntity<?> unblockUserByAdmin(@PathVariable("id") Long id) {
        userService.unblockUserByAdmin(id);
        return ResponseEntity.ok().build();
    }
}