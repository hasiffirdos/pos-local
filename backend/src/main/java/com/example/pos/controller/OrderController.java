package com.example.pos.controller;

import com.example.pos.dto.OrderItemRequest;
import com.example.pos.dto.OrderResponse;
import com.example.pos.dto.OrderUpdateRequest;
import com.example.pos.entity.OrderStatus;
import com.example.pos.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder() {
        return orderService.createOrder();
    }

    @GetMapping
    public java.util.List<OrderResponse> listOrders(@RequestParam(value = "status", required = false) OrderStatus status) {
        return orderService.listOrders(status);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id) {
        return orderService.getOrder(id);
    }

    @PatchMapping("/{id}")
    public OrderResponse updateOrder(@PathVariable UUID id, @RequestBody OrderUpdateRequest request) {
        return orderService.updateOrder(id, request);
    }

    @PostMapping("/{id}/items")
    public OrderResponse addOrUpdateItem(@PathVariable UUID id, @Valid @RequestBody OrderItemRequest request) {
        return orderService.addOrUpdateItem(id, request);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public OrderResponse removeItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        return orderService.removeItem(id, itemId);
    }

    @PostMapping("/{id}/checkout")
    public OrderResponse checkout(@PathVariable UUID id) {
        return orderService.checkout(id);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable UUID id) {
        return orderService.cancelOrder(id);
    }
}
