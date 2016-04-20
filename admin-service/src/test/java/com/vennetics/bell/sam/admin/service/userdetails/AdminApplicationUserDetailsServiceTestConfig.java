package com.vennetics.bell.sam.admin.service.userdetails;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan({ "com.vennetics.bell.sam.core.errors", "com.vennetics.bell.sam.error",
        "com.vennetics.bell.sam.model.admin.service.pojo" })
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class AdminApplicationUserDetailsServiceTestConfig {

    /**
     * Loads the test YML file for testing the SDM spring wiring.
     *
     * @return the property placeholder.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-admin-service-controller-test-config.yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    protected AdminApplicationUserDetailsServiceTestConfig() {

    }
}
