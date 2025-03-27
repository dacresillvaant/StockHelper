package com.mateusz.springgpt.config;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${mailgun.api-key}")
    private String mailgunApiKey;

    @Value("${mailgun.eu-base-url}")
    private String euBaseUrl;

    @Bean
    public MailgunMessagesApi mailgunMessagesApi() {
        return MailgunClient.config(euBaseUrl, mailgunApiKey).createAsyncApi(MailgunMessagesApi.class);
    }
}