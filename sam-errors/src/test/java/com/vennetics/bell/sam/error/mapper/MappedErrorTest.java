package com.vennetics.bell.sam.error.mapper;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;

public class MappedErrorTest {

    private static final AnnotationIntrospector JAXB_ANNOTATION_INTROSPECTOR = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());

    @Test
    public void shouldConstructFromErrorDescription() {

        final MappedError result = new MappedError(dummyErrorDescription(), false, "A1", "A2")
                .withPath("/a")
                .withTimestamp(50L);

        assertThat(result.getCode(), is("code"));
        assertThat(result.getErrorCode(), is("type"));
        assertThat(result.getMessageTemplate(), is("messageTemplate"));
        assertThat(result.getMessageVariables().length, is(2));
        assertThat(result.getMessageVariables()[0], is("A1"));
        assertThat(result.getStatusCode(), is(400));
        assertThat(result.httpStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getPath(), is("/a"));
        assertThat(result.getTimestamp(), is(50L));
    }

    @Test
    public void shouldIndicateWhetherMappedErrorIsAPolicyError() {

        assertThat(new MappedError(dummyErrorDescription()).isPolicyError(), is(false));
        assertThat(new MappedError(dummyErrorDescription(), false).isPolicyError(), is(false));
        assertThat(new MappedError(dummyErrorDescription(), true).isPolicyError(), is(true));
    }

    @Test
    public void shouldOverrideToString() {

        assertThat(new MappedError(dummyErrorDescription(), false, "A1", "A2")
                .withPath("/abc")
                .withTimestamp(17L).toString(),
                is("MappedError [statusCode=400, errorCode=type, messageTemplate=messageTemplate, code=code, policy=false, path=/abc, timestamp=17, messageVariables=[A1, A2]]"));
    }

    @SuppressWarnings("unused")
    @Test
    public void shouldHashOnAllFields() {

        final int hash1 = new MappedError(new ErrorDescription("C", "T", 400, ""), false).hashCode();
        final int hash2 = new MappedError(new ErrorDescription("C", "T", 400, ""), false).hashCode();
        final int hash3 = new MappedError(new ErrorDescription("X", "T", 400, ""), false).hashCode();
        final int hash4 = new MappedError(new ErrorDescription("C", "X", 400, ""), false).hashCode();
        final int hash5 = new MappedError(new ErrorDescription("C", "T", 500, ""), false).hashCode();
        final int hash7 = new MappedError(new ErrorDescription("C", "T", 400, ""), false).withPath("/A").hashCode();
        final int hash8 = new MappedError(new ErrorDescription("C", "T", 400, ""), false).withTimestamp(1L).hashCode();
        final int hash9 = new MappedError(new ErrorDescription("C", "T", 400, ""), true).hashCode();
        final int hash10 = new MappedError(new ErrorDescription(null, "T", 400, ""), false).hashCode();
        final int hash11 = new MappedError(new ErrorDescription("C", null, 400, ""), false).hashCode();
        final int hash13 = new MappedError(new ErrorDescription("C", "T", 400, ""), false, "A1").hashCode();

        assertThat(hash1, equalTo(hash2));
        assertThat(hash1, not(equalTo(hash3)));
        assertThat(hash1, not(equalTo(hash4)));
        assertThat(hash1, not(equalTo(hash5)));
        assertThat(hash1, not(equalTo(hash9)));
        assertThat(hash1, not(equalTo(hash10)));
        assertThat(hash1, not(equalTo(hash11)));
        assertThat(hash1, not(equalTo(hash13)));
    }

    @Test
    public void shouldUseAllFieldsForEqualityCheck() {

        final MappedError error1 = new MappedError(new ErrorDescription("C", "T", 400, ""), false);
        final MappedError error2 = new MappedError(new ErrorDescription("C", "T", 400, ""), false);
        final MappedError error3 = new MappedError(new ErrorDescription("X", "T", 400, ""), false);
        final MappedError error4 = new MappedError(new ErrorDescription("C", "X", 400, ""), false);
        final MappedError error5 = new MappedError(new ErrorDescription("C", "T", 500, ""), false);
        final MappedError error6 = new MappedError(new ErrorDescription("C", "T", 500, "M"), false);
        final MappedError error7 = new MappedError(new ErrorDescription("C", "T", 400, ""), true);
        final MappedError error8 = new MappedError(new ErrorDescription(null, "T", 400, ""), false);
        final MappedError error9 = new MappedError(new ErrorDescription("C", null, 400, ""), false);
        final MappedError error10 = new MappedError(new ErrorDescription("C", "T", 500, ""), false);
        final MappedError error11 = new MappedError(new ErrorDescription("C", "T", 400, ""), false).withPath("/A");
        final MappedError error12 = new MappedError(new ErrorDescription("C", "T", 400, ""), false).withTimestamp(5L);
        final MappedError error13 = new MappedError(new ErrorDescription("C", "T", 400, ""), false, "A1");
        final MappedError error14 = new MappedError(new ErrorDescription("C", "T", 400, null), false);

        assertThat(error1, equalTo(error1));
        assertThat(error1, equalTo(error2));
        assertThat(error1, not(equalTo(error3)));
        assertThat(error1, not(equalTo(error4)));
        assertThat(error1, not(equalTo(error5)));
        assertThat(error1, not(equalTo(error6)));
        assertThat(error1, not(equalTo(error7)));
        assertThat(error1, not(equalTo(error8)));
        assertThat(error8, not(equalTo(error1)));
        assertThat(error1, not(equalTo(error9)));
        assertThat(error9, not(equalTo(error1)));
        assertThat(error1, not(equalTo(error10)));
        assertThat(error10, not(equalTo(error1)));
        assertThat(error10, not(equalTo(null)));
        assertThat(error10, not(equalTo("NotAnError")));
        assertThat(error1, not(equalTo(error11)));
        assertThat(error11, not(equalTo(error1)));
        assertThat(error1, not(equalTo(error12)));
        assertThat(error1, not(equalTo(error13)));
        assertThat(error1, not(equalTo(error14)));
        assertThat(error14, not(equalTo(error1)));
    }

    @Test
    public void shouldMarshallViaJackson() throws Exception {

        final MappedError entity = new MappedError("code",
                                                           "messageTemplate",
                                                           "code",
                                                           500,
                                                           true,
                                                           new String[] { "variable1" },
                                                           "path",
                                                           0L);

        final String result = jsonObjectMapper().writeValueAsString(entity);

        final MappedError mappedError = jsonObjectMapper().readValue(result, MappedError.class);

        assertEquals(entity, mappedError);
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "messageTemplate");
    }

    private ObjectMapper jsonObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setAnnotationIntrospector(JAXB_ANNOTATION_INTROSPECTOR);
        return mapper;
    }
}
