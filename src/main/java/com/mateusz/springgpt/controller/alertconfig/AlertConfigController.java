package com.mateusz.springgpt.controller.alertconfig;

import com.mateusz.springgpt.controller.alertconfig.dto.AlertConfigDto;
import com.mateusz.springgpt.entity.AlertConfigEntity;
import com.mateusz.springgpt.service.AlertConfigService;
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

    @PostMapping("/add/lowpricealert")
    public AlertConfigEntity addAlertConfig(@RequestBody AlertConfigDto alertConfigDto) {
        return alertConfigService.addAlertConfiguration(alertConfigDto);
    }

    @GetMapping("/get/lowpricealert/")
    public List<AlertConfigEntity> getAlertConfig(@RequestParam String ticker) {
        return alertConfigService.getAlertConfiguration(ticker);
    }

    @GetMapping("/get/lowpricealert/all")
    public ResponseEntity<List<AlertConfigEntity>> getAllAlertConfigurations() {
        List<AlertConfigEntity> lowPriceAlertConfigurations = alertConfigService.getAlertConfigurations();

        if (lowPriceAlertConfigurations.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(lowPriceAlertConfigurations);
        }
    }

    @DeleteMapping("/delete/lowpricealert/")
    public String deleteAlertConfig(@RequestParam String ticker, @RequestParam AlertType alertType) {
        return alertConfigService.deleteAlertConfiguration(ticker, alertType);
    }
}