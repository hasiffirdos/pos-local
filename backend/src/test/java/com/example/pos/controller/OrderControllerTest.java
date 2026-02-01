package com.example.pos.controller;

import com.example.pos.dto.OrderItemRequest;
import com.example.pos.dto.OrderItemResponse;
import com.example.pos.dto.OrderResponse;
import com.example.pos.entity.OrderStatus;
import com.example.pos.exception.GlobalExceptionHandler;
import com.example.pos.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_returnsCreated() throws Exception {
        UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        OrderResponse response = new OrderResponse(
            orderId,
            "INV-20240505-AAAA1111",
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            OrderStatus.DRAFT,
            "CASH",
            new BigDecimal("0.16"),
            BigDecimal.ZERO,
            null,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            Instant.parse("2024-05-05T09:15:00Z"),
            List.of()
        );

        when(orderService.createOrder()).thenReturn(response);

        mockMvc.perform(post("/api/orders"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void getOrder_returnsOrder() throws Exception {
        UUID orderId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        OrderItemResponse item = new OrderItemResponse(
            UUID.fromString("33333333-3333-3333-3333-333333333333"),
            UUID.fromString("44444444-4444-4444-4444-444444444444"),
            "Croissant",
            2,
            new BigDecimal("3.00"),
            new BigDecimal("6.00")
        );
        OrderResponse response = new OrderResponse(
            orderId,
            "INV-20240505-BBBB2222",
            "FISC-123",
            "PRA|FISC-123|INV-1",
            "https://pra.gov/verify/FISC-123",
            new BigDecimal("6.00"),
            BigDecimal.ZERO,
            new BigDecimal("6.00"),
            OrderStatus.DRAFT,
            "CASH",
            new BigDecimal("0.16"),
            BigDecimal.ZERO,
            "Casey",
            "555-0101",
            "12345-1234567-8",
            "1234567-8",
            null,
            "Pickup",
            new BigDecimal("0.00"),
            Instant.parse("2024-05-05T10:00:00Z"),
            List.of(item)
        );

        when(orderService.getOrder(orderId)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("22222222-2222-2222-2222-222222222222"))
            .andExpect(jsonPath("$.items[0].itemName").value("Croissant"))
            .andExpect(jsonPath("$.total").value(6.00));
    }

    @Test
    void getOrder_notFound_returnsNotFound() throws Exception {
        UUID orderId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        when(orderService.getOrder(orderId))
            .thenThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Order not found"))
            .andExpect(jsonPath("$.path").value("/api/orders/" + orderId));
    }

    @Test
    void addOrUpdateItem_returnsOrder() throws Exception {
        UUID orderId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        OrderItemResponse item = new OrderItemResponse(
            UUID.fromString("77777777-7777-7777-7777-777777777777"),
            UUID.fromString("88888888-8888-8888-8888-888888888888"),
            "Bagel",
            1,
            new BigDecimal("2.50"),
            new BigDecimal("2.50")
        );
        OrderResponse response = new OrderResponse(
            orderId,
            "INV-20240505-CCCC3333",
            null,
            null,
            null,
            new BigDecimal("2.50"),
            BigDecimal.ZERO,
            new BigDecimal("2.50"),
            OrderStatus.DRAFT,
            "CASH",
            new BigDecimal("0.16"),
            BigDecimal.ZERO,
            null,
            null,
            null,
            null,
            null,
            null,
            BigDecimal.ZERO,
            Instant.parse("2024-05-05T11:00:00Z"),
            List.of(item)
        );

        when(orderService.addOrUpdateItem(eq(orderId), any(OrderItemRequest.class)))
            .thenReturn(response);

        OrderItemRequest request = new OrderItemRequest(
            UUID.fromString("88888888-8888-8888-8888-888888888888"),
            1
        );

        mockMvc.perform(post("/api/orders/{id}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].quantity").value(1))
            .andExpect(jsonPath("$.subtotal").value(2.50));
    }

    @Test
    void addOrUpdateItem_withInvalidBody_returnsBadRequest() throws Exception {
        UUID orderId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        String payload = """
            {
              "itemId": "88888888-8888-8888-8888-888888888888",
              "quantity": 0
            }
            """;

        mockMvc.perform(post("/api/orders/{id}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message", containsString("must be greater than 0")))
            .andExpect(jsonPath("$.path").value("/api/orders/" + orderId + "/items"));
    }

    @Test
    void checkout_withIllegalArgument_returnsBadRequest() throws Exception {
        UUID orderId = UUID.fromString("aaaaaaaa-1111-1111-1111-111111111111");
        when(orderService.checkout(orderId))
            .thenThrow(new IllegalArgumentException("Only DRAFT orders can be checked out"));

        mockMvc.perform(post("/api/orders/{id}/checkout", orderId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Only DRAFT orders can be checked out"))
            .andExpect(jsonPath("$.path").value("/api/orders/" + orderId + "/checkout"));
    }
}
