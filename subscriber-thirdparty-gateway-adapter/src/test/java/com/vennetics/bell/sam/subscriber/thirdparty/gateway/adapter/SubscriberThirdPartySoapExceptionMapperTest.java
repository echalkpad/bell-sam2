package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.apache.cxf.interceptor.Fault;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.error.adapters.HttpStatusCodeExceptionEntityMapper;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.PolicyException;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.ServiceException;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberThirdPartySoapExceptionMapperTest {

    @InjectMocks
    private SubscriberThirdPartySoapExceptionMapper mapper;

    @Mock
    private IErrorAdapter mockErrorAdapter;
    @Mock
    private IExceptionTypeMapper mockExceptionTypeMapper;

    @Mock
    private HttpStatusCodeExceptionEntityMapper entityMapper;

    @Mock
    private MappedError mockMappedError;

    @Test
    public void shouldDefaultToServiceErrorWhenNotHttpInternalException() {

        final HttpStatus statusCode = HttpStatus.SERVICE_UNAVAILABLE;

        final Throwable exception = new Exception("X");

        expectServiceErrorMapping(statusCode, exception);
    }

    @Test
    public void shouldDefaultToServiceErrorWhenNoPayloadFound() {

        final HttpStatus statusCode = HttpStatus.BAD_REQUEST;

        final Throwable exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        when(entityMapper.captureEntityFromException((HttpClientErrorException) exception)).thenReturn(new ResponseEntity<>(statusCode));

        expectServiceErrorMapping(statusCode, exception);
    }

    @Test
    public void shouldDefaultToServiceErrorWhenNullEntity() {

        final HttpStatus statusCode = HttpStatus.BAD_REQUEST;

        final Throwable exception = new HttpClientErrorException(statusCode);

        when(entityMapper.captureEntityFromException((HttpClientErrorException) exception)).thenReturn(null);

        expectServiceErrorMapping(statusCode, exception);
    }

    @Test
    public void shouldMapPolicyExceptionsToSoapFault() {

        final MappedError error = new MappedError(dummyErrorDescription(), true, "A1", "A2");

        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        when(entityMapper.captureEntityFromException(exception)).thenReturn(new ResponseEntity<MappedError>(error,
                                                                                                            exception.getStatusCode()));

        final Fault result = mapper.mapExceptionToFault(exception);

        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST.value()));

        assertThat(result.getCause(), instanceOf(PolicyException.class));

        final generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.PolicyException cause = ((PolicyException) result.getCause()).getFaultInfo();

        assertThat(cause.getMessageId(), is("code"));
        assertThat(cause.getText(), is("messageTemplate"));
        assertThat(cause.getVariables().size(), is(2));
        assertThat(cause.getVariables().get(0), is("A1"));
        assertThat(cause.getVariables().get(1), is("A2"));
    }

    @Test
    public void shouldMapServiceExceptionsToSoapFault() {

        final MappedError error = new MappedError(dummyErrorDescription(), false, "A1", "A2");

        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        when(entityMapper.captureEntityFromException(exception)).thenReturn(new ResponseEntity<MappedError>(error,
                                                                                                            exception.getStatusCode()));

        final Fault result = mapper.mapExceptionToFault(exception);

        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST.value()));

        assertThat(result.getCause(), instanceOf(ServiceException.class));

        final generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.ServiceException exceptionEntity = ((ServiceException) result.getCause()).getFaultInfo();

        assertThat(exceptionEntity.getMessageId(), is("code"));
        assertThat(exceptionEntity.getText(), is("messageTemplate"));
        assertThat(exceptionEntity.getVariables().size(), is(2));
        assertThat(exceptionEntity.getVariables().get(0), is("A1"));
        assertThat(exceptionEntity.getVariables().get(1), is("A2"));
    }

    private void expectServiceErrorMapping(final HttpStatus statusCode, final Throwable exception) {
        final String[] variables = new String[] { "A", "B" };
        when(mockMappedError.getStatusCode()).thenReturn(statusCode.value());
        when(mockExceptionTypeMapper.mapException(exception)).thenReturn(mockMappedError);
        when(mockMappedError.getCode()).thenReturn("ERROR_CODE");

        // Will be ignored
        when(mockMappedError.getMessageVariables()).thenReturn(variables);

        final Fault result = mapper.mapExceptionToFault(exception);

        assertThat(result.getStatusCode(), is(statusCode.value()));
        assertThat(result.getCause(), instanceOf(ServiceException.class));

        final ServiceException serviceException = (ServiceException) result.getCause();
        final generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.ServiceException exceptionObject = serviceException.getFaultInfo();
        assertThat(exceptionObject.getMessageId(), equalTo("SVC0001"));

        assertThat(exceptionObject.getVariables(), equalTo(Arrays.asList(variables)));
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "messageTemplate");
    }
}
