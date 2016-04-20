package com.vennetics.bell.sam.subscriber.thirdparty.server;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import com.vennetics.bell.sam.adapters.sdm.ldap.SdmLdapConfig;
import com.vennetics.bell.sam.adapters.sdm.stub.SdmStubConfig;
import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.rest.config.RestConfig;
import com.vennetics.bell.sam.subscriber.thirdparty.service.SubscriberThirdPartyControllerConfig;
import com.vennetics.microservices.common.core.security.jwt.JwtControllerMvcConfig;

/**
 * Spring Application for SubX
 *
 * @author aaronwatters
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
@Import({ SdmLdapConfig.class, SdmStubConfig.class, SubscriberThirdPartyControllerConfig.class,
        SamErrorsConfig.class, RestConfig.class, CoreErrorsConfig.class,
        JwtControllerMvcConfig.class })
public class SubscriberThirdPartyApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(SubscriberThirdPartyApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }

}
