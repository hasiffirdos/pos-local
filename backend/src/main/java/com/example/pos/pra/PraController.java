package com.example.pos.pra;

import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pra")
public class PraController {
    private final PraFiscalizationClient fiscalizationClient;

    public PraController(@Qualifier("praFiscalizationClient") PraFiscalizationClient fiscalizationClient) {
        this.fiscalizationClient = fiscalizationClient;
    }

    @GetMapping("/health")
    public PraHealth health() {
        return fiscalizationClient.health();
    }

    @PostMapping("/fiscalize")
    public PraFiscalizationResult fiscalize(@RequestBody PraInvoiceModel invoice) {
        return fiscalizationClient.fiscalize(invoice);
    }
}
