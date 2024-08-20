package com.socialmedia.socialmedia.controller;

import com.socialmedia.socialmedia.entities.Chat;
import com.socialmedia.socialmedia.entities.Message;
import com.socialmedia.socialmedia.request.SendMessageRequest;
import com.socialmedia.socialmedia.response.ApiResponse;
import com.socialmedia.socialmedia.services.ChatService;
import com.socialmedia.socialmedia.services.MessageService;
import com.socialmedia.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;
    private final ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<Message> sendMessageHandler(@RequestBody SendMessageRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        Long senderId = userService.getUserIdFromUserDetails(userDetails);
        request.setUserId(senderId);
        Message message = messageService.sendMessage(request);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Message>> sendMessageHandler(@PathVariable Long chatId, @AuthenticationPrincipal UserDetails userDetails) {
        Long senderId = userService.getUserIdFromUserDetails(userDetails);
        List<Message> message = messageService.getChatsMessages(chatId, senderId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable Long messageId, @AuthenticationPrincipal UserDetails userDetails) {
        Long senderId = userService.getUserIdFromUserDetails(userDetails);
        messageService.deleteMessageById(messageId, senderId);
        ApiResponse response = new ApiResponse("Message deleted successfully", false);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/chat/getOrCreate/{selectedUserId}")
    public ResponseEntity<Long> getOrCreateChat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long selectedUserId) {
        Long senderId = userService.getUserIdFromUserDetails(userDetails);
        Chat chat = chatService.findOrCreateChat(senderId, selectedUserId);
        return new ResponseEntity<>(chat.getId(), HttpStatus.OK);
    }

}
