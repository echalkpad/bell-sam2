package com.vennetics.bell.sam.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.error.adapters.NetapiErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created on 09/02/2016.
 */
@RunWith(MockitoJUnitRunner.class) public class ExceptionHandlingNetapiRestControllerTest {

    // Object under test
    @Autowired @InjectMocks private DummyNetapiRestController controller;

    // Mock Objects
    @Mock private ObjectMapper jsonObjectMapper;

    @Mock private IExceptionTypeMapper exceptionTypeMapper;
    @Mock private NetapiErrorAdapter errorAdapter;

    @Mock private HttpServletRequest mockRequest;
    @Mock private MappedError mockMappedError;
    @Mock private Object mockExceptionResponse;

    @Test public void shouldMapAndAdaptExceptions() throws Exception {

        final Exception caughtException = new Exception();

        when(exceptionTypeMapper.mapException(caughtException, mockRequest)).thenReturn(
                        mockMappedError);
        when(errorAdapter.adapt(mockMappedError)).thenReturn(mockExceptionResponse);
        when(mockMappedError.httpStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        ResponseEntity<?> response = controller.handleAllExceptions(caughtException, mockRequest);
        assertThat(response.getBody(), is(mockExceptionResponse));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }
}

class DummyNetapiRestController extends ExceptionHandlingNetapiRestController {
}
