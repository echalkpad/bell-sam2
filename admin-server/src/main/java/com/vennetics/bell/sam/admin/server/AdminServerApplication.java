package com.vennetics.bell.sam.admin.server;

import com.vennetics.bell.sam.admin.service.AdminServiceConfig;
import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.rest.config.RestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import javax.annotation.PreDestroy;

/**
 * The Admin Server Application
 */
@SpringBootApplication
@EnableEurekaClient
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "squid:S1118"})
@Import({ AdminServiceConfig.class, RestConfig.class, SamErrorsConfig.class, CoreErrorsConfig.class})
public class AdminServerApplication {

    private static ConfigurableApplicationContext ctx;
    public static void main(final String[] args) {
        ctx = SpringApplication.run(AdminServerApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}

