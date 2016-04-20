
package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import java.util.Arrays;

import org.apache.cxf.jaxws.EndpointImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.error.adapters.BellSubscriberErrorAdapter;
import com.vennetics.bell.sam.error.adapters.HttpStatusCodeExceptionEntityMapper;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;
import com.vennetics.microservices.common.core.soap.gateway.adapter.IEndpointFactory;
import com.vennetics.microservices.common.core.soap.gateway.adapter.ISoapExceptionMapper;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0._interface.Subscriber;

/**
 * Configures a {@link Subscriber} endpoint.
 */

@Configuration
public class SubscriberThirdPartyConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThirdPartyConfiguration.class);

    protected static final String SUBSCRIBER_V1_ENDPOINT = "/subscriber/v1_0";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Subscriber subscriber;

    @Autowired
    private IEndpointFactory endpointFactory;

    @Autowired
    @Qualifier(BellSubscriberErrorAdapter.SERVICE_NAME)
    private IErrorAdapter errorAdapter;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private IExceptionTypeMapper exceptionTypeMapper;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Bean
    public EndpointImpl subscriberEndpoint() {
        logger.debug(">>> subscriberEndpoint");
        return endpointFactory.exceptionMappingEndpoint(SUBSCRIBER_V1_ENDPOINT,
                                                        subscriber,
                                                        subscriberThirdPartySoapExceptionMapper(),
                                                        true);
    }

    @Bean
    public ISoapExceptionMapper subscriberThirdPartySoapExceptionMapper() {

        logger.debug(">>> subscriberThirdPartySoapExceptionMapper");
        final HttpStatusCodeExceptionEntityMapper exceptionToEntityMapper = new HttpStatusCodeExceptionEntityMapper(jsonObjectMapper,
                                                                                                                    Arrays.asList(MappedError.class));

        return new SubscriberThirdPartySoapExceptionMapper(exceptionTypeMapper,
                                                           errorAdapter,
                                                           exceptionToEntityMapper);
    }

    public RestTemplate subscriberThirdPartyRestTemplate() {
        // Clone the RestTemplate configured by Ribbon and replace the standard
        // message converters with
        // the custom JSON converter.
        logger.debug("Customising RestTemplate {}", restTemplate);
        final RestTemplate customTemplate = new RestTemplate(restTemplate.getRequestFactory());
        customTemplate.getMessageConverters().clear();
        customTemplate.getMessageConverters().add(jsonMessageConverter);
        logger.debug("Customised RestTemplate {}", customTemplate);
        return customTemplate;
    }
}
