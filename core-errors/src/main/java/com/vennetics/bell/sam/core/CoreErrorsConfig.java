package com.vennetics.bell.sam.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Core Errors.
 */
@Configuration
@ComponentScan(basePackages = "com.vennetics.bell.sam.core")
@SuppressWarnings({ "squid:S2094" }) // Suppress error about this being an empty class
public class CoreErrorsConfig {
}
