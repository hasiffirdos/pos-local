package com.example.pos.pra.dto;

public record PraFiscalizationResult(
    boolean success,
    String fiscalInvoiceNumber,
    String qrText,
    String verificationUrl,
    String message
) {}
