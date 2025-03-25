package com.mateusz.springgpt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OpenAiService {

    private final OpenAiChatModel chatModel;

    @Autowired
    public OpenAiService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public AssistantMessage sendPrompt(String userPrompt) {
        Prompt prompt = new Prompt(userPrompt);
        ChatResponse response = chatModel.call(prompt);

        log.info("Total tokens used for prompt \"{}\" is: {}", userPrompt, response.getMetadata().getUsage().getTotalTokens());
        log.info("Remaining tokens: {}", response.getMetadata().getRateLimit().getTokensRemaining());

        return response.getResult().getOutput();
    }

    public ResponseEntity<String> listModels() {
        String url = "https://api.openai.com/v1/models";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestBody = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, requestBody, String.class);
    }
}