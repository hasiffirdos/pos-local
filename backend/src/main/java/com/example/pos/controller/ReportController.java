package com.example.pos.controller;

import com.example.pos.dto.DailySalesReportResponse;
import com.example.pos.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily-sales")
    public DailySalesReportResponse dailySales(@RequestParam("date") LocalDate date) {
        return reportService.dailySales(date);
    }
}
