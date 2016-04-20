package com.vennetics.bell.sam.cassandra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableEurekaClient
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "squid:S1118"})
@ComponentScan(basePackages = {"com.vennetics.bell.sam", "com.vennetics.bell.sam.core.cassandra"})
public class CassandraApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(CassandraApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}

