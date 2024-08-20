package com.socialmedia.socialmedia.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendMessageRequest {

    private Long userId;

    private Long chatId;

    private String content;


}
