package com.mateusz.stockassistant.controller.alertconfig;

import com.mateusz.stockassistant.controller.alertconfig.dto.AlertConfigDto;
import com.mateusz.stockassistant.entity.AlertConfigEntity;
import com.mateusz.stockassistant.service.AlertConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertconfig")
public class AlertConfigController {

    private final AlertConfigService alertConfigService;

    @Autowired
    public AlertConfigController(AlertConfigService alertConfigService) {
        this.alertConfigService = alertConfigService;
    }

    @PostMapping("/add/alert")
    public AlertConfigEntity addAlertConfig(@RequestBody AlertConfigDto alertConfigDto) {
        return alertConfigService.addAlertConfiguration(alertConfigDto);
    }

    @GetMapping("/get/alert/")
    public List<AlertConfigEntity> getAlertConfig(@RequestParam String ticker) {
        return alertConfigService.getAlertConfiguration(ticker);
    }

    @GetMapping("/get/alert/all")
    public ResponseEntity<List<AlertConfigEntity>> getAllAlertConfigurations() {
        List<AlertConfigEntity> lowPriceAlertConfigurations = alertConfigService.getAllAlertConfigurations();

        if (lowPriceAlertConfigurations.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(lowPriceAlertConfigurations);
        }
    }

    @DeleteMapping("/delete/alert/")
    public String deleteAlertConfig(@RequestParam String ticker, @RequestParam AlertType alertType) {
        return alertConfigService.deleteAlertConfiguration(ticker, alertType);
    }
}