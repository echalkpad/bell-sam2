package com.vennetics.bell.sam.apigateway.server;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@ComponentScan(basePackages = { "com.vennetics.microservices.common", "com.vennetics.bell.sam" })
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class ApiGatewayServerApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(ApiGatewayServerApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}
