package com.vennetics.microservices.common.core.soap.gateway.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.vennetics.bell.sam.registry.IServiceRegistry;
import com.vennetics.bell.sam.registry.ServiceConstants;

/**
 * Configuration for a CXF Servlet.
 */

@Configuration
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class CxfConfiguration {

    protected static final String SOAP_URL_PREFIX = ServiceConstants.BELL_SUBSCRIBER_URL;

    private static final Logger logger = LoggerFactory.getLogger(CxfConfiguration.class);

    @Autowired
    private IServiceRegistry serviceRegistry;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {

        final String[] urlMappings = generateUrlMappings();
        final ServletRegistrationBean cxfBean = new ServletRegistrationBean(new CXFServlet(),
                                                                            urlMappings);

        logger.debug("Registering new CXF servlet on mappings {}", Arrays.toString(urlMappings));

        return cxfBean;
    }

    private String[] generateUrlMappings() {

        final List<String> urlList = serviceRegistry.servicesForProtocol("SUBSCRIBER-THIRDPARTY-SDM-SERVER")
                .stream()
                .map(CxfConfiguration::buildServiceIdentifierUrl)
                                                    .peek(url -> logger.debug("url:{}", url))
                .collect(Collectors.toList());
        return urlList.toArray(new String[urlList.size()]);
    }

    @SuppressWarnings({"squid:UnusedPrivateMethod"}) // It is used, but sonarqube doesn't recognise it.
    private static String buildServiceIdentifierUrl(final String serviceIdentifier) {
        return String.format("%s/%s/*", SOAP_URL_PREFIX, serviceIdentifier);
    }
}
