package com.example.pos.controller;

import com.example.pos.dto.DailySalesReportResponse;
import com.example.pos.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void dailySales_returnsReport() throws Exception {
        LocalDate date = LocalDate.parse("2024-05-06");
        DailySalesReportResponse response = new DailySalesReportResponse(
            date,
            3,
            new BigDecimal("42.50")
        );

        when(reportService.dailySales(eq(date))).thenReturn(response);

        mockMvc.perform(get("/api/reports/daily-sales")
                .queryParam("date", "2024-05-06")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").value("2024-05-06"))
            .andExpect(jsonPath("$.orderCount").value(3))
            .andExpect(jsonPath("$.totalSales").value(42.50));
    }

    @Test
    void dailySales_missingDate_returnsServerError() throws Exception {
        mockMvc.perform(get("/api/reports/daily-sales"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Unexpected error"));

        verifyNoInteractions(reportService);
    }
}
