package com.mateusz.stockassistant.controller.trend;

import com.mateusz.stockassistant.logic.TrendNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trend")
public class TrendNotifierController {

    private final TrendNotifier trendNotifier;

    @Autowired
    public TrendNotifierController(TrendNotifier trendNotifier) {
        this.trendNotifier = trendNotifier;
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkTrends() {
        trendNotifier.checkTrends();
        return ResponseEntity.ok("Trend check initiated");
    }
}