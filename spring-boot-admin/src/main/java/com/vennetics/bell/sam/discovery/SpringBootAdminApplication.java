package com.vennetics.bell.sam.discovery;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.codecentric.boot.admin.config.EnableAdminServer;
import de.codecentric.boot.admin.discovery.ApplicationDiscoveryListener;
import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.ApplicationRegistry;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableEurekaClient
@EnableAdminServer
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118", "squid:S1604" })
public class SpringBootAdminApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootAdminApplication.class);
    private static ConfigurableApplicationContext ctx;

    @Value("${applicationDiscovery.contextPathApplications:hystrix-dashboard}")
    private String contextPathApplications;

    public static void main(final String[] args) {
        ctx = SpringApplication.run(SpringBootAdminApplication.class, args);
    }

    @Bean
    @Autowired
    @ConfigurationProperties(prefix = "applicationDiscovery")
    ApplicationDiscoveryListener customDiscoveryListener(final DiscoveryClient discoveryClient,
                                                         final ApplicationRegistry registry) {

        final Set<String> applicationNamesToIntercept = convertContextPathApplicationListToSet(contextPathApplications);

        logger.debug(">>> customDiscoveryListener");
        return new DiscoveryListener(discoveryClient, registry, applicationNamesToIntercept);
    }

    private Set<String> convertContextPathApplicationListToSet(final String commaSeparatedApplicationList) {
        logger.debug(">>> convertContextPathApplicationList {}", commaSeparatedApplicationList);
        try (Stream<String> stringStream = Arrays.stream(commaSeparatedApplicationList.split(","))) {
            return stringStream.collect(Collectors.toCollection(TreeSet::new));
        }

    }

    protected void setContextPathApplications(final String contextPathApplications) {
        this.contextPathApplications = contextPathApplications;
    }

    @PreDestroy
    public void clearContext() {
        ctx.close();
    }

    private static final class DiscoveryListener extends ApplicationDiscoveryListener {
        private final Set<String> applicationNamesToIntercept;

        private DiscoveryListener(final DiscoveryClient discoveryClient,
                                  final ApplicationRegistry registry,
                                  final Set<String> applicationNamesToIntercept) {
            super(discoveryClient, registry);
            this.applicationNamesToIntercept = applicationNamesToIntercept;
        }

        @Override
        protected Application convert(final ServiceInstance instance) {
            logger.debug(">>> convert {}", instance.getServiceId());

            final String normalisedServiceId = instance.getServiceId().toLowerCase();
            if (applicationNamesToIntercept.contains(normalisedServiceId)) {
                final String serviceUrl = append(instance.getUri().toString(),
                                                 "/" + normalisedServiceId);
                final String managementUrl = append(instance.getUri().toString(),
                                                    "/" + normalisedServiceId);
                final String healthUrl = append(managementUrl, "health");

                return Application.create(instance.getServiceId())
                                  .withHealthUrl(healthUrl)
                                  .withManagementUrl(managementUrl)
                                  .withServiceUrl(serviceUrl)
                                  .build();
            }
            return super.convert(instance);
        }
    }
}
