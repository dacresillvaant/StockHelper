package com.mateusz.springgpt.service;

import com.mateusz.springgpt.controller.alertconfig.dto.NewLowPriceAlertConfigDto;
import com.mateusz.springgpt.entity.LowPriceAlertEntity;
import com.mateusz.springgpt.repository.LowPriceAlertRepository;
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

    private final LowPriceAlertRepository lowPriceAlertRepository;

    @Autowired
    public AlertConfigService(LowPriceAlertRepository lowPriceAlertRepository) {
        this.lowPriceAlertRepository = lowPriceAlertRepository;
    }

    public LowPriceAlertEntity addLowPriceAlertConfiguration(NewLowPriceAlertConfigDto newLowPriceAlertConfigDto) {
        if (lowPriceAlertRepository.existsByTicker(newLowPriceAlertConfigDto.getTicker())) {
            throw new DataIntegrityViolationException("Config for ticker '" + newLowPriceAlertConfigDto.getTicker() + "' already exists");
        }

        LowPriceAlertEntity newAlertConfig = LowPriceAlertEntity.builder()
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .ticker(newLowPriceAlertConfigDto.getTicker())
                .percentChangeThreshold(newLowPriceAlertConfigDto.getPercentChangeThreshold())
                .build();

        return lowPriceAlertRepository.save(newAlertConfig);
    }

    public LowPriceAlertEntity getLowPriceAlertConfiguration(String ticker) {
        return lowPriceAlertRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoSuchElementException("Config for ticker '" + ticker + "' not found."));
    }

    public List<LowPriceAlertEntity> getLowPriceAlertConfigurations() {
        return lowPriceAlertRepository.findAll();
    }

    @Transactional
    public String deleteLowPriceAlertConfiguration(String ticker) {
        int numberOfConfigurationsDeleted = lowPriceAlertRepository.deleteByTicker(ticker);
        if (numberOfConfigurationsDeleted > 0) {
            return ticker.concat(" alert configuration has been deleted");
        } else {
            return ticker.concat(" alert configuration not found, no configuration has been deleted");
        }
    }
}