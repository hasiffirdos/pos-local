package com.example.pos.pra.ims;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImsInvoiceResponse(
    @JsonProperty("InvoiceNumber") String invoiceNumber,
    @JsonProperty("Code") String code,
    @JsonProperty("Response") String response,
    @JsonProperty("Errors") Object errors
) {}
