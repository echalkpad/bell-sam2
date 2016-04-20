package com.vennetics.bell.sam.core.exception;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

public class ServiceAffectingExceptionTest {

    @Mock
    private Logger mockLogger;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AbstractFormattedException.setLogger(mockLogger);
    }

    @Test
    public void shouldConstructFromMessageType() {

        final ServiceAffectingException exception = new ServiceAffectingException(TestErrorType.TYPE_1) { };

        assertThat(exception.getErrorMessageType(), is(TestErrorType.TYPE_1));
        assertThat(exception.getMessageArguments().length, is(0));
        assertThat(exception.getMessage(), is("TYPE_1"));
    }

    @Test
    public void shouldConstructFromMessageTypeAndArguments() {

        final ServiceAffectingException exception = new ServiceAffectingException(TestErrorType.TYPE_1, "A1", "A2") { };
        assertThat(exception.getErrorMessageType(), is(TestErrorType.TYPE_1));
        assertThat(exception.getMessageArguments().length, is(2));
        assertThat(exception.getMessageArguments()[0], is("A1"));
        assertThat(exception.getMessage(), is("TYPE_1 arg[0]=[A1] arg[1]=[A2] "));
    }

    @Test
    public void shouldConstructFromExceptionAndMessageType() {

        final Throwable dummyCause = new Throwable();

        final ServiceAffectingException exception = new ServiceAffectingException(dummyCause, TestErrorType.TYPE_1) { };

        assertThat(exception.getErrorMessageType(), is(TestErrorType.TYPE_1));
        assertThat(exception.getMessageArguments().length, is(0));
        assertThat(exception.getMessage(), is("TYPE_1"));

        verify(mockLogger).warn("[TYPE_1]: TYPE_1", dummyCause);
    }

    @Test
    public void shouldConstructFromExceptionAndMessageTypeAndArguments() {

        final Throwable dummyCause = new Throwable();

        final ServiceAffectingException exception = new ServiceAffectingException(dummyCause, TestErrorType.TYPE_1, "A1", "A2") { };
        assertThat(exception.getErrorMessageType(), is(TestErrorType.TYPE_1));
        assertThat(exception.getMessageArguments().length, is(2));
        assertThat(exception.getMessageArguments()[0], is("A1"));
        assertThat(exception.getMessage(), is("TYPE_1 arg[0]=[A1] arg[1]=[A2] "));

        verify(mockLogger).warn("[TYPE_1 arg[0]=[A1] arg[1]=[A2] ]: TYPE_1 arg[0]=[A1] arg[1]=[A2] ", dummyCause);
    }

    private enum TestErrorType {
        TYPE_1;
    }
}
