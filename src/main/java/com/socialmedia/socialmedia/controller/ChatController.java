package com.socialmedia.socialmedia.controller;

import com.socialmedia.socialmedia.entities.Chat;
import com.socialmedia.socialmedia.request.GroupChatRequest;
import com.socialmedia.socialmedia.request.SingleChatRequest;
import com.socialmedia.socialmedia.response.ApiResponse;
import com.socialmedia.socialmedia.services.ChatService;
import com.socialmedia.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;

    @PostMapping("/single")
    public ResponseEntity<Chat> createGroupHandler(@RequestBody SingleChatRequest singleChatRequest,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long reqUser =  userService.getUserIdFromUserDetails(userDetails);
        Chat chat = chatService.createChat(reqUser, singleChatRequest.getUserId());
        return new ResponseEntity<>(chat,HttpStatus.OK);
    }

    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupHandler(@RequestBody GroupChatRequest groupChatRequest,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long reqUser =  userService.getUserIdFromUserDetails(userDetails);
        Chat chat = chatService.createGroup(groupChatRequest, reqUser);
        return new ResponseEntity<>(chat,HttpStatus.OK);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> findChatByIdHandler(@PathVariable Long chatId) {
        Chat chat = chatService.findChatById(chatId);
        return new ResponseEntity<>(chat,HttpStatus.OK);
    }


    @PostMapping("/user")
    public ResponseEntity<List<Chat>> findAllChatByUserIdHandler (@AuthenticationPrincipal UserDetails userDetails) {
        Long reqUser =  userService.getUserIdFromUserDetails(userDetails);
        List<Chat> chats = chatService.findAllChatByUserId(reqUser);
        return new ResponseEntity<>(chats,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<ApiResponse> deleteChatByIdHandler(@PathVariable Long chatId, @AuthenticationPrincipal UserDetails userDetails) {
        Long reqUser =  userService.getUserIdFromUserDetails(userDetails);
        chatService.deleteChat(chatId, reqUser);
        ApiResponse response = new ApiResponse("Chat deleted successfully", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
