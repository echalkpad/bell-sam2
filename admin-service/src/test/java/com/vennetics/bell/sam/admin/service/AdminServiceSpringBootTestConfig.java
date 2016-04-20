package com.vennetics.bell.sam.admin.service;

import com.vennetics.microservices.common.api.model.admin.register.IServiceRegistration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

/**
 * This class injects the spring boot application into the SpringApplicationConfiguration
 * for CrudRepository integration testing.
 */
@SpringBootApplication
@Import(AdminServiceConfig.class)
public class AdminServiceSpringBootTestConfig {

    @Bean
    public IServiceRegistration mockIServiceRegistration() {
        return mock(IServiceRegistration.class);
    }


}
