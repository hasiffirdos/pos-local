package com.example.pos.pra;

import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceModel;
import com.example.pos.pra.ims.ImsInvoiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class CloudPraFiscalizationClient implements PraFiscalizationClient {
    private static final Logger logger = LoggerFactory.getLogger(CloudPraFiscalizationClient.class);
    private final PraProperties properties;
    private final RestTemplate restTemplate;

    public CloudPraFiscalizationClient(
        PraProperties properties,
        @Autowired(required = false) @Qualifier("praCloudRestTemplate") RestTemplate praCloudRestTemplate,
        RestTemplateBuilder restTemplateBuilder
    ) {
        this.properties = properties;
        // Use custom configured RestTemplate if available (when mode=cloud), otherwise use default
        if (praCloudRestTemplate != null) {
            logger.info("Using custom configured RestTemplate for PRA Cloud API");
            this.restTemplate = praCloudRestTemplate;
        } else {
            logger.info("Using default RestTemplate for PRA Cloud API");
            this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
        }
    }

    @Override
    public PraFiscalizationResult fiscalize(PraInvoiceModel invoice) {
        String url = properties.getCloud().getApiUrl();
        String token = properties.getCloud().getApiToken();
        String environment = properties.getCloud().getEnvironment();
        
        logger.info("========================================");
        logger.info("PRA Cloud Fiscalization Request");
        logger.info("========================================");
        logger.info("Environment: {}", environment.toUpperCase());
        logger.info("URL: {}", url);
        logger.info("Token: {}...{}", 
            token != null && token.length() > 8 ? token.substring(0, 8) : "NULL",
            token != null && token.length() > 8 ? token.substring(token.length() - 4) : "");
        logger.info("USIN: {}", invoice.usin());
        logger.info("POS ID: {}", invoice.posId());
        logger.info("Date/Time: {}", invoice.dateTime());
        logger.info("Payment Mode: {}", invoice.paymentMode());
        logger.info("Invoice Type: {}", invoice.invoiceType());
        logger.info("----------------------------------------");
        logger.info("Total Sale Value: {}", invoice.totalSaleValue());
        logger.info("Total Tax Charged: {}", invoice.totalTaxCharged());
        logger.info("Discount: {}", invoice.discount());
        logger.info("Total Bill Amount: {}", invoice.totalBillAmount());
        logger.info("Total Quantity: {}", invoice.totalQuantity());
        logger.info("----------------------------------------");
        logger.info("Items Count: {}", invoice.items().size());
        invoice.items().forEach(item -> {
            logger.info("  - {} x {} = {} (Tax: {}, Total: {})",
                item.itemName(), item.quantity(), item.saleValue(), 
                item.taxCharged(), item.totalAmount());
        });
        logger.info("----------------------------------------");
        logger.info("Customer: {}", invoice.buyerName());
        logger.info("Customer Phone: {}", invoice.buyerPhoneNumber());
        logger.info("Customer CNIC: {}", invoice.buyerCnic());
        logger.info("Customer PNTN: {}", invoice.buyerPntn());
        logger.info("========================================");

        // Validate token
        if (token == null || token.isBlank() || "your-production-token-here".equals(token)) {
            logger.error("❌ PRA Cloud token not configured for environment: {}", environment);
            return new PraFiscalizationResult(false, null, null, null, 
                "Cloud API token not configured. Please set pra.cloud." + environment + "-token");
        }

        // Create headers with Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        HttpEntity<PraInvoiceModel> request = new HttpEntity<>(invoice, headers);

        try {
            ResponseEntity<ImsInvoiceResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                ImsInvoiceResponse.class
            );

            logger.info("========================================");
            logger.info("PRA Cloud Fiscalization Response");
            logger.info("========================================");
            logger.info("HTTP Status: {}", response.getStatusCode());
            
            ImsInvoiceResponse body = response.getBody();
            
            if (body == null) {
                logger.error("Response body is NULL");
                return new PraFiscalizationResult(false, null, null, null, "Cloud API response body is null");
            }
            
            logger.info("Invoice Number: {}", body.invoiceNumber());
            logger.info("Code: {}", body.code());
            logger.info("Response: {}", body.response());
            logger.info("Errors: {}", body.errors());
            logger.info("========================================");
            
            if (body.invoiceNumber() == null || body.invoiceNumber().isBlank()) {
                logger.error("Invoice number is missing in response");
                return new PraFiscalizationResult(false, null, null, null, "Cloud API response missing invoice number");
            }

            String qrText = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + body.invoiceNumber();
            String verificationUrl = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + body.invoiceNumber();
            
            logger.info("✅ Fiscalization SUCCESS - Invoice: {}", body.invoiceNumber());
            return new PraFiscalizationResult(true, body.invoiceNumber(), qrText, verificationUrl, body.response());
        } catch (HttpStatusCodeException ex) {
            logger.error("========================================");
            logger.error("PRA Cloud Fiscalization FAILED");
            logger.error("========================================");
            logger.error("HTTP Status: {}", ex.getStatusCode());
            logger.error("Response Body: {}", ex.getResponseBodyAsString());
            logger.error("Headers: {}", ex.getResponseHeaders());
            logger.error("========================================");
            String errorMsg = String.format("PRA Cloud API error (%s): %s", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new PraUnavailableException(errorMsg);
        } catch (Exception ex) {
            logger.error("========================================");
            logger.error("PRA Cloud Fiscalization FAILED");
            logger.error("========================================");
            logger.error("Error: {}", ex.getMessage(), ex);
            logger.error("========================================");
            throw new PraUnavailableException("PRA Cloud API unavailable: " + ex.getMessage());
        }
    }

    @Override
    public PraHealth health() {
        // For cloud API, we can't do a simple GET health check
        // The cloud API only has POST endpoint
        // Return status based on configuration
        String token = properties.getCloud().getApiToken();
        String environment = properties.getCloud().getEnvironment();
        
        if (token == null || token.isBlank() || "your-production-token-here".equals(token)) {
            logger.warn("PRA Cloud health check: token not configured for {}", environment);
            return new PraHealth("UNAVAILABLE", "Cloud API token not configured for " + environment);
        }

        logger.info("PRA Cloud health check: configured for {}", environment);
        return new PraHealth("OK", "Cloud API configured (" + environment + ")");
    }
}
