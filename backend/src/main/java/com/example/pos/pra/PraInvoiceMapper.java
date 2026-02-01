package com.example.pos.pra;

import com.example.pos.entity.Order;
import com.example.pos.entity.OrderItem;
import com.example.pos.pra.dto.PraInvoiceItem;
import com.example.pos.pra.dto.PraInvoiceModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PraInvoiceMapper {
    private static final DateTimeFormatter IMS_DATE_TIME =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final PraProperties properties;

    public PraInvoiceMapper(PraProperties properties) {
        this.properties = properties;
    }

    public PraInvoiceModel fromOrder(Order order) {
        int invoiceType = properties.getIms().getInvoiceType();
        BigDecimal gstRate = resolveGstRate(order.getPaymentMode());
        List<PraInvoiceItem> items = order.getItems().stream()
            .map(item -> toItem(item, invoiceType, gstRate))
            .toList();

        BigDecimal totalSaleValue = items.stream()
            .map(PraInvoiceItem::saleValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTaxCharged = order.getTax() == null ? BigDecimal.ZERO : order.getTax();
        BigDecimal totalBillAmount = items.stream()
            .map(PraInvoiceItem::totalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalQuantity = items.stream()
            .map(PraInvoiceItem::quantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = defaultZero(order.getDiscount());
        BigDecimal totalFurtherTax = BigDecimal.ZERO;

        BigDecimal discountedTotal = totalBillAmount.subtract(totalDiscount);
        if (discountedTotal.signum() < 0) {
            discountedTotal = BigDecimal.ZERO;
        }

        return new PraInvoiceModel(
            properties.getIms().getPosId(),
            order.getInvoiceNumber(),
            IMS_DATE_TIME.format(order.getCreatedAt()),
            totalSaleValue,
            totalTaxCharged,
            discountedTotal,
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
    }

    private PraInvoiceItem toItem(OrderItem orderItem, int invoiceType, BigDecimal gstRate) {
        var item = orderItem.getItem();
        if (item.getItemCode() == null || item.getItemCode().isBlank()) {
            throw new IllegalArgumentException("Item code is required for fiscalization");
        }
        BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());
        BigDecimal lineTotal = defaultZero(orderItem.getLineTotal());
        BigDecimal furtherTax = BigDecimal.ZERO;
        BigDecimal saleValue = lineTotal.max(BigDecimal.ZERO);
        // PRA expects TaxRate as percentage integer (e.g., 16) not decimal (e.g., 0.16)
        BigDecimal taxRate = gstRate.multiply(ONE_HUNDRED);
        BigDecimal taxCharged = saleValue.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = saleValue.add(taxCharged);

        String pctCode = item.getPctCode();
        if (pctCode == null || pctCode.isBlank()) {
            pctCode = properties.getIms().getDefaultPctCode();
        }

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
            BigDecimal.ZERO,
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
