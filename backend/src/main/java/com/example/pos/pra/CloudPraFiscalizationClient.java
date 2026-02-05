package com.example.pos.pra;

import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceModel;
import com.example.pos.pra.ims.ImsInvoiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private final PraProperties props;
    private final RestTemplate restTemplate;

    public CloudPraFiscalizationClient(PraProperties props, RestTemplateBuilder builder) {
        this.props = props;
        this.restTemplate = builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }

    @Override
    public PraFiscalizationResult fiscalize(PraInvoiceModel invoice) {
        String url = props.getApiUrl();
        String token = props.getApiToken();
        
        logger.info("PRA Cloud Request - URL: {}, USIN: {}, POS: {}", url, invoice.usin(), invoice.posId());

        if (token == null || token.isBlank() || token.contains("placeholder")) {
            logger.error("PRA token not configured");
            return new PraFiscalizationResult(false, null, null, null, "API token not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        try {
            ResponseEntity<ImsInvoiceResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(invoice, headers), ImsInvoiceResponse.class
            );

            ImsInvoiceResponse body = response.getBody();
            if (body == null || body.invoiceNumber() == null || body.invoiceNumber().isBlank()) {
                logger.error("PRA response missing invoice number");
                return new PraFiscalizationResult(false, null, null, null, "Missing invoice number in response");
            }

            String verifyUrl = props.getVerifyUrlBase() + body.invoiceNumber();
            logger.info("PRA Success - Invoice: {}", body.invoiceNumber());
            
            return new PraFiscalizationResult(true, body.invoiceNumber(), verifyUrl, verifyUrl, body.response());
            
        } catch (HttpStatusCodeException ex) {
            logger.error("PRA Error - Status: {}, Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new PraUnavailableException("PRA API error: " + ex.getStatusCode());
        } catch (Exception ex) {
            logger.error("PRA Error: {}", ex.getMessage());
            throw new PraUnavailableException("PRA API unavailable: " + ex.getMessage());
        }
    }

    @Override
    public PraHealth health() {
        String token = props.getApiToken();
        if (token == null || token.isBlank() || token.contains("placeholder")) {
            return new PraHealth("UNAVAILABLE", "Token not configured");
        }
        return new PraHealth("OK", "Cloud API configured (" + props.getEnvironment() + ")");
    }
}
