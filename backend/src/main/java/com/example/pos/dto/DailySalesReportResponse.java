package com.example.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesReportResponse(
    LocalDate date,
    int orderCount,
    BigDecimal totalSales
) {}
