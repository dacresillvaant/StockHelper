package com.mateusz.springgpt.config;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${mailgun.api-key}")
    private String mailgunApiKey;

    @Value("${mailgun.eu-base-url}")
    private String euBaseUrl;

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    @Autowired
    public AppConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Bean
    public MailgunMessagesApi mailgunMessagesApi() {
        return MailgunClient.config(euBaseUrl, mailgunApiKey).createAsyncApi(MailgunMessagesApi.class);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
    }
}