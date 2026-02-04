package com.example.pos.pra;

import com.example.pos.exception.GlobalExceptionHandler;
import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceItem;
import com.example.pos.pra.dto.PraInvoiceModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PraController.class)
@Import(GlobalExceptionHandler.class)
class PraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PraFiscalizationClient fiscalizationClient;

//    @Test
//    void health_returnsStatus() throws Exception {
//        when(fiscalizationClient.health()).thenReturn(new PraHealth("OK", "Stub client ready"));
//
//        mockMvc.perform(get("/api/pra/health"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.status").value("OK"))
//            .andExpect(jsonPath("$.details").value("Stub client ready"));
//    }
//
//    @Test
//    void fiscalize_returnsResult() throws Exception {
//        PraFiscalizationResult result = new PraFiscalizationResult(
//            true,
//            "FISC-ABC123",
//            "PRA|FISC-ABC123|INV-1",
//            "https://pra.gov/verify/FISC-ABC123",
//            "Fiscalized (stub)"
//        );
//        when(fiscalizationClient.fiscalize(any(PraInvoiceModel.class))).thenReturn(result);
//
//        mockMvc.perform(post("/api/pra/fiscalize")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(sampleInvoice())))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.fiscalInvoiceNumber").value("FISC-ABC123"));
//    }
//
//    @Test
//    void fiscalize_handlesStubFailure() throws Exception {
//        when(fiscalizationClient.fiscalize(any(PraInvoiceModel.class)))
//            .thenThrow(new PraUnavailableException("PRA IMS unavailable (stub)"));
//
//        mockMvc.perform(post("/api/pra/fiscalize")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(sampleInvoice())))
//            .andExpect(status().isBadGateway())
//            .andExpect(jsonPath("$.message").value("PRA IMS unavailable (stub)"))
//            .andExpect(jsonPath("$.status").value(502));
//    }

    private PraInvoiceModel sampleInvoice() {
        PraInvoiceItem item = new PraInvoiceItem(
            "SKU-1",
            "Sample",
            "00000000",
            new BigDecimal("1"),
            BigDecimal.ZERO,
            new BigDecimal("10.00"),
            BigDecimal.ZERO,
            new BigDecimal("10.00"),
            1,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null
        );
        return new PraInvoiceModel(
            1L,
            "INV-1",
            "2024-05-10 10:15:30",
            new BigDecimal("10.00"),
            BigDecimal.ZERO,
            new BigDecimal("10.00"),
            new BigDecimal("1"),
            1,
            1,
            List.of(item),
            "",
            null,
            "Buyer",
            null,
            null,
            null,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }
}
