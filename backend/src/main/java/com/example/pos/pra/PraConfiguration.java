package com.example.pos.pra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PraConfiguration {

    @Bean
    public PraFiscalizationClient praFiscalizationClient(CloudPraFiscalizationClient cloudClient) {
        return cloudClient;
    }
}
