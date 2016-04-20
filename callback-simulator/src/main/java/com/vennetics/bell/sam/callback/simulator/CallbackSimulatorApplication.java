package com.vennetics.bell.sam.callback.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableEurekaClient
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class CallbackSimulatorApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(CallbackSimulatorApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}
