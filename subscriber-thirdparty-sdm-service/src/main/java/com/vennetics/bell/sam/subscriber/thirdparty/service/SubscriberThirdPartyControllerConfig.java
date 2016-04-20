package com.vennetics.bell.sam.subscriber.thirdparty.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.vennetics.bell.sam.subscriber.thirdparty.service.autoconfigure.SdmServiceProperties;

@Configuration
@ComponentScan("com.vennetics.bell.sam.subscriber.thirdparty.service")
@SuppressWarnings({ "squid:S2094" })
@EnableWebMvc
@EnableConfigurationProperties(SdmServiceProperties.class)
public class SubscriberThirdPartyControllerConfig {

}
