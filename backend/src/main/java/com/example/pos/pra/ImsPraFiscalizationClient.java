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
        
        logger.info("========================================");
        logger.info("PRA IMS Fiscalization Request");
        logger.info("========================================");
        logger.info("URL: {}", url);
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

        try {
            ImsInvoiceResponse response = restTemplate.postForObject(url, invoice, ImsInvoiceResponse.class);
            
            logger.info("========================================");
            logger.info("PRA IMS Fiscalization Response");
            logger.info("========================================");
            
            if (response == null) {
                logger.error("Response is NULL");
                return new PraFiscalizationResult(false, null, null, null, "IMS response is null");
            }
            
            logger.info("Invoice Number: {}", response.invoiceNumber());
            logger.info("Code: {}", response.code());
            logger.info("Response: {}", response.response());
            logger.info("Errors: {}", response.errors());
            logger.info("========================================");
            
            if (response.invoiceNumber() == null || response.invoiceNumber().isBlank()) {
                logger.error("Invoice number is missing in response");
                return new PraFiscalizationResult(false, null, null, null, "IMS response missing invoice number");
            }

            String qrText = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + response.invoiceNumber();
            String verificationUrl = "https://reg.pra.punjab.gov.pk/IMSFiscalReport/SearchPOSInvoice_Report.aspx?PRAInvNo=" + response.invoiceNumber();
            
            logger.info("✅ Fiscalization SUCCESS - Invoice: {}", response.invoiceNumber());
            return new PraFiscalizationResult(true, response.invoiceNumber(), qrText, verificationUrl, response.response());
        } catch (HttpStatusCodeException ex) {
            logger.error("========================================");
            logger.error("PRA IMS Fiscalization FAILED");
            logger.error("========================================");
            logger.error("HTTP Status: {}", ex.getStatusCode());
            logger.error("Response Body: {}", ex.getResponseBodyAsString());
            logger.error("========================================");
            throw new PraUnavailableException("PRA IMS unavailable (ims)");
        } catch (Exception ex) {
            logger.error("========================================");
            logger.error("PRA IMS Fiscalization FAILED");
            logger.error("========================================");
            logger.error("Error: {}", ex.getMessage(), ex);
            logger.error("========================================");
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
