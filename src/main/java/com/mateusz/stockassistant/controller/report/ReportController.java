package com.mateusz.stockassistant.controller.report;

import com.mateusz.stockassistant.controller.report.dto.ProfitReportDto;
import com.mateusz.stockassistant.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/ownedstockreport/")
    public List<ProfitReportDto> generateOwnedStockProfitReport() {
        return  reportService.prepareOwnedStockProfitReport();
    }
}