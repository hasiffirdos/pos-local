package com.example.pos.pra.dto;

import java.math.BigDecimal;

public record PraPayment(
    String method,
    BigDecimal paidAmount,
    BigDecimal changeAmount
) {}
