package com.example.pos.service;

import com.example.pos.dto.DailySalesReportResponse;
import com.example.pos.entity.Order;
import com.example.pos.entity.OrderStatus;
import com.example.pos.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReportService {
    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public DailySalesReportResponse dailySales(LocalDate date) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant start = date.atStartOfDay(zoneId).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(zoneId).toInstant();

        List<Order> orders = orderRepository.findByStatusAndCreatedAtBetween(OrderStatus.PAID, start, end);
        BigDecimal totalSales = orders.stream()
            .map(Order::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DailySalesReportResponse(date, orders.size(), totalSales);
    }
}
