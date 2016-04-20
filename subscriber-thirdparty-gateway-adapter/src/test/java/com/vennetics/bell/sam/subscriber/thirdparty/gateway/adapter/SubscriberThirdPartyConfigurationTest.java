package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.vennetics.microservices.common.core.soap.gateway.adapter.IEndpointFactory;
import com.vennetics.microservices.common.core.soap.gateway.adapter.ISoapExceptionMapper;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0._interface.Subscriber;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberThirdPartyConfigurationTest {

    @Mock private Subscriber mockSubscriber;
    @Mock private IEndpointFactory mockEndpointFactory;

    @Mock private EndpointImpl mockEndpoint;

    // @Mock
    // private ISoapExceptionMapper soapExceptionMapper;

    @Autowired
    @InjectMocks
    private SubscriberThirdPartyConfiguration configuration;

    @Test
    public void shouldDeliverEndpoint() throws Exception {

        when(mockEndpointFactory.exceptionMappingEndpoint(Matchers.eq(SubscriberThirdPartyConfiguration.SUBSCRIBER_V1_ENDPOINT),
                                                          Matchers.eq(mockSubscriber),
                                                          Matchers.isA(ISoapExceptionMapper.class),
                                                          Matchers.eq(true)))
                .thenReturn(mockEndpoint);
        final EndpointImpl endpoint = configuration.subscriberEndpoint();

        assertThat(endpoint, sameInstance(mockEndpoint));
    }
}
