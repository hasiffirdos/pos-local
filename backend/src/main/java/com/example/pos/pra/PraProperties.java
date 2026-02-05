package com.example.pos.pra;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "pra")
public class PraProperties {
    private String mode = "stub";  // Options: cloud, stub
    
    // PRA Cloud API settings
    private String environment = "sandbox";  // Options: sandbox, production
    private String sandboxUrl = "https://ims.pral.com.pk/ims/sandbox/api/Live/PostData";
    private String sandboxToken = "";
    private String productionUrl = "https://ims.pral.com.pk/ims/production/api/Live/PostData";
    private String productionToken = "";
    
    // Invoice settings
    private long posId = 0;
    private int invoiceType = 1;
    private String defaultPctCode = "98211000";
    private double cashGstRate = 0.16;
    private double cardGstRate = 0.05;
    
    // Verification URL base
    private String verifyUrlBase = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=";
    
    // Stub settings (for testing)
    private final Stub stub = new Stub();

    // Getters and setters
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getSandboxUrl() {
        return sandboxUrl;
    }

    public void setSandboxUrl(String sandboxUrl) {
        this.sandboxUrl = sandboxUrl;
    }

    public String getSandboxToken() {
        return sandboxToken;
    }

    public void setSandboxToken(String sandboxToken) {
        this.sandboxToken = sandboxToken;
    }

    public String getProductionUrl() {
        return productionUrl;
    }

    public void setProductionUrl(String productionUrl) {
        this.productionUrl = productionUrl;
    }

    public String getProductionToken() {
        return productionToken;
    }

    public void setProductionToken(String productionToken) {
        this.productionToken = productionToken;
    }

    public long getPosId() {
        return posId;
    }

    public void setPosId(long posId) {
        this.posId = posId;
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

    public String getVerifyUrlBase() {
        return verifyUrlBase;
    }

    public void setVerifyUrlBase(String verifyUrlBase) {
        this.verifyUrlBase = verifyUrlBase;
    }

    public Stub getStub() {
        return stub;
    }

    // Convenience methods
    public String getApiUrl() {
        return "production".equalsIgnoreCase(environment) ? productionUrl : sandboxUrl;
    }

    public String getApiToken() {
        return "production".equalsIgnoreCase(environment) ? productionToken : sandboxToken;
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
}
