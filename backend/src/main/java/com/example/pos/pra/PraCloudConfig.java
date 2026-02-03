package com.example.pos.pra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for PRA Cloud API RestTemplate with TLS 1.2+ support
 * This is only activated when pra.mode=cloud
 * 
 * Note: Modern Java (8+) supports TLS 1.2+ by default, so no special SSL configuration needed
 */
@Configuration
@ConditionalOnProperty(name = "pra.mode", havingValue = "cloud")
public class PraCloudConfig {
    private static final Logger logger = LoggerFactory.getLogger(PraCloudConfig.class);

    @Bean(name = "praCloudRestTemplate")
    public RestTemplate praCloudRestTemplate(RestTemplateBuilder builder) {
        logger.info("Configuring RestTemplate for PRA Cloud API");
        
        // Configure request factory with timeouts
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000); // 10 seconds
        requestFactory.setReadTimeout(30000); // 30 seconds
        
        RestTemplate restTemplate = builder
            .requestFactory(() -> requestFactory)
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
            
        logger.info("PRA Cloud RestTemplate configured successfully (TLS 1.2+ enabled by default)");
        return restTemplate;
    }
}
