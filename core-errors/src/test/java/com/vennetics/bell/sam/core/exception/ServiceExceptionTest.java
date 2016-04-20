package com.vennetics.bell.sam.core.exception;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

public class ServiceExceptionTest {

    @Mock
    private Logger mockLogger;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AbstractFormattedException.setLogger(mockLogger);
    }

    @Test
    public void shouldConstructFromMessageType() {

        final ServiceException exception = new ServiceException(TestErrorType.TYPE_1) { };

        assertThat(exception.getErrorMessageType(), is(TestErrorType.TYPE_1));
        assertThat(exception.getMessageArguments().length, is(0));
        assertThat(exception.getMessage(), is("TYPE_1"));
    }

    @Test
    public void shouldConstructFromMessageTypeAndArguments() {

        final ServiceException exception = new ServiceException(TestErrorType.TYPE_1, "A1", "A2") { };
        assertThat(exception.getErrorMessageType(), is(TestErrorType.TYPE_1));
        assertThat(exception.getMessageArguments().length, is(2));
        assertThat(exception.getMessageArguments()[0], is("A1"));
        assertThat(exception.getMessage(), is("TYPE_1 arg[0]=[A1] arg[1]=[A2] "));
    }

    @Test
    public void shouldDefaultToNonSpecificExceptionType() {

        final ServiceException exception = new ServiceException(TestErrorType.TYPE_1) { };

        assertThat(exception.isSpecificException(), is(false));
        assertThat(exception.getNonSpecificMessageId(), is(ServiceException.NON_SPECIFIC_SERVICE_MESSAGE_ID));
    }

    private enum TestErrorType {
        TYPE_1;
    }
}
