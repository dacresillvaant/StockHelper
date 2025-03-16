package com.mateusz.springgpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringGptApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringGptApplication.class, args);
    }
}