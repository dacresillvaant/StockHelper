package com.mateusz.stockassistant.service;

import com.mateusz.stockassistant.entity.HeatmapEntity;
import com.mateusz.stockassistant.repository.HeatmapRepository;
import com.mateusz.stockassistant.tools.ImageAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class HeatmapAnalysisService {

    private final HeatmapRepository heatmapRepository;

    @Autowired
    public HeatmapAnalysisService(HeatmapRepository heatmapRepository) {
        this.heatmapRepository = heatmapRepository;
    }

    public HeatmapEntity getHeatmapById(Long id) {
        return heatmapRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Screenshot with id '" + id + "' not found"));
    }

    public BigDecimal analyzeHeatmap(Long id) {
        Optional<HeatmapEntity> heatmap = heatmapRepository.findById(id);

        if (heatmap.isEmpty()) {
            return null;
        }

        String base64Image = heatmap.get().getBase64();
        log.info("Analyzing pixels of heatmap number: {}", id);
        Mat image = ImageAnalyzer.base64ToMat(base64Image);

        return ImageAnalyzer.greenRedRatio(image);
    }
}