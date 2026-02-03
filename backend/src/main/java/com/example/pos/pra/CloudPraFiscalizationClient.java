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
        
        logger.info("PRA Cloud fiscalize request -> {} ({}) for USIN {}", url, environment, invoice.usin());

        // Validate token
        if (token == null || token.isBlank() || "your-production-token-here".equals(token)) {
            logger.error("PRA Cloud token not configured for environment: {}", environment);
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

            ImsInvoiceResponse body = response.getBody();
            if (body == null || body.invoiceNumber() == null || body.invoiceNumber().isBlank()) {
                logger.error("PRA Cloud response missing invoice number");
                return new PraFiscalizationResult(false, null, null, null, "Cloud API response missing invoice number");
            }

            logger.info("PRA Cloud fiscalize success -> Invoice: {}, Code: {}", body.invoiceNumber(), body.code());

            String qrText = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + body.invoiceNumber();
            String verificationUrl = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + body.invoiceNumber();
            
            return new PraFiscalizationResult(true, body.invoiceNumber(), qrText, verificationUrl, body.response());
        } catch (HttpStatusCodeException ex) {
            logger.error("PRA Cloud fiscalize failed with status {} - Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            String errorMsg = String.format("PRA Cloud API error (%s): %s", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new PraUnavailableException(errorMsg);
        } catch (Exception ex) {
            logger.error("PRA Cloud fiscalize failed", ex);
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
