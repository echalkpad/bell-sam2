package com.vennetics.bell.sam.rest.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;

@RunWith(MockitoJUnitRunner.class)
public class PassThruExceptionHandlingRestControllerTest {

    @Autowired
    @InjectMocks
    private DummyGatewayRestController controller;

    @Mock
    private ObjectMapper jsonObjectMapper;

    @Mock
    private IExceptionTypeMapper exceptionTypeMapper;
    @Mock
    private IErrorAdapter errorAdapter;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private MappedError mockMappedError;
    @Mock
    private Object mockExceptionResponse;

    @Before
    public void init() {
        controller.init();
    }

    @Test
    public void shouldReturnCorrectEntityIfHtppStatusCodeException() throws Exception {

        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity1"), same(DummyExceptionEntity1.class))).thenReturn(new DummyExceptionEntity1());
        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity2"), same(DummyExceptionEntity2.class))).thenReturn(new DummyExceptionEntity2());

        ResponseEntity<?> response = controller.handleAllExceptions(
                wrappedStatusCodeException(jsonBodyWithRoot("DummyExceptionEntity1"), HttpStatus.BAD_GATEWAY), mockRequest);
        assertThat(response.getBody(), instanceOf(DummyExceptionEntity1.class));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));

        response = controller.handleAllExceptions(wrappedStatusCodeException(jsonBodyWithRoot("DummyExceptionEntity2"), HttpStatus.FORBIDDEN), mockRequest);
        assertThat(response.getBody(), instanceOf(DummyExceptionEntity2.class));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void shouldReturnInternalExceptionIfHtppStatusCodeExceptionBodyCannotBeParsed() throws Exception {

        final Exception caughtException = wrappedStatusCodeException(
                unparseableJsonBodyWithRoot("DummyExceptionEntity1"), HttpStatus.BAD_GATEWAY);

        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity1"), same(DummyExceptionEntity1.class))).thenThrow(new IOException());
        when(exceptionTypeMapper.mapException(caughtException, mockRequest)).thenReturn(mockMappedError);
        when(errorAdapter.adapt(mockMappedError)).thenReturn(mockExceptionResponse);
        when(mockMappedError.httpStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        final ResponseEntity<?> response = controller.handleAllExceptions(caughtException, mockRequest);
        assertThat(response.getBody(), is(mockExceptionResponse));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

    @Test
    public void shouldReturnDefaultEntityIfHtppStatusCodeExceptionBodyNotRecognised() throws Exception {

        final Exception caughtException1 = wrappedStatusCodeException(jsonBodyWithRoot("Unrecognised"), HttpStatus.BAD_GATEWAY);
        final Exception caughtException2 = wrappedStatusCodeException("Unrecognisable Root", HttpStatus.BAD_GATEWAY);

        when(exceptionTypeMapper.mapException(caughtException1, mockRequest)).thenReturn(mockMappedError);
        when(exceptionTypeMapper.mapException(caughtException2, mockRequest)).thenReturn(mockMappedError);
        when(errorAdapter.adapt(mockMappedError)).thenReturn(mockExceptionResponse);
        when(mockMappedError.httpStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        ResponseEntity<?> response = controller.handleAllExceptions(caughtException1, mockRequest);
        assertThat(response.getBody(), is(mockExceptionResponse));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));

        response = controller.handleAllExceptions(caughtException2, mockRequest);
        assertThat(response.getBody(), is(mockExceptionResponse));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

    @Test
    public void shouldReturnInternalExceptionIfExceptionIsNotHttpStatusCodeException() throws Exception {

        final Exception caughtException = new IOException();

        when(exceptionTypeMapper.mapException(caughtException, mockRequest)).thenReturn(mockMappedError);
        when(errorAdapter.adapt(mockMappedError)).thenReturn(mockExceptionResponse);
        when(mockMappedError.httpStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        final ResponseEntity<?> response = controller.handleAllExceptions(caughtException, mockRequest);
        assertThat(response.getBody(), is(mockExceptionResponse));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

    @Test
    public void shouldRecogniseRootElementInPrettyPrintedJson() throws Exception {

        when(jsonObjectMapper.readValue(contains("DummyExceptionEntity1"), same(DummyExceptionEntity1.class))).thenReturn(new DummyExceptionEntity1());

        final ResponseEntity<?> response = controller.handleAllExceptions(
                wrappedStatusCodeException(prettyJsonBodyWithRoot("DummyExceptionEntity1"), HttpStatus.BAD_GATEWAY), mockRequest);
        assertThat(response.getBody(), instanceOf(DummyExceptionEntity1.class));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

    private String prettyJsonBodyWithRoot(final String rootName) {
        return sourceWithLines(
                "",
                "    ",
                " { ",
                "",
                "   \"" + rootName + "\":{}}"
        );
    }

    private String sourceWithLines(final String ... lines) {
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

    private HttpStatusCodeException statusCodeException(final String body, final HttpStatus statusCode) {
        return new HttpStatusCodeException(statusCode, "", body.getBytes(), null) {
        };
    }
}

class DummyGatewayRestController extends PassThruExceptionHandlingRestController {

    @Autowired
    private IErrorAdapter errorAdapter;

    @Override
    protected List<Class<?>> exceptionClasses() {
        return Arrays.asList(DummyExceptionEntity1.class, DummyExceptionEntity2.class);
    }

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }
}

class DummyExceptionEntity1 {
}

class DummyExceptionEntity2 {
}




