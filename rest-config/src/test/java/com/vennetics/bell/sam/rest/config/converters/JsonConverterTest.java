package com.vennetics.bell.sam.rest.config.converters;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonConverterTest {

    private MappingJackson2HttpMessageConverter converter = new JsonConverter().jsonMessageConverter();

    @Test
    public void shouldDeliverJsonFromAnnotatedEntity() throws Exception {

        final ObjectMapper mapper = converter.getObjectMapper();

        final TestEntity entity = new TestEntity("F1", "F2", "F3");

        final String result = mapper.writeValueAsString(entity);
        assertThat(JsonPath.read(result, "$.TestEntity"), is(not(nullValue())));
        assertThat(JsonPath.read(result, "$.TestEntity.field1"), is("F1"));
        assertThat(JsonPath.read(result, "$.TestEntity.field2"), is("F2"));
        assertThat(JsonPath.read(result, "$.TestEntity.field99"), is("F3"));
        assertThat(result, not(containsString("emptyEntity")));
    }
    @Test
    public void shouldDeliverEntityFromJson() throws Exception {

        final ObjectMapper mapper = converter.getObjectMapper();

        final TestEntity result = mapper.readValue(jsonBody(), TestEntity.class);
        assertThat(result.getField1(), is("value1"));
        assertThat(result.getField2(), is("value2"));
        assertThat(result.getField3(), is("value3"));
    }

    private String jsonBody() {
        return sourceWithLines(
                "{\"TestEntity\": {",
                "  \"field1\": \"value1\",",
                "  \"field2\": \"value2\",",
                "  \"field99\": \"value3\"",
                "}}"
        );
    }

    private String sourceWithLines(final String ... lines) {
        return Arrays.stream(lines).collect(Collectors.joining("\n"));
    }
}
