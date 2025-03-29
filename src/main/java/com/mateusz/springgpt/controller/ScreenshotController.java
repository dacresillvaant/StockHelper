package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.controller.dto.ImageAnalyzeResponse;
import com.mateusz.springgpt.entity.HeatmapEntity;
import com.mateusz.springgpt.repository.HeatmapRepository;
import com.mateusz.springgpt.service.HeatmapAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/screenshot")
public class ScreenshotController {

    private final HeatmapRepository heatmapRepository;
    private final HeatmapAnalysisService heatmapAnalysisService;

    @Autowired
    public ScreenshotController(HeatmapRepository heatmapRepository, HeatmapAnalysisService heatmapAnalysisService) {
        this.heatmapRepository = heatmapRepository;
        this.heatmapAnalysisService = heatmapAnalysisService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeatmapEntity> getHeatmapById(@PathVariable Long id) {
        Optional<HeatmapEntity> heatmap = heatmapRepository.findById(id);

        return heatmap.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/analyze")
    public ResponseEntity<ImageAnalyzeResponse> analyzeScreenshot(@PathVariable Long id) {
        BigDecimal ratio = heatmapAnalysisService.analyzeHeatmap(id);

        if (ratio == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ImageAnalyzeResponse(id, ratio));
    }
}