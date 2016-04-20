package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan({ "com.vennetics.bell.sam.core.errors", "com.vennetics.bell.sam.error" })
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class SubscriberThirdPartySdmErrorTestConfig {

    /**
     * Loads the test YML file for testing the SDM spring wiring.
     *
     * @return the property placeholder.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("subscriber-thirdparty-sdm-test-config.yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    /** Must be public for spring instantiation */
    public SubscriberThirdPartySdmErrorTestConfig() {

    }

}
