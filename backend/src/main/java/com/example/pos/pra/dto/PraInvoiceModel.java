package com.example.pos.pra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record PraInvoiceModel(
    @JsonProperty("POSID") long posId,
    @JsonProperty("USIN") String usin,
    @JsonProperty("DateTime") String dateTime,
    @JsonProperty("TotalSaleValue") BigDecimal totalSaleValue,
    @JsonProperty("TotalTaxCharged") BigDecimal totalTaxCharged,
    @JsonProperty("TotalBillAmount") BigDecimal totalBillAmount,
    @JsonProperty("TotalQuantity") BigDecimal totalQuantity,
    @JsonProperty("PaymentMode") int paymentMode,
    @JsonProperty("InvoiceType") int invoiceType,
    @JsonProperty("Items") List<PraInvoiceItem> items,
    @JsonProperty("InvoiceNumber") String invoiceNumber,
    @JsonProperty("RefUSIN") String refUsin,
    @JsonProperty("BuyerName") String buyerName,
    @JsonProperty("BuyerPNTN") String buyerPntn,
    @JsonProperty("BuyerCNIC") String buyerCnic,
    @JsonProperty("BuyerPhoneNumber") String buyerPhoneNumber,
    @JsonProperty("Discount") BigDecimal discount,
    @JsonProperty("FurtherTax") BigDecimal furtherTax
) {}
