package com.example.pos.pra;

import com.example.pos.entity.Order;
import com.example.pos.entity.OrderItem;
import com.example.pos.pra.dto.PraInvoiceItem;
import com.example.pos.pra.dto.PraInvoiceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PraInvoiceMapper {
    private static final Logger logger = LoggerFactory.getLogger(PraInvoiceMapper.class);
    private static final DateTimeFormatter IMS_DATE_TIME =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final PraProperties properties;

    public PraInvoiceMapper(PraProperties properties) {
        this.properties = properties;
    }

    public PraInvoiceModel fromOrder(Order order) {
        logger.info("=== PRA Invoice Mapping Started ===");
        logger.info("Order ID: {}, Invoice Number: {}, Payment Mode: {}", 
            order.getId(), order.getInvoiceNumber(), order.getPaymentMode());
        
        int invoiceType = properties.getIms().getInvoiceType();
        BigDecimal gstRate = resolveGstRate(order.getPaymentMode());
        
        logger.info("GST Rate: {}% ({}), Invoice Type: {}", 
            gstRate.multiply(ONE_HUNDRED), gstRate, invoiceType);
        
        List<PraInvoiceItem> items = order.getItems().stream()
            .map(item -> toItem(item, invoiceType, gstRate))
            .toList();

        // Calculate totals from items
        BigDecimal totalSaleValue = items.stream()
            .map(PraInvoiceItem::saleValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalTaxCharged = items.stream()
            .map(PraInvoiceItem::taxCharged)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalQuantity = items.stream()
            .map(PraInvoiceItem::quantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDiscount = defaultZero(order.getDiscount());
        BigDecimal totalFurtherTax = BigDecimal.ZERO;

        // PRA Formula: TotalBillAmount = TotalSaleValue + TotalTaxCharged - Discount
        BigDecimal totalBillAmount = totalSaleValue
            .add(totalTaxCharged)
            .subtract(totalDiscount)
            .setScale(2, RoundingMode.HALF_UP);
        
        if (totalBillAmount.signum() < 0) {
            totalBillAmount = BigDecimal.ZERO;
        }

        logger.info("=== Invoice Totals Calculation ===");
        logger.info("Total Sale Value: {}", totalSaleValue);
        logger.info("Total Tax Charged: {}", totalTaxCharged);
        logger.info("Total Discount: {}", totalDiscount);
        logger.info("Total Bill Amount: {} (Formula: {} + {} - {})", 
            totalBillAmount, totalSaleValue, totalTaxCharged, totalDiscount);
        logger.info("Total Quantity: {}", totalQuantity);

        PraInvoiceModel model = new PraInvoiceModel(
            properties.getIms().getPosId(),
            order.getInvoiceNumber(),
            IMS_DATE_TIME.format(order.getCreatedAt()),
            totalSaleValue,
            totalTaxCharged,
            totalBillAmount,
            totalQuantity,
            mapPaymentMode(order.getPaymentMode()),
            invoiceType,
            items,
            "",
            null,
            order.getCustomerName(),
            fallback(order.getCustomerPntn(), order.getCustomerTaxId()),
            order.getCustomerCnic(),
            order.getCustomerPhone(),
            totalDiscount,
            totalFurtherTax
        );
        
        logger.info("=== PRA Invoice Mapping Completed ===");
        return model;
    }

    private PraInvoiceItem toItem(OrderItem orderItem, int invoiceType, BigDecimal gstRate) {
        var item = orderItem.getItem();
        if (item.getItemCode() == null || item.getItemCode().isBlank()) {
            throw new IllegalArgumentException("Item code is required for fiscalization");
        }
        
        BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());
        BigDecimal lineTotal = defaultZero(orderItem.getLineTotal());
        
        // PRA Format:
        // SaleValue = Price × Quantity (EXCLUDING tax)
        // TaxCharged = SaleValue × TaxRate
        // TotalAmount = SaleValue + TaxCharged
        
        // Our lineTotal is price × quantity (without tax)
        BigDecimal saleValue = lineTotal.setScale(2, RoundingMode.HALF_UP);
        
        // PRA expects TaxRate as percentage (e.g., 16 for 16%, not 0.16)
        BigDecimal taxRate = gstRate.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
        
        // Calculate tax on the sale value
        BigDecimal taxCharged = saleValue.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
        
        // Total = SaleValue + Tax (no item-level discount in current implementation)
        BigDecimal totalAmount = saleValue.add(taxCharged).setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal itemDiscount = BigDecimal.ZERO;
        BigDecimal furtherTax = BigDecimal.ZERO;

        String pctCode = item.getPctCode();
        if (pctCode == null || pctCode.isBlank()) {
            pctCode = properties.getIms().getDefaultPctCode();
        }

        logger.info("  Item: {} ({})", item.getName(), item.getItemCode());
        logger.info("    Quantity: {}, Unit Price: {}, Line Total: {}", 
            quantity, orderItem.getUnitPrice(), lineTotal);
        logger.info("    Sale Value: {}, Tax Rate: {}%, Tax Charged: {}, Total Amount: {}", 
            saleValue, taxRate, taxCharged, totalAmount);
        logger.info("    PCT Code: {}", pctCode);

        return new PraInvoiceItem(
            item.getItemCode(),
            item.getName(),
            pctCode,
            quantity,
            taxRate,
            saleValue,
            taxCharged,
            totalAmount,
            invoiceType,
            itemDiscount,
            furtherTax,
            null
        );
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String fallback(String primary, String secondary) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return secondary;
    }

    private int mapPaymentMode(com.example.pos.entity.PaymentMode paymentMode) {
        if (paymentMode == com.example.pos.entity.PaymentMode.CARD) {
            return 2;
        }
        return 1;
    }

    private BigDecimal resolveGstRate(com.example.pos.entity.PaymentMode paymentMode) {
        if (paymentMode == com.example.pos.entity.PaymentMode.CARD) {
            return BigDecimal.valueOf(properties.getIms().getCardGstRate());
        }
        return BigDecimal.valueOf(properties.getIms().getCashGstRate());
    }
}
