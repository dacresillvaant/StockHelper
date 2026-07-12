package com.mateusz.stockassistant.controller.mailgun;

import com.mateusz.stockassistant.controller.mailgun.dto.EmailRequest;
import com.mateusz.stockassistant.service.mailgunemail.MailgunEmailService;
import org.springframework.beans.factory.annotation.Autowired;
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