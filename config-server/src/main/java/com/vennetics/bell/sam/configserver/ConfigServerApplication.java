package com.vennetics.bell.sam.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "squid:S1118"})
public class ConfigServerApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(ConfigServerApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}
