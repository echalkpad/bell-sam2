package com.vennetics.bell.sam.core.exception;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

public class AbstractFormattedExceptionTest {

    @Mock private Logger mockLogger;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AbstractFormattedException.setLogger(mockLogger);
    }

    @Test
    public void shouldConstructFromMessageCode() {

        final AbstractFormattedException exception = new AbstractFormattedException("MessageCode") { };
        assertThat(exception.getMessageCode(), is("MessageCode"));
        assertThat(exception.getFormattedMessage(), is("MessageCode"));
        verify(mockLogger).warn("[MessageCode]: MessageCode");
    }

    @Test
    public void shouldConstructFromMessageCodeAndMessage() {

        final AbstractFormattedException exception = new AbstractFormattedException("MessageCode", "Message") { };
        assertThat(exception.getMessageCode(), is("MessageCode"));
        assertThat(exception.getFormattedMessage(), is("Message"));
        verify(mockLogger).warn("[MessageCode]: Message");
    }

    @Test
    public void shouldConstructFromMessageCodeAndException() {

        final Throwable dummyException = new Throwable();
        final AbstractFormattedException exception = new AbstractFormattedException(dummyException, "MessageCode") { };
        assertThat(exception.getMessageCode(), is("MessageCode"));
        assertThat(exception.getFormattedMessage(), is("MessageCode"));
        verify(mockLogger).warn("[MessageCode]: MessageCode", dummyException);
    }

    @Test
    public void shouldConstructFromMessageCodeAndMessageAndException() {

        final Throwable dummyException = new Throwable();
        final AbstractFormattedException exception = new AbstractFormattedException(dummyException, "MessageCode", "Message") { };
        assertThat(exception.getMessageCode(), is("MessageCode"));
        assertThat(exception.getFormattedMessage(), is("Message"));
        verify(mockLogger).warn("[MessageCode]: Message", dummyException);
    }

    @Test
    public void shouldConstructFromNullMessageCode() {

        final AbstractFormattedException exception = new AbstractFormattedException(null) { };
        assertThat(exception.getMessageCode(), is(Matchers.nullValue()));
        assertThat(exception.getFormattedMessage(), is(Matchers.nullValue()));
        verify(mockLogger).warn("Generating error with type [null]");
    }
}
