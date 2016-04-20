package com.vennetics.bell.sam.error.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.vennetics.bell.sam.core.errors.ErrorDictionary;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.core.exception.PolicyException;
import com.vennetics.bell.sam.core.exception.ServiceException;
import com.vennetics.bell.sam.core.exception.SpecificPolicyException;
import com.vennetics.bell.sam.core.exception.SpecificServiceException;
import com.vennetics.bell.sam.error.RestErrorMessageType;

@RunWith(value = MockitoJUnitRunner.class)
public class ExceptionTypeMapperTest {

    @Mock private ErrorDictionary mockDictionary;
    @Mock private HttpServletRequest mockRequest;

    @Autowired
    @InjectMocks
    private ExceptionTypeMapper mapper;

    @Test
    public void shouldMapApiException() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(new DummyPolicyException("address"));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.getMessageVariables()[0], is("address"));
        assertThat(mappedPolicyError.isPolicyError(), is(true));
        assertThat(mappedPolicyError.getTimestamp(), greaterThan(0L));

        final MappedError mappedServiceError = mapper.mapException(new DummyServiceException("address"));

        assertThat(mappedServiceError.getCode(), is("code"));
        assertThat(mappedServiceError.getStatusCode(), is(400));
        assertThat(mappedServiceError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.getMessageVariables()[0], is("address"));
        assertThat(mappedServiceError.isPolicyError(), is(false));
        assertThat(mappedPolicyError.getTimestamp(), greaterThan(0L));
    }

    @Test
    public void shouldMapHystrixCommandException() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(newHystrixCommandException(
                        new DummyServiceException("address")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.getMessageVariables()[0], is("address"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
    }

    @Test
    public void shouldMapHystrixNonCommandRuntimeException() {

        when(mockDictionary.getError(RestErrorMessageType.RESOURCE_UNAVAILABLE_COMMAND_FAILED)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(newHystrixRuntimeException(new RuntimeException("oops")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.getMessageVariables()[0], is("command failed"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
    }

    @Test
    public void shouldMapHystrixBadRequestException() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(newHystrixBadRequestException(new DummyPolicyException("address")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(true));
    }

    @Test
    public void shouldMapHystrixBadRequestExceptionWhenCauseIsNotDefined() {

        when(mockDictionary.getError(RestErrorMessageType.RESOURCE_INTERNAL_ERROR)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(newHystrixBadRequestException(null));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
    }

    @Test
    public void shouldMapAuthenticationExceptionWhenCauseIsApiException() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(newAuthenticationException(new DummyServiceException("address")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
    }

    @Test
    public void shouldMapAuthenticationExceptionWhenCauseIsNotApiException() {

        when(mockDictionary.getError(RestErrorMessageType.HTTP_401_UNAUTHORIZED)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(newAuthenticationException(new RuntimeException("bad")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
    }

    @Test
    public void shouldMapIllegalArgumentException() {

        when(mockDictionary.getError(RestErrorMessageType.RESOURCE_BAD_REQUEST_INVALID_ARGUMENT)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(new IllegalArgumentException("bad"));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.getMessageVariables()[0], is("bad"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
    }

    @Test
    public void shouldMapUnrecognisedExceptionAsInternalResourceFailure() {

        when(mockDictionary.getError(RestErrorMessageType.RESOURCE_INTERNAL_ERROR)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(new Exception("bad"));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
        assertThat(mappedPolicyError.getPath(), is(nullValue()));
    }

    @Test
    public void shouldMapUnrecognisedExecutionExceptionAsInternalResourceFailure() {

        when(mockDictionary.getError(RestErrorMessageType.RESOURCE_INTERNAL_ERROR)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(new ExecutionException(new Exception("bad")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
        assertThat(mappedPolicyError.getPath(), is(nullValue()));
    }

    @Test
    public void shouldMapRecognisedExecutionException() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(new ExecutionException(new DummyPolicyException("address")));

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(true));
        assertThat(mappedPolicyError.getPath(), is(nullValue()));
    }

    @Test
    public void shouldRecordRequestPath() {

        when(mockDictionary.getError(RestErrorMessageType.RESOURCE_INTERNAL_ERROR)).thenReturn(dummyErrorDescription());
        when(mockRequest.getPathInfo()).thenReturn("/ABC");

        final MappedError mappedPolicyError = mapper.mapException(new Exception("bad"), mockRequest);

        assertThat(mappedPolicyError.getCode(), is("code"));
        assertThat(mappedPolicyError.getStatusCode(), is(400));
        assertThat(mappedPolicyError.getErrorCode(), is("type"));
        assertThat(mappedPolicyError.getMessageTemplate(), is("message args=({0})"));
        assertThat(mappedPolicyError.isPolicyError(), is(false));
        assertThat(mappedPolicyError.getPath(), is("/ABC"));
    }

    @Test
    public void shouldUseNonSpecificMessageIdForNonSpecificPolicyExceptions() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedPolicyError = mapper.mapException(new DummyNonSpecificPolicyException("ARG"));

        assertThat(mappedPolicyError.getCode(), is("POL0001"));
        assertThat(mappedPolicyError.getMessageVariables()[0], is("code"));
        assertThat(mappedPolicyError.getMessageVariables()[1], is("ARG"));
    }

    @Test
    public void shouldUseNonSpecificMessageIdForNonSpecificServiceExceptions() {

        when(mockDictionary.getError(DummyErrorType.DUMMY_ERROR_1)).thenReturn(dummyErrorDescription());

        final MappedError mappedServiceError = mapper.mapException(new DummyNonSpecificServiceException("ARG"));

        assertThat(mappedServiceError.getCode(), is("SVC0001"));
        assertThat(mappedServiceError.getMessageVariables()[0], is("code"));
        assertThat(mappedServiceError.getMessageVariables()[1], is("ARG"));
    }

    private Throwable newHystrixCommandException(final Throwable cause) {
        return new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, DummyHystrixCommand.class, null, cause, null);
    }

    private Throwable newAuthenticationException(final Throwable cause) {
        return new AuthenticationException(null, cause) { };
    }

    private Throwable newHystrixRuntimeException(final Throwable cause) {
        return new HystrixRuntimeException(HystrixRuntimeException.FailureType.TIMEOUT, DummyHystrixCommand.class, "command failed", cause, null);
    }

    private Throwable newHystrixBadRequestException(final Throwable cause) {
        return new HystrixBadRequestException(null, cause);
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "message args=({0})");
    }

    private static class DummyNonSpecificPolicyException extends PolicyException {

        public DummyNonSpecificPolicyException(final String argument) {
            super(DummyErrorType.DUMMY_ERROR_1, argument);
        }
    }

    private static class DummyPolicyException extends SpecificPolicyException {

        public DummyPolicyException(final String argument) {
            super(DummyErrorType.DUMMY_ERROR_1, argument);
        }
    }

    private static class DummyServiceException extends SpecificServiceException {

        public DummyServiceException(final String argument) {
            super(DummyErrorType.DUMMY_ERROR_1, argument);
        }
    }

    private static class DummyNonSpecificServiceException extends ServiceException {

        public DummyNonSpecificServiceException(final String argument) {
            super(DummyErrorType.DUMMY_ERROR_1, argument);
        }
    }

    private static class DummyHystrixCommand implements HystrixInvokable<Object> {

    }

    private enum DummyErrorType {
        DUMMY_ERROR_1;
    }
}
