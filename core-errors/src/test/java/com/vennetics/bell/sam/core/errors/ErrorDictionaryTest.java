package com.vennetics.bell.sam.core.errors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ErrorDictionaryTest {

    @Rule
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public final ExpectedException thrown = ExpectedException.none();

    private static ErrorDictionary dictionary;


    @Before
    public void setUp() throws Exception {
        dictionary = new ErrorDictionary();
    }


    @Test
    public void shouldFailIfErrorCodeNotFound() {

        thrown.expectMessage("No error defined in dictionary for type TEST_MESSAGE_1");
        dictionary.getError(TestErrorMessageType.TEST_MESSAGE_1);
    }
}
