package com.vennetics.bell.sam.adapters.sdm.ldap;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Test configuration
 */
@Configuration
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class SdmTestConfig {

    /**
     * Loads the test YML file for testing the SDM spring wiring.
     *
     * @return the property placeholder.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("sdm-adapter-test-config.yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    /** Must be public for spring instantiation */
    public SdmTestConfig() {

    }
}
