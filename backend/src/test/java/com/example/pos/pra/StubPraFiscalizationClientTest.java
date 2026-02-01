package com.example.pos.pra;

import com.example.pos.pra.dto.PraInvoiceItem;
import com.example.pos.pra.dto.PraInvoiceModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StubPraFiscalizationClientTest {

    @Test
    void fiscalize_generatesStableFiscalNumber() {
        PraProperties properties = new PraProperties();
        properties.getStub().setFailRate(0.0);
        properties.getStub().setFailOnAmountAbove(BigDecimal.ZERO);
        StubPraFiscalizationClient client = new StubPraFiscalizationClient(properties);

        PraInvoiceModel invoice = buildInvoice("INV-1001", new BigDecimal("50.00"));

        String first = client.fiscalize(invoice).fiscalInvoiceNumber();
        String second = client.fiscalize(invoice).fiscalInvoiceNumber();

        assertEquals(first, second);
    }

    @Test
    void fiscalize_failsWhenAmountAboveThreshold() {
        PraProperties properties = new PraProperties();
        properties.getStub().setFailOnAmountAbove(new BigDecimal("10.00"));
        StubPraFiscalizationClient client = new StubPraFiscalizationClient(properties);

        PraInvoiceModel invoice = buildInvoice("INV-2001", new BigDecimal("50.00"));

        assertThrows(PraUnavailableException.class, () -> client.fiscalize(invoice));
    }

    @Test
    void fiscalize_failsWhenRateIsOne() {
        PraProperties properties = new PraProperties();
        properties.getStub().setFailRate(1.0);
        StubPraFiscalizationClient client = new StubPraFiscalizationClient(properties);

        PraInvoiceModel invoice = buildInvoice("INV-3001", new BigDecimal("5.00"));

        assertThrows(PraUnavailableException.class, () -> client.fiscalize(invoice));
    }

    private PraInvoiceModel buildInvoice(String usin, BigDecimal total) {
        PraInvoiceItem item = new PraInvoiceItem(
            "SKU-1",
            "Sample",
            "00000000",
            new BigDecimal("1"),
            BigDecimal.ZERO,
            total,
            BigDecimal.ZERO,
            total,
            1,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null
        );
        return new PraInvoiceModel(
            1L,
            usin,
            "2024-05-10 10:15:30",
            total,
            BigDecimal.ZERO,
            total,
            new BigDecimal("1"),
            1,
            1,
            List.of(item),
            "",
            null,
            "Buyer",
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }
}
