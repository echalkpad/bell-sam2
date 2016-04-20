package com.vennetics.bell.sam.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableHystrixDashboard
@EnableEurekaClient
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "squid:S1118"})
public class HystrixDashboardApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(HystrixDashboardApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}
