package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.MappedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static com.vennetics.bell.sam.oneapi.rest.v091.sms.ErrorConstants.ERROR_TAG_END;
import static com.vennetics.bell.sam.oneapi.rest.v091.sms.ErrorConstants.ERROR_TAG_START;
import static com.vennetics.bell.sam.oneapi.rest.v091.sms.ErrorConstants.XML_DECLARATION;

/**
 * Implements {@link IErrorAdapter}, adapting a {@link MappedError} to a {@link String}.
 * <p>
 * Unfortunately the error object for OneAPI v0.91 is not a valid XML document. It doesn't
 * contain a XML root and as a result we need to build up the error as a {@link String} rather
 * than marshalling to an object.
 */

@Service
public class OneapiErrorAsStringAdapter implements IErrorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(OneapiErrorAsStringAdapter.class);

    @Override
    public Object adapt(final MappedError error) {
        String errorMessage;
        LOG.debug(">>> adapt {}", error);
        if (error.isPolicyError()) {
            errorMessage = "A policy error occurred. Error code is: " + error.getCode() + ": "
                            + error.getMessageTemplate();
        } else {
            errorMessage = error.getMessageTemplate();
        }

        MessageFormat form = new MessageFormat(errorMessage);

        final StringBuilder sb = new StringBuilder(160);

        return sb.append(XML_DECLARATION)
                 .append(ERROR_TAG_START)
                 .append(form.format(error.getMessageVariables()))
                 .append(ERROR_TAG_END)
                 .toString();
    }
}
