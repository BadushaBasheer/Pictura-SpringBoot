package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.entities.Message;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.request.SendMessageRequest;

import java.util.List;

public interface MessageService {

    Message sendMessage(SendMessageRequest sendMessageRequest);

    List<Message> getChatsMessages(Long chatId, Long reqUser);

    Message findMessageById(Long id);

    void deleteMessageById(Long id, Long reqUser);
}
