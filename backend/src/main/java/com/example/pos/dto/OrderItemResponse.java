package com.example.pos.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID itemId,
    String itemName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {}
