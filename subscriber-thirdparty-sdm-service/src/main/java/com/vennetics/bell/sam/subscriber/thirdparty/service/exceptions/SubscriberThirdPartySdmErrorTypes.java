package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Enum defining SDM adapter error types.
 */
@ErrorTypeSet("/subscriber-thirdparty-sdm-error-messages.yml")
public enum SubscriberThirdPartySdmErrorTypes {

                                               SVC_SUBSCRIBER_NOT_UNIQUE,

                                               SVC_SUBSCRIBER_NOT_FOUND,

                                               SVC_INVALID_OU,

                                               POL_INVALID_MDN,

                                               POL_INVALID_FILTER_TYPE,

                                               POL_WILDCARD_FORBIDDEN,

                                               POL_INVALID_IMSI;
}
