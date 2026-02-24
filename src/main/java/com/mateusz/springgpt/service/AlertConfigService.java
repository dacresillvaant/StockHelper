package com.mateusz.springgpt.service;

import com.mateusz.springgpt.controller.alertconfig.AlertType;
import com.mateusz.springgpt.controller.alertconfig.dto.AlertConfigDto;
import com.mateusz.springgpt.entity.AlertConfigEntity;
import com.mateusz.springgpt.repository.AlertConfigRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class AlertConfigService {

    private final AlertConfigRepository alertConfigRepository;

    @Autowired
    public AlertConfigService(AlertConfigRepository alertConfigRepository) {
        this.alertConfigRepository = alertConfigRepository;
    }

    public AlertConfigEntity addAlertConfiguration(AlertConfigDto alertConfigDto) {
        if (alertConfigRepository.existsByTicker(alertConfigDto.getTicker())) {
            throw new DataIntegrityViolationException("Config for ticker '" + alertConfigDto.getTicker() + "' already exists");
        }

        AlertConfigEntity newAlertConfig = AlertConfigEntity.builder()
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .ticker(alertConfigDto.getTicker())
                .percentChangeThreshold(alertConfigDto.getPercentChangeThreshold())
                .alertType(alertConfigDto.getAlertType())
                .build();

        return alertConfigRepository.save(newAlertConfig);
    }

    public List<AlertConfigEntity> getAlertConfiguration(String ticker) {
        List<AlertConfigEntity> alertConfigEntities = alertConfigRepository.findByTicker(ticker);

        if (alertConfigEntities.isEmpty()) {
            throw new NoSuchElementException("Config for ticker '" + ticker + "' not found.");
        } else {
            return alertConfigEntities;
        }
    }

    public List<AlertConfigEntity> getAlertConfigurations() {
        return alertConfigRepository.findAll();
    }

    @Transactional
    public String deleteAlertConfiguration(String ticker, AlertType alertType) {
        int numberOfConfigurationsDeleted = alertConfigRepository.deleteByTickerAndAlertType(ticker, alertType);
        if (numberOfConfigurationsDeleted > 0) {
            return ticker.concat(" alert configuration has been deleted");
        } else {
            return ticker.concat(" alert configuration not found, no configuration has been deleted");
        }
    }
}