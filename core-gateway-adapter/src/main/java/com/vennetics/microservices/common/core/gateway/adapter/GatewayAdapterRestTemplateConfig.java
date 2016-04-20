package com.vennetics.microservices.common.core.gateway.adapter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.microservices.common.core.security.jwt.JwtHeaderClientRequestInterceptor;
import com.vennetics.microservices.common.core.security.jwt.JwtTokenBuilder;

@Configuration
public class GatewayAdapterRestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayAdapterRestTemplateConfig.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void customiseTemplate() {

        // Clone the RestTemplate configured by Ribbon and replace the standard
        // message converters with
        // the custom JSON converter.
        logger.debug("Customising RestTemplate {} for gateway adapter", restTemplate);

        // Make this one first
        restTemplate.getMessageConverters().add(0, jsonMessageConverter);

        // To add JWT to request headers
        restTemplate.getInterceptors().add(jwtHeaderRequestInterceptor());

        logger.debug("Customised RestTemplate {} for gateway adapter", restTemplate);
    }

    @Bean
    public ClientHttpRequestInterceptor jwtHeaderRequestInterceptor() {
        return new JwtHeaderClientRequestInterceptor(new JwtTokenBuilder<>(new ObjectMapper()));
    }
}
