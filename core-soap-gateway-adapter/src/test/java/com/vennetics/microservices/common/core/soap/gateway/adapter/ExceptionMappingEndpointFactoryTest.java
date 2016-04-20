package com.vennetics.microservices.common.core.soap.gateway.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionMappingEndpointFactoryTest {

    @InjectMocks
    @Autowired
    private ExceptionMappingEndpointFactory factory;

    @Mock private IExceptionTypeMapper mockExceptionTypeMapper;

    @Mock
    private ISoapExceptionMapper soapExceptionMapper;
    private final Object dummyImplementation = new Object();

    private EndpointImpl endpoint;

    @After
    public void teardown() throws Exception {
        if (endpoint != null) {
            endpoint.close();
        }
    }

    @Test
    public void shouldDeliverExceptionMappingEndpoint() throws Exception {

        endpoint = factory.exceptionMappingEndpoint("ABC",
                                                    dummyImplementation,
                                                    soapExceptionMapper,
                                                    false);

        assertThat(endpoint, is(not(nullValue())));
        assertThat(endpoint.getAddress(), is("ABC"));
        assertThat(endpoint.getServerFactory(), instanceOf(ExceptionMappingEndpointFactory.ExceptionMappingServerFactoryBean.class));
        assertThat(endpoint.getInInterceptors(), hasSize(0));
        assertThat(endpoint.getInFaultInterceptors(), hasSize(0));
        assertThat(endpoint.getOutInterceptors(), hasSize(0));
        assertThat(endpoint.getOutFaultInterceptors(), hasSize(0));
    }

    @Test
    public void shouldDeliverEndpointWithLoggingInterceptors() throws Exception {

        endpoint = factory.exceptionMappingEndpoint("ABC",
                                                    dummyImplementation,
                                                    soapExceptionMapper,
                                                    true);

        assertThat(endpoint, is(not(nullValue())));
        assertThat(endpoint.getInInterceptors().get(0), instanceOf(LoggingInInterceptor.class));
        assertThat(endpoint.getInFaultInterceptors().get(0), instanceOf(LoggingInInterceptor.class));
        assertThat(endpoint.getOutInterceptors().get(0), instanceOf(LoggingOutInterceptor.class));
        assertThat(endpoint.getOutFaultInterceptors().get(0), instanceOf(LoggingOutInterceptor.class));
    }
}
