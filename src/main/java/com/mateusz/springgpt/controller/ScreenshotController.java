package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.controller.dto.ImageAnalyzeDto;
import com.mateusz.springgpt.entity.HeatmapEntity;
import com.mateusz.springgpt.service.HeatmapAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/screenshot")
public class ScreenshotController {

    private final HeatmapAnalysisService heatmapAnalysisService;

    @Autowired
    public ScreenshotController(HeatmapAnalysisService heatmapAnalysisService) {
        this.heatmapAnalysisService = heatmapAnalysisService;
    }

    @GetMapping("/{id}")
    public HeatmapEntity getHeatmapById(@PathVariable Long id) {
        return heatmapAnalysisService.getHeatmapById(id);
    }

    @GetMapping("/{id}/analyze")
    public ResponseEntity<ImageAnalyzeDto> analyzeScreenshot(@PathVariable Long id) {
        BigDecimal ratio = heatmapAnalysisService.analyzeHeatmap(id);

        if (ratio == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ImageAnalyzeDto(id, ratio));
    }
}