package com.vennetics.microservices.common.core.security.jwt;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration that should be included on the server-side JWT usage for
 * internal service calls e.g. GW to enabler.
 */
@Configuration
@EnableWebMvc
public class JwtControllerMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JwtControllerMvcConfig.class);

    @PostConstruct
    public void init() {

        // Debug line to show this config has been loaded, nothing else
        logger.debug("Initialising Controller Config with JWT handler interecptor");
    }

    @Bean
    public HandlerInterceptor jwtHeaderServerInterceptor() {
        return new JwtHeaderServerInterceptor(new JwtTokenBuilder<>(new ObjectMapper()));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(jwtHeaderServerInterceptor());
    }

}
