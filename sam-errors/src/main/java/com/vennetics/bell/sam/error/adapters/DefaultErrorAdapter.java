package com.vennetics.bell.sam.error.adapters;

import com.vennetics.bell.sam.error.mapper.MappedError;
import org.springframework.stereotype.Service;

/**
 * Default error adapter implementation. Applies no translation rules.
 */
@Service(DefaultErrorAdapter.SERVICE_NAME)
public class DefaultErrorAdapter implements IErrorAdapter {

    public static final String SERVICE_NAME = "defaultErrorAdapter";

    @Override public Object adapt(final MappedError error) {
        return error;
    }
}
