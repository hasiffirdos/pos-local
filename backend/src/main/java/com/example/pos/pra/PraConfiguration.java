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
        PraProperties props,
        StubPraFiscalizationClient stubClient,
        CloudPraFiscalizationClient cloudClient
    ) {
        String mode = props.getMode();
        
        if ("stub".equalsIgnoreCase(mode)) {
            logger.info("PRA Mode: STUB (testing)");
            return stubClient;
        }
        
        logger.info("PRA Mode: CLOUD (environment: {})", props.getEnvironment());
        return cloudClient;
    }
}
