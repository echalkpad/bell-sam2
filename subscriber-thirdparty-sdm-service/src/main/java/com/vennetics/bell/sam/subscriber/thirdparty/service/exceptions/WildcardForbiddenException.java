package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificPolicyException;

/**
 * Thrown when a request contains a wildcard.
 */
public class WildcardForbiddenException extends SpecificPolicyException {

    private static final long serialVersionUID = 6141214224019977407L;

    public WildcardForbiddenException(final String searchCriteria) {
        super(SubscriberThirdPartySdmErrorTypes.POL_WILDCARD_FORBIDDEN, searchCriteria);
    }

}
