package com.mateusz.springgpt.service;

import com.mateusz.springgpt.controller.dto.NewStockDto;
import com.mateusz.springgpt.entity.OwnedStockEntity;
import com.mateusz.springgpt.repository.OwnedStockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class StockService {

    private final OwnedStockRepository ownedStockRepository;

    @Autowired
    public StockService(OwnedStockRepository ownedStockRepository) {
        this.ownedStockRepository = ownedStockRepository;
    }

    public OwnedStockEntity addStock(NewStockDto newStockDto) {
        if (ownedStockRepository.existsByTicker(newStockDto.getTicker())) {
            throw new DataIntegrityViolationException("Stock with ticker '" + newStockDto.getTicker() + "' already exists.");
        }

        OwnedStockEntity newStock = OwnedStockEntity.builder()
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .boughtDate(newStockDto.getBoughtDate())
                .ticker(newStockDto.getTicker())
                .name(newStockDto.getName())
                .position(newStockDto.getPosition())
                .purchasePrice(newStockDto.getPurchasePrice())
                .currency(newStockDto.getCurrency()).build();

        return ownedStockRepository.save(newStock);
    }

    public OwnedStockEntity getStock(String ticker) {
        return ownedStockRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoSuchElementException("Stock with ticker '" + ticker + "' not found"));
    }

    public List<OwnedStockEntity> getAllStocks() {
        return Optional.of(ownedStockRepository.findAll()).orElseThrow(() -> new NoSuchElementException("Stock entity is empty"));
    }
}