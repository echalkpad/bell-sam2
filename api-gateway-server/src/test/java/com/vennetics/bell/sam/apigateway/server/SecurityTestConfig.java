package com.vennetics.bell.sam.apigateway.server;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan("com.vennetics.bell.sam.apigateway.server.controller")
public class SecurityTestConfig {

    @Bean
    public RestTemplate gatewayAdapterRestTemplate() {
        final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        return restTemplate;

    }
}
