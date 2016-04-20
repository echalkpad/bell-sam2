package com.vennetics.bell.sam.admin.service.controllers;

import com.vennetics.bell.sam.admin.service.api.IApplicationAdminService;
import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.rest.config.RestConfig;
import com.vennetics.microservices.common.api.model.admin.v1.application.IApplicationDetailsService;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@EnableWebMvc
//  AdminServiceConfig is not imported as it loads Cassandra
@Import({ RestConfig.class, SamErrorsConfig.class, CoreErrorsConfig.class})
@ComponentScan({"com.vennetics.bell.sam.admin.service.controllers" })
public class ApplicationAdminServiceControllerTestConfig {

    @Bean
    public IApplicationAdminService mockApplicationAdminService() {
        return mock(IApplicationAdminService.class);
    }

    @Bean
    public IApplicationDetailsService mockApplicationDetailsService() {
        return mock(IApplicationDetailsService.class);
    }

    /**
     * Loads the test YML file for testing the spring wiring.
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
}
