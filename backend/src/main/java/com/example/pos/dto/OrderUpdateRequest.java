package com.example.pos.dto;

import java.math.BigDecimal;

public record OrderUpdateRequest(
    String customerName,
    String customerPhone,
    String customerCnic,
    String customerPntn,
    String customerTaxId,
    String notes,
    BigDecimal discount,
    String paymentMode
) {}
