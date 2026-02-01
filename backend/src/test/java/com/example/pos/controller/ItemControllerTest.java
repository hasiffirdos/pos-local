package com.example.pos.controller;

import com.example.pos.dto.ItemRequest;
import com.example.pos.dto.ItemResponse;
import com.example.pos.exception.GlobalExceptionHandler;
import com.example.pos.service.ItemService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void listItems_returnsItems() throws Exception {
        List<ItemResponse> items = List.of(
            new ItemResponse(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "Espresso",
                new BigDecimal("2.50"),
                "Beverage",
                "ESP-001",
                "00000000",
                true,
                Instant.parse("2024-05-01T10:15:30Z"),
                Instant.parse("2024-05-01T10:15:30Z")
            ),
            new ItemResponse(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "Muffin",
                new BigDecimal("3.25"),
                "Food",
                "MUF-001",
                "00000000",
                true,
                Instant.parse("2024-05-02T09:00:00Z"),
                Instant.parse("2024-05-02T09:00:00Z")
            )
        );

        when(itemService.listActive()).thenReturn(items);

        mockMvc.perform(get("/api/items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
            .andExpect(jsonPath("$[0].name").value("Espresso"))
            .andExpect(jsonPath("$[1].id").value("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"))
            .andExpect(jsonPath("$[1].category").value("Food"));
    }

    @Test
    void createItem_returnsCreated() throws Exception {
        UUID id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        ItemResponse response = new ItemResponse(
            id,
            "Latte",
            new BigDecimal("4.75"),
            "Beverage",
            "LAT-001",
            "00000000",
            true,
            Instant.parse("2024-05-03T08:00:00Z"),
            Instant.parse("2024-05-03T08:00:00Z")
        );

        when(itemService.create(any(ItemRequest.class))).thenReturn(response);

        ItemRequest request = new ItemRequest(
            "Latte",
            new BigDecimal("4.75"),
            "Beverage",
            "LAT-001",
            "00000000"
        );

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("cccccccc-cccc-cccc-cccc-cccccccccccc"))
            .andExpect(jsonPath("$.name").value("Latte"))
            .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void createItem_withInvalidBody_returnsBadRequest() throws Exception {
        String payload = """
            {
              "name": "",
              "price": -1,
              "category": "",
              "itemCode": "",
              "pctCode": ""
            }
            """;

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message", containsString("must not be blank")))
            .andExpect(jsonPath("$.path").value("/api/items"));
    }

    @Test
    void updateItem_returnsOk() throws Exception {
        UUID id = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        ItemResponse response = new ItemResponse(
            id,
            "Mocha",
            new BigDecimal("5.25"),
            "Beverage",
            "MOC-001",
            "00000000",
            true,
            Instant.parse("2024-05-04T11:30:00Z"),
            Instant.parse("2024-05-04T11:30:00Z")
        );

        when(itemService.update(eq(id), any(ItemRequest.class))).thenReturn(response);

        ItemRequest request = new ItemRequest(
            "Mocha",
            new BigDecimal("5.25"),
            "Beverage",
            "MOC-001",
            "00000000"
        );

        mockMvc.perform(put("/api/items/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("dddddddd-dddd-dddd-dddd-dddddddddddd"))
            .andExpect(jsonPath("$.price").value(5.25));
    }

    @Test
    void updateItem_notFound_returnsNotFound() throws Exception {
        UUID id = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        when(itemService.update(eq(id), any(ItemRequest.class)))
            .thenThrow(new EntityNotFoundException("Item not found"));

        ItemRequest request = new ItemRequest(
            "Tea",
            new BigDecimal("2.00"),
            "Beverage",
            "TEA-001",
            "00000000"
        );

        mockMvc.perform(put("/api/items/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Item not found"))
            .andExpect(jsonPath("$.path").value("/api/items/" + id));
    }

    @Test
    void deleteItem_returnsNoContent() throws Exception {
        UUID id = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        doNothing().when(itemService).softDelete(id);

        mockMvc.perform(delete("/api/items/{id}", id))
            .andExpect(status().isNoContent());
    }
}
