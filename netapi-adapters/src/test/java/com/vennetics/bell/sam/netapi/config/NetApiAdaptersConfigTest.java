package com.vennetics.bell.sam.netapi.config;

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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class NetApiAdaptersConfigTest {

    @InjectMocks
    @Autowired
    private NetApiAdaptersConfig factory;

    @Mock private RestTemplate mockTemplate;
    @Mock private MappingJackson2HttpMessageConverter mockMessageConverter;

    @Mock private ClientHttpRequestFactory mockRequestFactory;

    @Test
    public void shouldDeliverNetapiRestTemplate() throws Exception {

        when(mockTemplate.getRequestFactory()).thenReturn(mockRequestFactory);

        final RestTemplate template = factory.netapiRestTemplate();

        assertThat(template.getMessageConverters().size(), is(1));
        assertThat(template.getMessageConverters().get(0), is(mockMessageConverter));
        assertThat(template.getRequestFactory(), is(mockRequestFactory));
    }
}
