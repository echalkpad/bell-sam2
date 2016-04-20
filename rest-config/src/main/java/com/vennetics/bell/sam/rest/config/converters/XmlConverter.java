package com.vennetics.bell.sam.rest.config.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Delivers an {@link org.springframework.http.converter.HttpMessageConverter}
 * bean to convert between schema-generated POJOs and XML.
 * <p>
 * The converter's mapper extends XmlMapper to ensure JAXB annotations are
 * honoured.
 */

@Configuration
public class XmlConverter {
    private static final Logger LOG = LoggerFactory.getLogger(XmlConverter.class);

    private static final AnnotationIntrospector JAXB_ANNOTATION_INTROSPECTOR = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());

    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlMessageConverter() {
        LOG.debug(">>> xmlMessageConverter");
        return new MappingJackson2XmlHttpMessageConverter(xmlObjectMapper());
    }

    private static ObjectMapper xmlObjectMapper() {
        final XmlMapper mapper = new XmlMapper();
        mapper.setAnnotationIntrospector(JAXB_ANNOTATION_INTROSPECTOR);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        return mapper;
    }
}
