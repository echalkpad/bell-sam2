package com.vennetics.bell.sam.error;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Sam errors
 */
@Configuration
@ComponentScan(basePackages = "com.vennetics.bell.sam.error")
@SuppressWarnings({ "squid:S2094" }) // Suppress error about this being an empty class
public class SamErrorsConfig {
}
