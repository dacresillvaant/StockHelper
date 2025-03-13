package com.mateusz.springgpt;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    @Autowired
    private OpenAiChatModel chatModel;

    public String sendPrompt(String userPrompt) {
        Prompt prompt = new Prompt(userPrompt);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}