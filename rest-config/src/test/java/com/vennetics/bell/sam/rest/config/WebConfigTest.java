package com.vennetics.bell.sam.rest.config;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WebConfigTest {

    @Autowired
    @InjectMocks
    private RestConfig webConfig;

    @Mock private MappingJackson2HttpMessageConverter mockJsonMessageConverter;
    @Mock private MappingJackson2XmlHttpMessageConverter mockXmlMessageConverter;
    @Mock private StringHttpMessageConverter mockStringMessageConverter;
    @Mock private FormHttpMessageConverter mockFormMessageConverter;
    @Mock private ContentNegotiationConfigurer mockNegotiationConfigurer;

    @Test
    public void shouldConfigureContentNegotiation() throws Exception {

        when(mockNegotiationConfigurer.ignoreAcceptHeader(false)).thenReturn(mockNegotiationConfigurer);
        when(mockNegotiationConfigurer.favorPathExtension(false)).thenReturn(mockNegotiationConfigurer);

        webConfig.configureContentNegotiation(mockNegotiationConfigurer);

        verify(mockNegotiationConfigurer).ignoreAcceptHeader(false);
        verify(mockNegotiationConfigurer).favorPathExtension(false);
    }

    @Test
    public void shouldExtendMessageConverters() throws Exception {

        final List<HttpMessageConverter<?>> converterList = new ArrayList<>();

        webConfig.configureMessageConverters(converterList);

        assertThat(converterList, hasSize(4));
        assertThat(converterList, containsInAnyOrder(mockJsonMessageConverter, mockXmlMessageConverter, mockStringMessageConverter, mockFormMessageConverter));
    }
}
