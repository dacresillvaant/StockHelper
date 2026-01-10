package com.mateusz.springgpt.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mateusz.springgpt.tools.mail.MailTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class MailgunEmailService {

    private final MailgunMessagesApi mailgunMessagesApi;

    @Value("${mailgun.from}")
    private String mailgunFrom;

    @Value("${mailgun.domain}")
    private String mailgunDomain;

    @Value("${mailgun.default-receiver}")
    @Getter
    private String defaultMailReceiver;

    private final Environment environment;

    @Autowired
    public MailgunEmailService(MailgunMessagesApi mailgunMessagesApi, Environment environment) {
        this.mailgunMessagesApi = mailgunMessagesApi;
        this.environment = environment;
    }

    protected void sendEmailBasicTemplate(String to, String subject, String text) {
        Message message = Message.builder()
                .from(mailgunFrom)
                .to(to)
                .subject(subject)
                .text(text)
                .build();

        try {
            log.info("Sending email to: {}, subject: {}", to, subject);
            mailgunMessagesApi.sendMessage(mailgunDomain, message);
        } catch (Exception e) {
            log.error("Failed to send the e-mail.", e);
        }
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        sendEmailBasicTemplate(to, subject, text);
    }

    @Async
    public void sendEmail(String to, MailTemplate mailTemplate) {
        sendEmailBasicTemplate(to, mailTemplate.subject(), mailTemplate.body());
    }

    @Async
    public void sendErrorAlertEmail(Exception exception, HttpServletRequest request) {
        String activeProfile = environment.getActiveProfiles()[0];
        String subject = "[".concat(activeProfile).concat("] Unhandled exception alert");

        StringBuilder message = new StringBuilder();
        message.append("Unhandled exception occurred in Stock Assistant app:\n\n");
        message.append("Timestamp: ").append(LocalDateTime.now()).append("\n");

        if (request != null) {
            message.append("Path: ").append(Optional.ofNullable(request.getRequestURI()).orElse("-")).append("\n");
            message.append("Query: ").append(Optional.ofNullable(request.getQueryString()).orElse("-")).append("\n");
            message.append("User-Agent: ").append(Optional.ofNullable(request.getHeader("User-Agent")).orElse("-")).append("\n");
            message.append("Remote IP: ").append(Optional.ofNullable(request.getRemoteAddr()).orElse("-")).append("\n");
        }

        message.append("\nException: ").append(exception.getClass().getName()).append("\n");
        message.append("Message: ").append(Optional.ofNullable(exception.getMessage()).orElse("(no message)")).append("\n");
        message.append("Stack trace:\n");

        for (StackTraceElement element : exception.getStackTrace()) {
            message.append("    at ").append(element.toString()).append("\n");
        }

        sendEmailBasicTemplate(defaultMailReceiver, subject, message.toString());
    }
}