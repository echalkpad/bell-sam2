package com.vennetics.bell.sam.rest.config.converters;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class XmlConverterTest {

    private final MappingJackson2XmlHttpMessageConverter converter = new XmlConverter().xmlMessageConverter();

    @Test
    public void shouldDeliverXmlFromAnnotatedEntity() throws Exception {

        final ObjectMapper mapper = converter.getObjectMapper();

        final TestEntity entity = new TestEntity("F1", "F2", "F3");

        final String result = mapper.writeValueAsString(entity);
        assertThat(result, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(result, containsString("<TestEntity"));
        assertThat(result, containsString("<field1>F1</field1>"));
        assertThat(result, containsString("<field2>F2</field2>"));
        assertThat(result, containsString("<field99>F3</field99>"));
        assertThat(result, not(containsString("<emptyEntity")));
        assertThat(result, endsWith("</TestEntity>"));
    }

    @Test
    public void shouldDeliverEntityFromXml() throws Exception {

        final ObjectMapper mapper = converter.getObjectMapper();

        final TestEntity result = mapper.readValue(xmlBody(), TestEntity.class);
        assertThat(result.getField1(), is("Value1"));
        assertThat(result.getField2(), is("Value2"));
        assertThat(result.getField3(), is("Value3"));
    }

    private String xmlBody() {
        return sourceWithLines(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<TestEntity>",
                "    <field1>Value1</field1>",
                "    <field2>Value2</field2>",
                "    <field99>Value3</field99>",
                "</TestEntity>"
        );
    }

    private String sourceWithLines(final String ... lines) {
        return Arrays.stream(lines).collect(Collectors.joining("\n"));
    }
}
