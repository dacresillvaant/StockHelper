package com.mateusz.springgpt.controller.openai;

import com.mateusz.springgpt.service.OpenAiService;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    @Autowired
    public OpenAiController (OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/chat")
    public AssistantMessage chat(@RequestBody String userPrompt) {
        return openAiService.sendPrompt(userPrompt);
    }

    @GetMapping("/models")
    public ResponseEntity<String> listModels() {
        return openAiService.listModels();
    }
}