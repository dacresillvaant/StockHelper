package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.entity.Heatmap;
import com.mateusz.springgpt.repository.ScreenshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/screenshot")
public class ScreenshotController {

    private final ScreenshotRepository screenshotRepository;

    @Autowired
    public ScreenshotController(ScreenshotRepository screenshotRepository) {
        this.screenshotRepository = screenshotRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Heatmap> getScreenshotById(@PathVariable Long id) {
        Optional<Heatmap> screenshot = screenshotRepository.findById(id);

        return screenshot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}