package com.example.pos.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ItemResponse(
    UUID id,
    String name,
    BigDecimal price,
    String category,
    String itemCode,
    String pctCode,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {}
