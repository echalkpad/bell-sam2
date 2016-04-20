package com.vennetics.bell.sam.smsx.enabler;

import com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure.SmppConfig;
import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.rest.config.RestConfig;
import com.vennetics.bell.sam.sms.common.configuration.SmsCommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PreDestroy;

/**
 * Spring Application for Sms-X Enabler
 *
 * @author markcorkery
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@ComponentScan(basePackages = { "com.vennetics.bell.sam.smsx.enabler" })
@EnableJpaRepositories("com.vennetics.bell.sam.*")
@EntityScan("com.vennetics.bell.sam.*")
@Import({ SmppConfig.class, SmsCommonConfig.class, SamErrorsConfig.class,
                CoreErrorsConfig.class, RestConfig.class })
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class SmsxEnablerApplication {

    private static ConfigurableApplicationContext ctx;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(SmsxEnablerApplication.class, args);
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }
}
