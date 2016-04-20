package com.vennetics.bell.sam.rest.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
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

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlingRestControllerTest {

    @Autowired
    @InjectMocks
    private DummyRestController controller;

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

    @Test
    public void shouldMapAndAdaptExceptions() throws Exception {

        final Exception caughtException = new Exception();

        when(exceptionTypeMapper.mapException(caughtException, mockRequest)).thenReturn(mockMappedError);
        when(errorAdapter.adapt(mockMappedError)).thenReturn(mockExceptionResponse);
        when(mockMappedError.httpStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        ResponseEntity<?> response = controller.handleAllExceptions(caughtException, mockRequest);
        assertThat(response.getBody(), is(mockExceptionResponse));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
    }

}
class DummyRestController extends ExceptionHandlingRestController {

    @Autowired
    private IErrorAdapter errorAdapter;

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }
}
