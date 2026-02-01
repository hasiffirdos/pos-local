package com.example.pos.pra;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "pra")
public class PraProperties {
    private String mode = "stub";
    private String imsBaseUrl = "http://localhost:8524";
    private final Stub stub = new Stub();
    private final Ims ims = new Ims();

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getImsBaseUrl() {
        return imsBaseUrl;
    }

    public void setImsBaseUrl(String imsBaseUrl) {
        this.imsBaseUrl = imsBaseUrl;
    }

    public Stub getStub() {
        return stub;
    }

    public Ims getIms() {
        return ims;
    }

    public static class Stub {
        private boolean enabled = true;
        private double failRate = 0.0;
        private BigDecimal failOnAmountAbove = BigDecimal.ZERO;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public double getFailRate() {
            return failRate;
        }

        public void setFailRate(double failRate) {
            this.failRate = failRate;
        }

        public BigDecimal getFailOnAmountAbove() {
            return failOnAmountAbove;
        }

        public void setFailOnAmountAbove(BigDecimal failOnAmountAbove) {
            this.failOnAmountAbove = failOnAmountAbove;
        }
    }

    public static class Ims {
        private long posId = 0;
        private int paymentMode = 1;
        private int invoiceType = 1;
        private String defaultPctCode = "00000000";
        private double cashGstRate = 0.16;
        private double cardGstRate = 0.05;

        public long getPosId() {
            return posId;
        }

        public void setPosId(long posId) {
            this.posId = posId;
        }

        public int getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(int paymentMode) {
            this.paymentMode = paymentMode;
        }

        public int getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(int invoiceType) {
            this.invoiceType = invoiceType;
        }

        public String getDefaultPctCode() {
            return defaultPctCode;
        }

        public void setDefaultPctCode(String defaultPctCode) {
            this.defaultPctCode = defaultPctCode;
        }

        public double getCashGstRate() {
            return cashGstRate;
        }

        public void setCashGstRate(double cashGstRate) {
            this.cashGstRate = cashGstRate;
        }

        public double getCardGstRate() {
            return cardGstRate;
        }

        public void setCardGstRate(double cardGstRate) {
            this.cardGstRate = cardGstRate;
        }
    }
}
