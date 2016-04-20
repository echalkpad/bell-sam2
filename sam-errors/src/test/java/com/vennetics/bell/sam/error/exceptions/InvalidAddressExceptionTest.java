package com.vennetics.bell.sam.error.exceptions;

import com.vennetics.bell.sam.core.exception.AbstractFormattedException;
import com.vennetics.bell.sam.error.ServiceErrorMessageType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created on 04/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class InvalidAddressExceptionTest {

    // Object Under Test
    private InvalidAddressException objectUnderTest;

    // Mock Objects
    @Mock
    private Logger mockLogger;

    // Concrete Objects
    private String address = "address";

    @Before
    public void setUp() throws Exception {
        AbstractFormattedException.setLogger(mockLogger);
    }

    @Test
    public void shouldConstructInvalidAddressExceptionAsExpected() {
        objectUnderTest = new InvalidAddressException(address);
        assertEquals(objectUnderTest.getMessageArguments()[0], address);
        assertEquals(objectUnderTest.getErrorMessageType(), ServiceErrorMessageType.NO_VALID_ADDRESS_ERROR);
    }
}
