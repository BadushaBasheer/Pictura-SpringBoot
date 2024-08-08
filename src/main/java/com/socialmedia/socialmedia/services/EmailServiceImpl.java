package com.socialmedia.socialmedia.services;

import com.socialmedia.socialmedia.enums.EmailTemplateName;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;


    @Async
    @Override
    public void sendEmail(String to,
                          String username,
                          EmailTemplateName emailTemplate,
                          String confirmationUrl,
                          String activationCode,
                          String subject) {
        long startTime = System.currentTimeMillis();

        try {
            String templateName = emailTemplate != null ? emailTemplate.name() : "confirm_email";
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name()
            );

            Map<String, Object> properties = new HashMap<>();
            properties.put("username", username);
            properties.put("confirmationUrl", confirmationUrl);
            properties.put("activation_code", activationCode);

            Context context = new Context();
            context.setVariables(properties);

            String template = templateEngine.process(templateName, context);

            helper.setFrom("badusha0701@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(template, true);

            long sendStartTime = System.currentTimeMillis();
            mailSender.send(mimeMessage);
            long sendEndTime = System.currentTimeMillis();
            logger.info("Mail sending time: {} ms", (sendEndTime - sendStartTime));

            logger.info("Email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Total email sending process time: {} ms", (endTime - startTime));
        }
    }
}
