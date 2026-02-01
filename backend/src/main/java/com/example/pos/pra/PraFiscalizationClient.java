package com.example.pos.pra;

import com.example.pos.pra.dto.PraFiscalizationResult;
import com.example.pos.pra.dto.PraHealth;
import com.example.pos.pra.dto.PraInvoiceModel;

public interface PraFiscalizationClient {
    PraFiscalizationResult fiscalize(PraInvoiceModel invoice);
    PraHealth health();
}
