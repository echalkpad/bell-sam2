package com.vennetics.bell.sam.core.errors;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

/**
 * Default implementation of {@link IErrorDictionary}, extending {@link ConcurrentHashMap}.
 */
public class ErrorDictionary extends ConcurrentHashMap<String, ErrorDescription> implements IErrorDictionary {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorDictionary.class);

    @Override
    public ErrorDescription getError(final Enum<?> type) {
        LOG.debug(">>> getError {}", type);
        final ErrorDescription result = get(type.toString());
        if (result == null) {
            throw new PlatformFailedException("No error defined in dictionary for type " + type);
        }

        return result;
    }
}
