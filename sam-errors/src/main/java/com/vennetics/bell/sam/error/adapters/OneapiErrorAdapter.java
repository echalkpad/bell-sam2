package com.vennetics.bell.sam.error.adapters;

import com.vennetics.bell.sam.error.Restv091Error;
import com.vennetics.bell.sam.error.mapper.MappedError;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Implements {@link IErrorAdapter}, adapting a {@link MappedError} to a {@link Restv091Error}.
 */

@Service
public class OneapiErrorAdapter implements IErrorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(OneapiErrorAdapter.class);

    @Override
    public Object adapt(final MappedError error) {
        String errorMessage;
        LOG.debug(">>> adapt {}", error);
        if (error.isPolicyError()) {
            errorMessage = "A policy error occurred. Error code is: " + error.getCode() + ": " + error.getMessageTemplate();
        } else {
            errorMessage = error.getMessageTemplate();
        }
        MessageFormat form = new MessageFormat(errorMessage);

        return new Restv091Error(form.format(error.getMessageVariables()));
    }
}
