package com.example.pos.pra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PraConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PraConfiguration.class);

    @Bean
    public PraFiscalizationClient praFiscalizationClient(
        PraProperties properties,
        StubPraFiscalizationClient stubClient,
        ImsPraFiscalizationClient imsClient,
        CloudPraFiscalizationClient cloudClient
    ) {
        String mode = properties.getMode();
        logger.info("Initializing PRA Fiscalization Client with mode: {}", mode);

        if ("cloud".equalsIgnoreCase(mode)) {
            logger.info("Using Cloud PRA Fiscalization Client (environment: {})", 
                properties.getCloud().getEnvironment());
            return cloudClient;
        } else if ("ims".equalsIgnoreCase(mode)) {
            logger.info("Using IMS PRA Fiscalization Client (base URL: {})", 
                properties.getImsBaseUrl());
            return imsClient;
        } else if (properties.getStub().isEnabled()) {
            logger.info("Using Stub PRA Fiscalization Client");
            return stubClient;
        } else {
            logger.info("Defaulting to IMS PRA Fiscalization Client");
            return imsClient;
        }
    }
}
