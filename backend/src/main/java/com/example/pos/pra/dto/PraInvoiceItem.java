package com.example.pos.pra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record PraInvoiceItem(
    @JsonProperty("ItemCode") String itemCode,
    @JsonProperty("ItemName") String itemName,
    @JsonProperty("PCTCode") String pctCode,
    @JsonProperty("Quantity") BigDecimal quantity,
    @JsonProperty("TaxRate") BigDecimal taxRate,
    @JsonProperty("SaleValue") BigDecimal saleValue,
    @JsonProperty("TaxCharged") BigDecimal taxCharged,
    @JsonProperty("TotalAmount") BigDecimal totalAmount,
    @JsonProperty("InvoiceType") int invoiceType,
    @JsonProperty("Discount") BigDecimal discount,
    @JsonProperty("FurtherTax") BigDecimal furtherTax,
    @JsonProperty("RefUSIN") String refUsin
) {}
