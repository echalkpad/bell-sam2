package com.vennetics.bell.sam.error.adapters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class HttpStatusCodeExceptionEntityMapperTest {

    private static final List<Class<?>> ENTITY_CLASSES = Arrays.asList(DummyExceptionEntity1.class,
                                                                       DummyExceptionEntity2.class);

    @Mock
    private ObjectMapper jsonObjectMapper;

    private HttpStatusCodeExceptionEntityMapper testClass;

    @Before
    public void init() {
        testClass = new HttpStatusCodeExceptionEntityMapper(jsonObjectMapper, ENTITY_CLASSES);
    }

    @Test
    public void shouldReturnCorrectEntityIfHtppStatusCodeException() throws Exception {

        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity1"),
                                        same(DummyExceptionEntity1.class))).thenReturn(new DummyExceptionEntity1());
        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity2"),
                                        same(DummyExceptionEntity2.class))).thenReturn(new DummyExceptionEntity2());

        ResponseEntity<?> response = testClass.captureEntityFromException(wrappedStatusCodeException(jsonBodyWithRoot("DummyExceptionEntity1"),
                                                                                                     HttpStatus.BAD_GATEWAY));
        assertThat(response.getBody(), instanceOf(DummyExceptionEntity1.class));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));

        response = testClass.captureEntityFromException(wrappedStatusCodeException(jsonBodyWithRoot("DummyExceptionEntity2"),
                                                                                   HttpStatus.FORBIDDEN));
        assertThat(response.getBody(), instanceOf(DummyExceptionEntity2.class));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void shouldReturnNullOnParseFailure() throws Exception {

        final Exception caughtException = wrappedStatusCodeException(unparseableJsonBodyWithRoot("DummyExceptionEntity1"),
                                                                     HttpStatus.BAD_GATEWAY);

        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity1"),
                                        same(DummyExceptionEntity1.class))).thenThrow(new IOException());

        assertNull(testClass.captureEntityFromException(caughtException));

    }

    @Test
    public void shouldReturnNullIfBodyNotRecognised() throws Exception {

        final Exception caughtException1 = wrappedStatusCodeException(jsonBodyWithRoot("Unrecognised"),
                                                                      HttpStatus.BAD_GATEWAY);
        final Exception caughtException2 = wrappedStatusCodeException("Unrecognisable Root",
                                                                      HttpStatus.BAD_GATEWAY);

        assertNull(testClass.captureEntityFromException(caughtException1));

        assertNull(testClass.captureEntityFromException(caughtException2));
    }

    @Test
    public void shouldReturnNullIfExceptionIsNotHttpStatusCodeException() throws Exception {

        final Exception caughtException = new IOException();

        assertNull(testClass.captureEntityFromException(caughtException));
    }

    @Test
    public void shouldRecogniseRootElementInPrettyPrintedJson() throws Exception {

        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity1"),
                                        same(DummyExceptionEntity1.class))).thenReturn(new DummyExceptionEntity1());

        final ResponseEntity<?> response = testClass.captureEntityFromException(wrappedStatusCodeException(prettyJsonBodyWithRoot("DummyExceptionEntity1"),
                                                                                                           HttpStatus.BAD_GATEWAY));
        assertThat(response.getBody(), instanceOf(DummyExceptionEntity1.class));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

    private String prettyJsonBodyWithRoot(final String rootName) {
        return sourceWithLines("", "    ", " { ", "", "   \"" + rootName + "\":{}}");
    }

    private String sourceWithLines(final String... lines) {
        return Arrays.stream(lines).collect(Collectors.joining("\n"));
    }

    private String jsonBodyWithRoot(final String rootName) {
        return "{\"" + rootName + "\":{}}";
    }

    private String unparseableJsonBodyWithRoot(final String rootName) {
        return "{\"" + rootName + "\": unterminated garbage";
    }

    private Exception wrappedStatusCodeException(final String body, final HttpStatus statusCode) {
        final HttpStatusCodeException statusCodeException = statusCodeException(body, statusCode);
        return new Exception(statusCodeException);
    }

    private HttpStatusCodeException statusCodeException(final String body,
                                                        final HttpStatus statusCode) {
        return new HttpStatusCodeException(statusCode, "", body.getBytes(), null) {
        };
    }

}

class DummyExceptionEntity1 {
}

class DummyExceptionEntity2 {
}
