package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.controller.dto.NewStockDto;
import com.mateusz.springgpt.entity.OwnedStockEntity;
import com.mateusz.springgpt.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock/")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/add")
    public OwnedStockEntity addStock(@RequestBody NewStockDto newStockDto) {
        return stockService.addStock(newStockDto);
    }

    @GetMapping("/get/")
    public OwnedStockEntity getStock(@RequestParam String ticker) {
        return stockService.getStock(ticker);
    }
}