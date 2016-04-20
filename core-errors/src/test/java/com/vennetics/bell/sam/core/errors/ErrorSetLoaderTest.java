package com.vennetics.bell.sam.core.errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;

public class ErrorSetLoaderTest {

    @Rule
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public final ExpectedException thrown = ExpectedException.none();

    private ErrorDictionary dictionary;

    private ErrorSetLoader loader;

    @Before
    public void setUp() throws Exception {
        dictionary = new ErrorDictionary();
        loader = new ErrorSetLoader();

    }

    @Test
    public void shouldReturnLoadedErrors() {

        loader.loadErrorSet(dictionary, "/test-error-messages.yml", TestErrorMessageType.values());

        for (final TestErrorMessageType type : TestErrorMessageType.values()) {

            final ErrorDescription error = dictionary.getError(type);

            assertEquals(type, TestErrorMessageType.valueOf(error.getType()));

            assertNotNull(error.getCode());

            assertNotNull(error.getMessage());
        }
    }

    @Test
    public void shouldFailIfErrorIsMissingFromFile() {

        thrown.expectMessage("No Error definition for [TEST_MESSAGE_2]");
        loader.loadErrorSet(dictionary, "/test-error-missing-messages.yml", TestErrorMessageType.values());
    }

    @Test
    public void shouldFailIfTypesAreNotUnique() {

        thrown.expectMessage("Error type [TEST_MESSAGE_1] has already been defined");
        loader.loadErrorSet(dictionary, "/test-error-non-unique-type-messages.yml", TestErrorMessageType.values());
    }

    @Test
    public void shouldFailIfCodesAreNotUnique() {

        thrown.expectMessage("Error code [JBOX-00003] for type [TEST_MESSAGE_4] in file [/test-error-non-unique-code-messages.yml] is a duplicate.");
        loader.loadErrorSet(dictionary, "/test-error-non-unique-code-messages.yml", TestErrorMessageType.values());
    }

    @Test
    public void shouldFailIfFileCannotBeFound() {

        thrown.expectMessage("Failed to find file resource /nosuchfile.yml");
        loader.loadErrorSet(dictionary, "/nosuchfile.yml", TestErrorMessageType.values());
    }

    @Test
    public void shouldFailIfFileCannotBeParsed() {

        thrown.expectMessage("Failed to read error message file");
        loader.loadErrorSet(dictionary, "/test-invalid.yml", TestErrorMessageType.values());
    }
}
