package com.example.pos.service;

import com.example.pos.dto.OrderItemRequest;
import com.example.pos.dto.OrderItemResponse;
import com.example.pos.dto.OrderResponse;
import com.example.pos.dto.OrderUpdateRequest;
import com.example.pos.entity.Item;
import com.example.pos.entity.Order;
import com.example.pos.entity.OrderItem;
import com.example.pos.entity.OrderStatus;
import com.example.pos.entity.PaymentMode;
import com.example.pos.pra.PraFiscalizationClient;
import com.example.pos.pra.PraInvoiceMapper;
import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.repository.ItemRepository;
import com.example.pos.repository.OrderItemRepository;
import com.example.pos.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final PraFiscalizationClient fiscalizationClient;
    private final PraInvoiceMapper praInvoiceMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ItemRepository itemRepository,
                        @Qualifier("praFiscalizationClient") PraFiscalizationClient fiscalizationClient,
                        PraInvoiceMapper praInvoiceMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.fiscalizationClient = fiscalizationClient;
        this.praInvoiceMapper = praInvoiceMapper;
    }

    public OrderResponse createOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.DRAFT);
        order.setPaymentMode(PaymentMode.CASH);
        order.setInvoiceNumber(generateInvoiceNumber());
        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> listOrders(OrderStatus status) {
        List<Order> orders = status == null
            ? orderRepository.findAllByOrderByCreatedAtDesc()
            : orderRepository.findByStatusOrderByCreatedAtDesc(status);
        return orders.stream().map(this::toResponse).toList();
    }

    public OrderResponse getOrder(UUID id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return toResponse(order);
    }

    public OrderResponse addOrUpdateItem(UUID orderId, OrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT orders can be modified");
        }

        Item item = itemRepository.findById(request.itemId())
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        OrderItem orderItem = orderItemRepository.findByOrderIdAndItemId(orderId, request.itemId())
            .orElseGet(() -> {
                OrderItem created = new OrderItem();
                created.setOrder(order);
                created.setItem(item);
                order.getItems().add(created);
                return created;
            });

        orderItem.setQuantity(request.quantity());
        orderItem.setUnitPrice(item.getPrice());
        orderItem.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(request.quantity())));

        orderItemRepository.save(orderItem);
        recalcTotals(order);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse checkout(UUID orderId) {
        logger.info("╔════════════════════════════════════════╗");
        logger.info("║     ORDER CHECKOUT STARTED             ║");
        logger.info("╚════════════════════════════════════════╝");
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        logger.info("Order ID: {}", order.getId());
        logger.info("Invoice Number: {}", order.getInvoiceNumber());
        logger.info("Status: {}", order.getStatus());
        logger.info("Payment Mode: {}", order.getPaymentMode());
        logger.info("Items Count: {}", order.getItems().size());
        
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT orders can be checked out");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty order");
        }
        if (order.getPaymentMode() == null) {
            throw new IllegalArgumentException("Payment mode is required");
        }
        
        logger.info("Recalculating order totals...");
        recalcTotals(order);
        
        logger.info("Order Totals:");
        logger.info("  Subtotal: {}", order.getSubtotal());
        logger.info("  Tax: {}", order.getTax());
        logger.info("  Discount: {}", order.getDiscount());
        logger.info("  Total: {}", order.getTotal());
        logger.info("  GST Rate: {}", order.getGstRate());
        
        logger.info("Starting PRA fiscalization...");
        PraFiscalizationResult result = fiscalizationClient.fiscalize(praInvoiceMapper.fromOrder(order));
        
        if (result.success()) {
            logger.info("✅ PRA Fiscalization SUCCESS");
            logger.info("Fiscal Invoice Number: {}", result.fiscalInvoiceNumber());
            logger.info("QR Text: {}", result.qrText());
            logger.info("Verification URL: {}", result.verificationUrl());
            logger.info("Message: {}", result.message());
        } else {
            logger.error("❌ PRA Fiscalization FAILED");
            logger.error("Message: {}", result.message());
        }
        
        order.setFiscalInvoiceNumber(result.fiscalInvoiceNumber());
        order.setFiscalQrText(result.qrText());
        order.setFiscalVerificationUrl(result.verificationUrl());
        order.setStatus(OrderStatus.PAID);
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with status: {}", savedOrder.getStatus());
        
        logger.info("╔════════════════════════════════════════╗");
        logger.info("║     ORDER CHECKOUT COMPLETED           ║");
        logger.info("╚════════════════════════════════════════╝");
        
        return toResponse(savedOrder);
    }

    public OrderResponse updateOrder(UUID orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (request.customerName() != null) {
            order.setCustomerName(request.customerName());
        }
        if (request.customerPhone() != null) {
            order.setCustomerPhone(request.customerPhone());
        }
        if (request.customerCnic() != null) {
            order.setCustomerCnic(request.customerCnic());
        }
        if (request.customerPntn() != null) {
            order.setCustomerPntn(request.customerPntn());
        }
        if (request.customerTaxId() != null) {
            order.setCustomerTaxId(request.customerTaxId());
        }
        if (request.notes() != null) {
            order.setNotes(request.notes());
        }
        if (request.discount() != null) {
            order.setDiscount(request.discount());
        }
        if (request.paymentMode() != null) {
            if (order.getStatus() != OrderStatus.DRAFT) {
                throw new IllegalArgumentException("Payment mode can only be changed in DRAFT");
            }
            order.setPaymentMode(PaymentMode.valueOf(request.paymentMode()));
        }
        recalcTotals(order);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Paid orders cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse removeItem(UUID orderId, UUID itemId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT orders can be modified");
        }
        OrderItem orderItem = orderItemRepository.findByOrderIdAndItemId(orderId, itemId)
            .orElseThrow(() -> new EntityNotFoundException("Order item not found"));
        order.getItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
        recalcTotals(order);
        return toResponse(orderRepository.save(order));
    }

    private void recalcTotals(Order order) {
        logger.debug("Recalculating order totals for order: {}", order.getId());
        
        BigDecimal subtotal = order.getItems().stream()
            .map(OrderItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal orderDiscount = order.getDiscount() == null ? BigDecimal.ZERO : order.getDiscount();
        BigDecimal discounted = subtotal.subtract(orderDiscount);
        if (discounted.signum() < 0) {
            discounted = BigDecimal.ZERO;
        }

        PaymentMode mode = order.getPaymentMode() == null ? PaymentMode.CASH : order.getPaymentMode();
        order.setPaymentMode(mode);
        BigDecimal gstRate = resolveGstRate(mode);
        BigDecimal tax = discounted.multiply(gstRate)
            .setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal total = discounted.add(tax).setScale(2, java.math.RoundingMode.HALF_UP);

        order.setSubtotal(subtotal.setScale(2, java.math.RoundingMode.HALF_UP));
        order.setTax(tax);
        order.setTotal(total);
        order.setGstRate(gstRate);
        order.setGstAmount(tax);
        
        logger.debug("Totals calculated - Subtotal: {}, Discount: {}, Tax: {}, Total: {}", 
            subtotal, orderDiscount, tax, total);
    }

    private BigDecimal resolveGstRate(PaymentMode paymentMode) {
        if (paymentMode == PaymentMode.CARD) {
            return new BigDecimal("0.05");
        }
        return new BigDecimal("0.16");
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(this::toItemResponse)
            .toList();
        return new OrderResponse(
            order.getId(),
            order.getInvoiceNumber(),
            order.getFiscalInvoiceNumber(),
            order.getFiscalQrText(),
            order.getFiscalVerificationUrl(),
            order.getSubtotal(),
            order.getTax(),
            order.getTotal(),
            order.getStatus(),
            order.getPaymentMode() != null ? order.getPaymentMode().name() : null,
            order.getGstRate(),
            order.getGstAmount(),
            order.getCustomerName(),
            order.getCustomerPhone(),
            order.getCustomerCnic(),
            order.getCustomerPntn(),
            order.getCustomerTaxId(),
            order.getNotes(),
            order.getDiscount(),
            order.getCreatedAt(),
            items
        );
    }

    private OrderItemResponse toItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
            orderItem.getId(),
            orderItem.getItem().getId(),
            orderItem.getItem().getName(),
            orderItem.getQuantity(),
            orderItem.getUnitPrice(),
            orderItem.getLineTotal()
        );
    }

    private String generateInvoiceNumber() {
        String date = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.BASIC_ISO_DATE);
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "INV-" + date + "-" + suffix;
    }
}
