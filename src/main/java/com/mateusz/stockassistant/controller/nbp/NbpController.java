package com.mateusz.stockassistant.controller.nbp;

import com.mateusz.stockassistant.controller.nbp.dto.NbpCurrencyRateDto;
import com.mateusz.stockassistant.service.NbpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nbp")
public class NbpController {

    private final NbpService nbpService;

    @Autowired
    public NbpController(NbpService nbpService) {
        this.nbpService = nbpService;
    }

    @GetMapping("/{symbol}/{date}")
    public ResponseEntity<NbpCurrencyRateDto> getPlnCurrencyRateForDate(@PathVariable String symbol, @PathVariable String date) {
        return nbpService.getPlnCurrencyRateForDate(symbol, date);
    }
}