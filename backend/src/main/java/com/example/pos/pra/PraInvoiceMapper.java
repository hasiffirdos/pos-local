package com.example.pos.pra;

import com.example.pos.entity.Order;
import com.example.pos.entity.OrderItem;
import com.example.pos.entity.PaymentMode;
import com.example.pos.pra.dto.PraInvoiceItem;
import com.example.pos.pra.dto.PraInvoiceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PraInvoiceMapper {
    private static final Logger logger = LoggerFactory.getLogger(PraInvoiceMapper.class);
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Karachi"));
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final PraProperties props;

    public PraInvoiceMapper(PraProperties props) {
        this.props = props;
    }

    public PraInvoiceModel fromOrder(Order order) {
        logger.info("Mapping order {} to PRA invoice", order.getId());
        
        BigDecimal gstRate = getGstRate(order.getPaymentMode());
        BigDecimal rawTotal = calcRawTotal(order);
        BigDecimal discount = defaultZero(order.getDiscount());
        
        List<PraInvoiceItem> items = order.getItems().stream()
            .map(item -> mapItem(item, gstRate, discount, rawTotal))
            .toList();

        BigDecimal totalSale = sum(items, PraInvoiceItem::saleValue);
        BigDecimal totalTax = sum(items, PraInvoiceItem::taxCharged);
        BigDecimal totalQty = sum(items, PraInvoiceItem::quantity);
        BigDecimal totalBill = totalSale.add(totalTax).max(BigDecimal.ZERO);

        logger.info("Invoice totals - Sale: {}, Tax: {}, Bill: {}", totalSale, totalTax, totalBill);

        return new PraInvoiceModel(
            props.getPosId(),
            order.getInvoiceNumber(),
            DATE_FORMAT.format(order.getCreatedAt()),
            totalSale,
            totalTax,
            totalBill,
            totalQty,
            mapPaymentMode(order.getPaymentMode()),
            props.getInvoiceType(),
            items,
            "",
            null,
            order.getCustomerName(),
            firstNonBlank(order.getCustomerPntn(), order.getCustomerTaxId()),
            order.getCustomerCnic(),
            order.getCustomerPhone(),
            discount,
            BigDecimal.ZERO
        );
    }

    private PraInvoiceItem mapItem(OrderItem orderItem, BigDecimal gstRate, 
                                    BigDecimal totalDiscount, BigDecimal rawTotal) {
        var item = orderItem.getItem();
        
        if (item.getItemCode() == null || item.getItemCode().isBlank()) {
            throw new IllegalArgumentException("Item code required for fiscalization");
        }
        
        BigDecimal qty = BigDecimal.valueOf(orderItem.getQuantity());
        BigDecimal lineTotal = defaultZero(orderItem.getLineTotal());
        
        // Proportional discount
        BigDecimal itemDiscount = BigDecimal.ZERO;
        if (rawTotal.signum() > 0 && totalDiscount.signum() > 0) {
            itemDiscount = lineTotal.divide(rawTotal, 10, RoundingMode.HALF_UP)
                .multiply(totalDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        BigDecimal saleValue = lineTotal.subtract(itemDiscount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxRate = gstRate.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxCharged = saleValue.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = saleValue.add(taxCharged).setScale(2, RoundingMode.HALF_UP);
        
        String pctCode = item.getPctCode();
        if (pctCode == null || pctCode.isBlank()) {
            pctCode = props.getDefaultPctCode();
        }

        logger.debug("Item {} - Sale: {}, Tax: {}, PCT: {}", item.getName(), saleValue, taxCharged, pctCode);

        return new PraInvoiceItem(
            item.getItemCode(),
            item.getName(),
            pctCode,
            qty,
            taxRate,
            saleValue,
            taxCharged,
            totalAmount,
            props.getInvoiceType(),
            itemDiscount,
            BigDecimal.ZERO,
            null
        );
    }

    private BigDecimal getGstRate(PaymentMode mode) {
        return BigDecimal.valueOf(mode == PaymentMode.CARD ? props.getCardGstRate() : props.getCashGstRate());
    }

    private int mapPaymentMode(PaymentMode mode) {
        return mode == PaymentMode.CARD ? 2 : 1;
    }

    private BigDecimal calcRawTotal(Order order) {
        return order.getItems().stream()
            .map(i -> defaultZero(i.getLineTotal()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sum(List<PraInvoiceItem> items, java.util.function.Function<PraInvoiceItem, BigDecimal> getter) {
        return items.stream().map(getter).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal defaultZero(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
