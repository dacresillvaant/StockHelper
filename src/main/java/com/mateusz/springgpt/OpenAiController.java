package com.mateusz.springgpt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/chat")
    public String chat(@RequestBody String userPrompt) {
        return openAiService.sendPrompt(userPrompt);
    }

    @GetMapping("/test")
    public String testConnection() {
        return openAiService.sendPrompt("?");
    }

    @GetMapping("/testconnection")
    public String checkApiConnection() {
        return openAiService.checkApiConnection();
    }
}