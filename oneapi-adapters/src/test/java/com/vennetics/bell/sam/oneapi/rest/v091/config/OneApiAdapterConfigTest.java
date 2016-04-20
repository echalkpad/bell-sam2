package com.vennetics.bell.sam.oneapi.rest.v091.config;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class OneApiAdapterConfigTest {

    @InjectMocks
    @Autowired
    private OneApiAdaptersConfig factory;

    @Mock private RestTemplate mockTemplate;
    @Mock private MappingJackson2HttpMessageConverter mockJacksonConverter;
    @Mock private FormHttpMessageConverter mockFormConverter;

    @Mock private ClientHttpRequestFactory mockRequestFactory;

    @Test
    public void shouldDeliverOneapiRestTemplate() throws Exception {

        when(mockTemplate.getRequestFactory()).thenReturn(mockRequestFactory);

        final RestTemplate template = factory.oneapiRestTemplate();

        assertThat(template.getMessageConverters().size(), is(2));
        assertThat(template.getMessageConverters().get(0), is(mockJacksonConverter));
        assertThat(template.getMessageConverters().get(1), is(mockFormConverter));
        assertThat(template.getRequestFactory(), is(mockRequestFactory));
    }
}
