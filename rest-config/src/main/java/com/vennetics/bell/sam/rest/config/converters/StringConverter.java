package com.vennetics.bell.sam.rest.config.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;

/**
 * Delivers an {@link StringHttpMessageConverter} bean to convert {@link String} responses
 * <p>
 */

@Configuration
public class StringConverter {
    private static final Logger LOG = LoggerFactory.getLogger(StringConverter.class);

    @Bean
    public StringHttpMessageConverter stringMessageConverter() {
        LOG.debug(">>> stringMessageConverter");
        return new StringHttpMessageConverter();
    }
}
