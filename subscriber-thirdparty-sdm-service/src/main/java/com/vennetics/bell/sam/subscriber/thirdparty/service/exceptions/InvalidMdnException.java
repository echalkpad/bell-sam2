package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificPolicyException;

/**
 * When a received MDN does not match the expected format (10 digits by
 * default).
 */
public class InvalidMdnException extends SpecificPolicyException {

    private static final long serialVersionUID = 6141214224019977407L;

    public InvalidMdnException(final String invalidMdn) {
        super(SubscriberThirdPartySdmErrorTypes.POL_INVALID_MDN, invalidMdn);
    }

}
