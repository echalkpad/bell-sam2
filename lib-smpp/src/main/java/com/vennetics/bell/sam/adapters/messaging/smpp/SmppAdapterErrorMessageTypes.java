package com.vennetics.bell.sam.adapters.messaging.smpp;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Error message types for
 * {@link com.vennetics.bell.sam.core.errors.ErrorDictionaryLoader}.
 */
@ErrorTypeSet("/smpp-error-messages.yml")
public enum SmppAdapterErrorMessageTypes {

                                          SMPP_ERROR_SENDING_OUTBOUND_MESSAGE;
}
