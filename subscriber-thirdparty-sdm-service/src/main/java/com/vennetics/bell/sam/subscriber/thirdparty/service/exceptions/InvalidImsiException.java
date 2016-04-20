package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificPolicyException;

/**
 * When a received IMSI does not match the expected format (15 digits by
 * default).
 */
public class InvalidImsiException extends SpecificPolicyException {

    private static final long serialVersionUID = 6141214224019977407L;

    public InvalidImsiException(final String invalidImsi) {
        super(SubscriberThirdPartySdmErrorTypes.POL_INVALID_IMSI, invalidImsi);
    }

}
