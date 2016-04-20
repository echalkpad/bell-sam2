package com.vennetics.bell.sam.rest.config.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;

/**
 * Delivers an {@link org.springframework.http.converter.FormHttpMessageConverter} bean to convert between
 * Multi Value maps and JSON.
 * <p>
 */

@Configuration
public class FormConverter {
    private static final Logger LOG = LoggerFactory.getLogger(FormConverter.class);

    @Bean
    public FormHttpMessageConverter formMessageConverter() {
        LOG.debug(">>> formMessageConverter");
        return new FormHttpMessageConverter();
    }
}
