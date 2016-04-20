package com.vennetics.microservices.common.core.soap.gateway.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;

import com.vennetics.bell.sam.registry.IServiceRegistry;

@RunWith(MockitoJUnitRunner.class)
public class CxfConfigurationTest {

    @InjectMocks
    @Autowired
    private CxfConfiguration configuration;

    @Mock private IServiceRegistry mockRegistry;

    @Test
    public void shouldGetServletRegistrationBean() throws Exception {

        when(mockRegistry.servicesForProtocol("SUBSCRIBER-THIRDPARTY-SDM-SERVER")).thenReturn(Arrays.asList("subid1", "subid2"));
        final ServletRegistrationBean bean = configuration.servletRegistrationBean();
        assertThat(bean, is(not(nullValue())));
        assertThat(bean.getUrlMappings().size(), is(2));
        assertThat(bean.getUrlMappings(), containsInAnyOrder(
                CxfConfiguration.SOAP_URL_PREFIX + "/subid1/*",
                CxfConfiguration.SOAP_URL_PREFIX + "/subid2/*"));
    }
}
