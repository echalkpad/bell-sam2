package com.vennetics.bell.sam.rest.config.converters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Delivers an {@link org.springframework.http.converter.HttpMessageConverter} bean to convert between
 * schema-generated POJOs and JSON.
 * <p>
 * The converter is configured to expect a 'root' value and to honour JAXB annotations.
 */

@Configuration
public class JsonConverter {
    private static final Logger LOG = LoggerFactory.getLogger(JsonConverter.class);

    private static final AnnotationIntrospector JAXB_ANNOTATION_INTROSPECTOR =
                                                        new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());

    @Bean
    public MappingJackson2HttpMessageConverter jsonMessageConverter() {
        LOG.debug(">>> jsonMessageConverter");
        return new MappingJackson2HttpMessageConverter(jsonObjectMapper());
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setAnnotationIntrospector(JAXB_ANNOTATION_INTROSPECTOR);
        return mapper;
    }
}
