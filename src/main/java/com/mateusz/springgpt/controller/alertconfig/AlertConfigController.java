package com.mateusz.springgpt.controller.alertconfig;

import com.mateusz.springgpt.controller.alertconfig.dto.NewLowPriceAlertConfigDto;
import com.mateusz.springgpt.entity.LowPriceAlertEntity;
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
    public LowPriceAlertEntity addLowPriceAlertConfig(@RequestBody NewLowPriceAlertConfigDto newLowPriceAlertConfigDto) {
        return alertConfigService.addLowPriceAlertConfiguration(newLowPriceAlertConfigDto);
    }

    @GetMapping("/get/lowpricealert/")
    public LowPriceAlertEntity getLowPriceAlertConfig(@RequestParam String ticker) {
        return alertConfigService.getLowPriceAlertConfiguration(ticker);
    }

    @GetMapping("/get/lowpricealert/all")
    public ResponseEntity<List<LowPriceAlertEntity>> getAllLowPriceAlertConfigurations() {
        List<LowPriceAlertEntity> lowPriceAlertConfigurations = alertConfigService.getLowPriceAlertConfigurations();

        if (lowPriceAlertConfigurations.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(lowPriceAlertConfigurations);
        }
    }

    @DeleteMapping("/delete/lowpricealert/")
    public String deleteLowPriceAlertConfig(@RequestParam String ticker) {
        return alertConfigService.deleteLowPriceAlertConfiguration(ticker);
    }
}
