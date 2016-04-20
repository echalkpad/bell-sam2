package com.vennetics.bell.sam.discovery;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "squid:S1118"})
public class DiscoveryApplication {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(DiscoveryApplication.class).web(true).run(args);
    }
}
