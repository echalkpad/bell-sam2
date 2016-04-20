package com.vennetics.bell.sam.rest.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Extends {@link WebMvcConfigurerAdapter} to configure support for JSON and XML payloads,
 * using schema-generated POJOs with JAXB annotations.
 * <p>
 * Content negotiation is configured to:
 * <ul>
 *     <li>recognise the media type(s) defined by the Accept header</li>
 *     <li>disregard any path extensions in the url</li>
 * </ul>
 * Appropriately configured converters for JSON and XML are appended to the list of
 * {@link HttpMessageConverter}s.
 */

@Configuration
@ComponentScan(basePackages = "com.vennetics.bell.sam.rest.config")
public class RestConfig extends WebMvcConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(RestConfig.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    @Autowired
    private StringHttpMessageConverter stringMessageConverter;

    @Autowired
    private FormHttpMessageConverter formMessageConverter;

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        LOG.debug(">>> configureContentNegotiation", configurer);
        configurer.ignoreAcceptHeader(false)
                .favorPathExtension(false);
    }

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        LOG.debug(">>> configureMessageConverters");
        converters.add(stringMessageConverter);
        converters.add(jsonMessageConverter);
        converters.add(xmlMessageConverter);
        converters.add(formMessageConverter);
        super.configureMessageConverters(converters);
    }

}
