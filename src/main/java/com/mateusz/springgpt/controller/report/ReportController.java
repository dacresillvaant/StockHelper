package com.mateusz.springgpt.controller.report;

import com.mateusz.springgpt.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/ownedstockreport/")
    public void generateOwnedStockProfitReport() {
        reportService.prepareOwnedStockProfitReport();
    }
}