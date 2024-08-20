package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.entities.Chat;
import com.socialmedia.socialmedia.entities.Message;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.repositories.MessageRepository;
import com.socialmedia.socialmedia.request.SendMessageRequest;
import com.socialmedia.socialmedia.services.ChatService;
import com.socialmedia.socialmedia.services.MessageService;
import com.socialmedia.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final UserService userService;

    private final ChatService chatService;



    @Override
    public Message sendMessage(SendMessageRequest request) {
        User user = userService.findUserById(request.getUserId());
        Chat chat = chatService.findChatById(request.getChatId());
        Message message = new Message();
        message.setChat(chat);
        message.setUser(user);
        message.setContent(request.getContent());
        message.setTimeStamp(LocalDateTime.now());
        messageRepository.save(message);
        return message;
    }



    @Override
    public List<Message> getChatsMessages(Long chatId, Long reqUser) {
        Chat chat = chatService.findChatById(chatId);
        List<Message> messages = messageRepository.findByChatId(chatId);
        boolean userExistsInChat = chat.getUsers().stream()
                .anyMatch(user -> user.getId().equals(reqUser));
        if (!userExistsInChat) {
            throw new RuntimeException("You do not own this chat: " + chatId);
        }
        return messages;
    }


    @Override
    public Message findMessageById(Long messageId) {
        Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new RuntimeException("Message not found" + messageId);
    }

    @Override
    public void deleteMessageById(Long messageId, Long reqUser) {
        Message message = findMessageById(messageId);
        if (!message.getUser().getId().equals(reqUser)) {
            throw new RuntimeException("You do not have permission to delete this message.");
        }
        messageRepository.delete(message);
    }

}
