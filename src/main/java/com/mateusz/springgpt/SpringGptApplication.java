package com.mateusz.springgpt;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringGptApplication implements CommandLineRunner {

    @Autowired
    OpenAiChatModel chatModel;

    public static void main(String[] args) {
        SpringApplication.run(SpringGptApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
		Prompt prompt = new Prompt("Tell me if you have access to list of stocks listed on New York Stock Exchange from this month?");
		System.out.println("Sending a prompt: " + prompt.getContents());

		ChatResponse response = chatModel.call(prompt);
		System.out.println("Full response is: " + response);
		System.out.println("Prompt respose is: " + response.getResult().getOutput().getText());
    }
}
