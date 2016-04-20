package com.vennetics.microservices.common.core.soap.gateway.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.apache.cxf.interceptor.Fault;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;

@RunWith(MockitoJUnitRunner.class)
public class SoapExceptionMapperTest {

    @InjectMocks
    private SoapExceptionMapper testClass;

    @Mock private IErrorAdapter mockErrorAdapter;
    @Mock private IExceptionTypeMapper mockExceptionTypeMapper;

    @Mock private MappedError mockMappedError;

    @Test
    public void shouldMapExceptionToFault() throws Exception {

        final Throwable exception = new Exception("X");
        final Throwable dummyFaultInfo = new Throwable("dummyFaultInfo");

        when(mockExceptionTypeMapper.mapException(exception)).thenReturn(mockMappedError);
        when(mockErrorAdapter.adapt(mockMappedError)).thenReturn(dummyFaultInfo);
        when(mockMappedError.getStatusCode()).thenReturn(501);

        final Fault result = testClass.mapExceptionToFault(exception);

        assertThat(result.getStatusCode(), is(501));
        assertThat(result.getCause(), sameInstance(dummyFaultInfo));
    }

    @Test
    public void shouldAllowAccessToMapper() throws Exception {

        assertThat(testClass.getExceptionMapper(), sameInstance(mockExceptionTypeMapper));
    }

    @Test
    public void shouldIncludeStatusAndWebFaultInSoapFault() throws Exception {

        final Throwable exception = new Exception("X");
        final Fault result = SoapExceptionMapper.newFault(exception, 100);

        assertThat(result.getStatusCode(), is(100));
        assertThat(result.getCause(), sameInstance(exception));
    }

    @Test
    public void shouldIdentifyHttpClientErrorCause() {

        final HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        final RuntimeException causeIsClientError = new RuntimeException(clientErrorException);
        final RuntimeException causeNotClinetError = new RuntimeException("");

        assertThat(testClass.getCauseIfHttpClientErrorException(clientErrorException),
                   sameInstance(clientErrorException));

        assertThat(testClass.getCauseIfHttpClientErrorException(causeIsClientError),
                   sameInstance(clientErrorException));

        assertNull(testClass.getCauseIfHttpClientErrorException(causeNotClinetError));

    }
}
