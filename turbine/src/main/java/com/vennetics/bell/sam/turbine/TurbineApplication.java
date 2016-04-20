package com.vennetics.bell.sam.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableTurbine
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "squid:S1118"})
public class TurbineApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(TurbineApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}
