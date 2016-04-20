package com.vennetics.bell.sam.sms.common.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Provides configuration for the SMS common library.
 */
@Configuration
@ComponentScan(basePackages = { "com.vennetics.bell.sam.sms.common" })
public class SmsCommonConfig {

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    @Primary
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate customTemplate = new RestTemplate();
        customTemplate.getMessageConverters().clear();
        customTemplate.getMessageConverters().add(jsonMessageConverter);
        customTemplate.getMessageConverters().add(xmlMessageConverter);
        return customTemplate;
    }
}
