package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.entities.Chat;
import com.socialmedia.socialmedia.request.GroupChatRequest;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.repositories.ChatRepository;
import com.socialmedia.socialmedia.services.ChatService;
import com.socialmedia.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final UserService userService;


//    @Override
//    public Chat createChat(Long reqUser, Long userId2) {
//
//        User user = userService.findUserById(userId2);
//        Chat isChatExists = chatRepository.findSingleChatByUserIds(user, reqUser);
//        if (isChatExists != null) {
//            return isChatExists;
//        }
//        Chat chat = new Chat();
//        chat.setCreatedBy(reqUser);
//        chat.getUsers().add(user);
//        chat.getUsers().add(reqUser);
//        chat.setGroup(false);
//        chatRepository.save(chat);
//        return chat;
//    }

    @Override
    public Chat createChat(Long reqUser, Long userId2) {
        User createdByUser = userService.findUserById(reqUser);
        User user2 = userService.findUserById(userId2);

        Chat isChatExists = chatRepository.findSingleChatByUserIds(createdByUser, user2); // Check if chat already exists
        if (isChatExists != null) {
            return isChatExists;
        }

        Chat chat = new Chat();
        chat.setCreatedBy(createdByUser);
        chat.getUsers().add(createdByUser);
        chat.getUsers().add(user2);
        chat.setGroup(false);
        chatRepository.save(chat);
        return chat;
    }

//    @Override
//    public Chat findOrCreateChat(Long currentUserId, Long selectedUserId) {
//        Optional<Chat> existingChat = chatRepository.findByUsers(currentUserId, selectedUserId);
//        if (existingChat.isPresent()) {
//            return existingChat.get();
//        }
//        User currentUser = userService.findUserById(currentUserId);
//        User selectedUser = userService.findUserById(selectedUserId);
//        Chat newChat = new Chat();
//        newChat.getUsers().add(currentUser);
//        newChat.getUsers().add(selectedUser);
//        chatRepository.save(newChat);
//        return newChat;
//    }

    @Override
    public Chat findOrCreateChat(Long currentUserId, Long selectedUserId) {
        User currentUser = userService.findUserById(currentUserId);
        User selectedUser = userService.findUserById(selectedUserId);

        Optional<Chat> existingChat = chatRepository.findByUsers(currentUser, selectedUser);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        Chat newChat = new Chat();
        newChat.getUsers().add(currentUser);
        newChat.getUsers().add(selectedUser);
        chatRepository.save(newChat);
        return newChat;
    }



    @Override
    public Chat findChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Can't find chat with id " + chatId + " exists"));
    }

    @Override
    public List<Chat> findAllChatByUserId(Long userId) {
        User user = userService.findUserById(userId);
        return chatRepository.findChatByUserId(user.getId());
    }

//    @Override
//    public Chat createGroup(GroupChatRequest req, Long reqUser) {
//        Chat group = new Chat();
//        group.setGroup(true);
//        group.setChat_image(req.getChat_image());
//        group.setChat_name(req.getChat_name());
//        group.setCreatedBy(reqUser);
//        group.getAdmins().add(reqUser);
//        for (Long userId : req.getUserIds()) {
//            User user = userService.findUserById(userId);
//            group.getUsers().add(user);
//        }
//        return group;
//    }

//    @Override
//    public Chat addUserToGroup(Long userId, Long chatId, User reqUser) {
//        Optional<Chat> opt = chatRepository.findById(chatId);
//        User user = userService.findUserById(userId);
//        if (opt.isPresent()) {
//            Chat chat = opt.get();
//            if (chat.getAdmins().contains(reqUser)) {
//                chat.getUsers().add(user);
//                return chat;
//            } else {
//                throw new IllegalArgumentException("You are not allowed to add this chat");
//            }
//        }
//        throw new IllegalArgumentException("No chat with id " + chatId + " exists");
//    }

    @Override
    public Chat createGroup(GroupChatRequest req, Long reqUser) {
        User createdByUser = userService.findUserById(reqUser);

        Chat group = new Chat();
        group.setGroup(true);
        group.setChat_image(req.getChat_image());
        group.setChat_name(req.getChat_name());
        group.setCreatedBy(createdByUser);
        group.getAdmins().add(createdByUser);

        for (Long userId : req.getUserIds()) {
            User user = userService.findUserById(userId);
            group.getUsers().add(user);
        }
        chatRepository.save(group);
        return group;
    }


    @Override
    public Chat addUserToGroup(Long userId, Long chatId, User reqUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("No chat with id " + chatId + " exists in this group"));

        User user = userService.findUserById(userId);

        if (chat.getAdmins().contains(reqUser)) {
            if (chat.getUsers().contains(user)) {
                throw new IllegalArgumentException("User is already part of this chat");
            }
            chat.getUsers().add(user);
            chatRepository.save(chat);
            return chat;
        } else {
            throw new IllegalArgumentException("You are not allowed to add this user to the chat");
        }
    }


    @Override
    public Chat renameGroup(Long chatId, String groupName, User reqUser) {
        Optional<Chat> opt = chatRepository.findById(chatId);
        User user = userService.findUserById(reqUser.getId());
        if (opt.isPresent()) {
            Chat chat = opt.get();
            if (chat.getUsers().contains(user)) {
                chat.setChat_name(groupName);
                return chatRepository.save(chat);
            }
            throw new IllegalArgumentException("You are not allowed to rename this user to the chat");
        }
        throw new IllegalArgumentException("No chat with id " + chatId + " exists in this group to rename");
    }

    @Override
    public Chat removeFromGroup(Long chatId, Long userId, User reqUser) {
        Optional<Chat> opt = chatRepository.findById(chatId);
        if (opt.isPresent()) {
            Chat chat = opt.get();
            User user = userService.findUserById(userId);
            if (chat.getAdmins().contains(reqUser)) {
                chat.getUsers().remove(user);
            }
            else if (chat.getUsers().contains(reqUser) && user.getId().equals(reqUser.getId())) {
                chat.getUsers().remove(reqUser);
            }
            chatRepository.save(chat);
            return chat;
        }
        // Throw an exception if the chat does not exist
        throw new IllegalArgumentException("No chat with id " + chatId + " exists in the group to remove");
    }

    @Override
    public void deleteChat(Long chatId, Long userId) {
        Optional<Chat> opt = chatRepository.findById(chatId);
        if (opt.isPresent()) {
            Chat chat = opt.get();
            // Check if the user is the creator of the chat or an admin
            if (chat.getCreatedBy().getId().equals(userId) ||
                    chat.getAdmins().stream().anyMatch(admin -> admin.getId().equals(userId))) {
                chatRepository.deleteById(chat.getId());
            } else {
                throw new RuntimeException("User is not authorized to delete this chat.");
            }

        } else {
            throw new RuntimeException("Chat not found with id: " + chatId);
        }
    }


}
