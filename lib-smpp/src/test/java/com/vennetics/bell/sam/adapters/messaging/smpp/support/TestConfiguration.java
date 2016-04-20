package com.vennetics.bell.sam.adapters.messaging.smpp.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Dummy mainline to define configuration for web integration testing. Replace
 * SmsService bean with a mock. TODO SJ Determine if there is a better way of
 * doing this...
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.vennetics.bell.sam")
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class TestConfiguration {

    public static void main(final String[] args) {
        SpringApplication.run(TestConfiguration.class, args);
    }
}
