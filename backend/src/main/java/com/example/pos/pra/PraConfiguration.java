package com.example.pos.pra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PraConfiguration {
    @Bean
    public PraFiscalizationClient praFiscalizationClient(
        PraProperties properties,
        StubPraFiscalizationClient stubClient,
        ImsPraFiscalizationClient imsClient
    ) {
        boolean useIms = "ims".equalsIgnoreCase(properties.getMode()) || !properties.getStub().isEnabled();
        if (useIms) {
            return imsClient;
        }
        return stubClient;
    }
}
