package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.entities.Chat;
import com.socialmedia.socialmedia.request.GroupChatRequest;
import com.socialmedia.socialmedia.entities.User;

import java.util.List;

public interface ChatService {

    Chat createChat(Long reqUser, Long userId2);

    Chat findOrCreateChat(Long currentUserId, Long selectedUserId);

    Chat findChatById(Long userId);

    List<Chat> findAllChatByUserId(Long userId);

    Chat createGroup(GroupChatRequest req, Long reqUser);

    Chat addUserToGroup(Long userId, Long chatId, User reqUser);

    Chat renameGroup(Long chatId, String groupName, User rqeUser);

    Chat removeFromGroup( Long chatId, Long userId, User reqUser);

    void deleteChat(Long chatId, Long userId);

}
