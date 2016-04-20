package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificPolicyException;

/**
 * Thrown if an OU specific in a request does match (configured) list of known
 * OU codes.
 */
public class InvalidOuException extends SpecificPolicyException {

    private static final long serialVersionUID = 6573388318946826486L;

    public InvalidOuException(final String... ouCode) {
        super(SubscriberThirdPartySdmErrorTypes.SVC_INVALID_OU, ouCode);
    }

}
