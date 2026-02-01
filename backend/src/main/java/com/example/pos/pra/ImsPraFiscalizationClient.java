package com.example.pos.pra;

import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceModel;
import com.example.pos.pra.ims.ImsInvoiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Component
public class ImsPraFiscalizationClient implements PraFiscalizationClient {
    private static final Logger logger = LoggerFactory.getLogger(ImsPraFiscalizationClient.class);
    private final PraProperties properties;
    private final RestTemplate restTemplate;

    public ImsPraFiscalizationClient(PraProperties properties, RestTemplateBuilder restTemplateBuilder) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public PraFiscalizationResult fiscalize(PraInvoiceModel invoice) {
        String baseUrl = properties.getImsBaseUrl();
        String url = baseUrl + "/api/IMSFiscal/GetInvoiceNumberByModel";
        logger.info("PRA IMS fiscalize request -> {} for USIN {}", url, invoice.usin());

        try {
            ImsInvoiceResponse response = restTemplate.postForObject(url, invoice, ImsInvoiceResponse.class);
            if (response == null || response.invoiceNumber() == null || response.invoiceNumber().isBlank()) {
                return new PraFiscalizationResult(false, null, null, null, "IMS response missing invoice number");
            }

            String qrText = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + response.invoiceNumber();
            String verificationUrl = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + response.invoiceNumber();
            return new PraFiscalizationResult(true, response.invoiceNumber(), qrText, verificationUrl, response.response());
        } catch (HttpStatusCodeException ex) {
            logger.warn("PRA IMS fiscalize failed with status {}", ex.getStatusCode());
            throw new PraUnavailableException("PRA IMS unavailable (ims)");
        } catch (Exception ex) {
            logger.warn("PRA IMS fiscalize failed", ex);
            throw new PraUnavailableException("PRA IMS unavailable (ims)");
        }
    }

    @Override
    public PraHealth health() {
        String baseUrl = properties.getImsBaseUrl();
        String url = baseUrl + "/api/IMSFiscal/Get";
        logger.info("PRA IMS health check -> {}", url);

        try {
            restTemplate.getForEntity(url, String.class);
            return new PraHealth("OK", "IMS reachable");
        } catch (Exception ex) {
            logger.warn("PRA IMS health check failed", ex);
            return new PraHealth("UNAVAILABLE", "IMS unreachable");
        }
    }
}
