package com.mateusz.springgpt.controller.stock;

import com.mateusz.springgpt.controller.stock.dto.NewStockDto;
import com.mateusz.springgpt.entity.OwnedStockEntity;
import com.mateusz.springgpt.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
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

    @GetMapping("/get/all")
    public ResponseEntity<List<OwnedStockEntity>> getAllStocks() {
        List<OwnedStockEntity> stocks = stockService.getAllStocks();

        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(stocks);
    }

    @DeleteMapping("/delete/")
    public String deleteStock(@RequestParam String ticker) {
        return stockService.deleteStock(ticker);
    }
}