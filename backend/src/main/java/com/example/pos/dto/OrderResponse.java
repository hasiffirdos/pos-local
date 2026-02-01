package com.example.pos.dto;

import com.example.pos.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    String invoiceNumber,
    String fiscalInvoiceNumber,
    String fiscalQrText,
    String fiscalVerificationUrl,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal total,
    OrderStatus status,
    String paymentMode,
    BigDecimal gstRate,
    BigDecimal gstAmount,
    String customerName,
    String customerPhone,
    String customerCnic,
    String customerPntn,
    String customerTaxId,
    String notes,
    BigDecimal discount,
    Instant createdAt,
    List<OrderItemResponse> items
) {}
