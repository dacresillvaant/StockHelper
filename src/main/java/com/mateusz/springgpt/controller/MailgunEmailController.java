package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.service.MailgunEmailService;
import com.mateusz.springgpt.service.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class MailgunEmailController {

    private final MailgunEmailService mailgunEmailService;

    @Autowired
    public MailgunEmailController(MailgunEmailService mailgunEmailService) {
        this.mailgunEmailService = mailgunEmailService;
    }

    @PostMapping("/send")
    public void sendEmail(@RequestBody EmailRequest emailRequest) {
        mailgunEmailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getText());
    }
}