package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.enums.EmailTemplateName;

public interface EmailService {

    void sendEmail(String to,
                   String username,
                   EmailTemplateName emailTemplate,
                   String confirmationUrl,
                   String activationCode,
                   String subject);
}

