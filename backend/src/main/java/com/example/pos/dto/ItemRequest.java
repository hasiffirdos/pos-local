package com.example.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemRequest(
    @NotBlank String name,
    @NotNull @Positive BigDecimal price,
    @NotBlank String category,
    @NotBlank String itemCode,
    @NotBlank String pctCode
) {}
