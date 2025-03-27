package com.mateusz.springgpt.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailgunEmailService {

    private final MailgunMessagesApi mailgunMessagesApi;

    @Value("${mailgun.from}")
    private String mailgunFrom;

    @Value("${mailgun.domain}")
    private String mailgunDomain;

    @Autowired
    public MailgunEmailService(MailgunMessagesApi mailgunMessagesApi) {
        this.mailgunMessagesApi = mailgunMessagesApi;
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
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
}