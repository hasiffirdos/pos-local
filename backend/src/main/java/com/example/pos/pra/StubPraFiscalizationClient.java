package com.example.pos.pra;

import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class StubPraFiscalizationClient implements PraFiscalizationClient {
    private static final String STATUS_OK = "OK";
    private final PraProperties properties;

    public StubPraFiscalizationClient(PraProperties properties) {
        this.properties = properties;
    }

    @Override
    public PraFiscalizationResult fiscalize(PraInvoiceModel invoice) {
        if (shouldFail(invoice)) {
            throw new PraUnavailableException("PRA IMS unavailable (stub)");
        }

        String fiscalInvoiceNumber = generateFiscalInvoiceNumber(invoice.usin());
        String qrText = "PRA|" + fiscalInvoiceNumber + "|" + invoice.usin();
        String verificationUrl = "https://pra.gov/verify/" + fiscalInvoiceNumber;

        return new PraFiscalizationResult(true, fiscalInvoiceNumber, qrText, verificationUrl, "Fiscalized (stub)");
    }

    @Override
    public PraHealth health() {
        return new PraHealth(STATUS_OK, "Stub client ready");
    }

    private boolean shouldFail(PraInvoiceModel invoice) {
        BigDecimal threshold = properties.getStub().getFailOnAmountAbove();
        if (threshold != null && threshold.compareTo(BigDecimal.ZERO) > 0) {
            if (invoice.totalBillAmount().compareTo(threshold) > 0) {
                return true;
            }
        }

        double rate = properties.getStub().getFailRate();
        if (rate <= 0) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble() < rate;
    }

    private String generateFiscalInvoiceNumber(String invoiceNumber) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(invoiceNumber.getBytes(StandardCharsets.UTF_8));
            String suffix = HexFormat.of().formatHex(hash).substring(0, 10).toUpperCase();
            return "FISC-" + suffix;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash invoice number", ex);
        }
    }
}
