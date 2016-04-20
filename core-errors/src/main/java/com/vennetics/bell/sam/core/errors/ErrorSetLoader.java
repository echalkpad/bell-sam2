package com.vennetics.bell.sam.core.errors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.core.errors.model.ErrorSet;

/**
 * Default implementation of {@link IErrorSetLoader}.
 */

@Component
public final class ErrorSetLoader implements IErrorSetLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorSetLoader.class);

    private static final ObjectMapper OBJECT_MAPPER = createYmlObjectMapper();

    @Override
    public void loadErrorSet(final ErrorDictionary dictionary, final String errorFile, final Enum<?>[] keys) {

        LOG.debug(">>> loadErrorSet {}", errorFile);

        final ErrorSet errors = parseErrorFile(errorFile);

        addErrorDefinitions(dictionary, errors);

        verifyAllErrorsHaveDefinitions(dictionary, errorFile, keys);

        verifyAllErrorCodesAreUnique(dictionary, errorFile);
    }

    private static void addErrorDefinitions(final ErrorDictionary dictionary,
                                            final ErrorSet errors) {
        for (final ErrorDescription error : errors.getErrorDescriptions()) {
            if (dictionary.get(error.getType()) != null) {
                throw new BeanInitializationException("Error type [" + error.getType() + "] has already been defined");
            }

            dictionary.put(error.getType(), error);
        }
    }

    private ErrorSet parseErrorFile(final String errorFile) {

        try (final InputStream xmlFile = getInputStreamFromFile(errorFile)) {
            return OBJECT_MAPPER.readValue(xmlFile, ErrorSet.class);

        } catch (final IOException e) {
            throw new BeanInitializationException("Failed to read error message file", e);
        }
    }

    private InputStream getInputStreamFromFile(final String file) {
        final InputStream stream = getClass().getResourceAsStream(file);
        if (stream == null) {
            throw new BeanInitializationException("Failed to find file resource " + file);
        }
        return stream;
    }

    private static void verifyAllErrorsHaveDefinitions(final ErrorDictionary dictionary,
                                                       final String errorFile,
                                                       final Enum<?>[] keys) {
        for (final Enum<?> key : keys) {
            if (dictionary.get(key.toString()) == null) {
                throw new BeanInitializationException("No Error definition for [" + key.toString()
                                + "] from file [" + errorFile + "]");
            }
        }
    }

    private static void verifyAllErrorCodesAreUnique(final ErrorDictionary dictionary,
                                                     final String errorFile) {
        final List<String> errorCodes = new ArrayList<>();

        for (final ErrorDescription error : dictionary.values()) {

            if (errorCodes.contains(error.getCode())) {
                throw new BeanInitializationException("Error code [" + error.getCode()
                                + "] for type [" + error.getType() + "] in file [" + errorFile
                                + "] is a duplicate.");
            }
            errorCodes.add(error.getCode());
        }
    }

    private static ObjectMapper createYmlObjectMapper() {
        return new ObjectMapper(new YAMLFactory());
    }
}
