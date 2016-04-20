package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.PolicyException;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.ServiceException;

/**
 * Localises exception translation logic to tidy up the use of having two
 * classes of same name created when mapped from wsdl. One is the Soap fault
 * that can be thrown as an exception to CXF. The other is the schema defined
 * pojo containing the service information.
 */
public final class SubscriberThirdPartyExceptionUtils {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThirdPartyExceptionUtils.class);

    private SubscriberThirdPartyExceptionUtils() {

    }

    public static generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.PolicyException newPolicyException(final String messageId,
                                                                                                              final String text,
                                                                                                              final String[] variables) {

        logger.debug("PolicyException messageId={}, text={}, variables={}",
                     messageId,
                     text,
                     variables);

        final PolicyException exception = newPolicyExceptionObject(messageId, text, variables);

        logger.debug("Mapped to: {}", exception);

        return new generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.PolicyException("PolicyException",
                                                                                            exception);
    }

    public static generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.ServiceException newServiceException(final String messageId,
                                                                                                                final String text,
                                                                                                                final String[] variables) {

        logger.debug("ServiceException messageId={}, text={}, variables={}",
                     messageId,
                     text,
                     variables);

        final ServiceException exception = newServiceExceptionObject(messageId, text, variables);

        logger.debug("Mapped to: {}", exception);

        return new generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.ServiceException("ServiceException",
                                                                                             exception);

    }

    private static ServiceException newServiceExceptionObject(final String messageId,
                                                              final String text,
                                                              final String[] variables) {
        final ServiceException exception = new ServiceException();
        exception.setMessageId(messageId);
        exception.setText(text);
        if (variables != null) {
            exception.getVariables().addAll(Arrays.asList(variables));
        }

        return exception;

    }

    private static PolicyException newPolicyExceptionObject(final String messageId,
                                                            final String text,
                                                            final String[] variables) {
        final PolicyException exception = new PolicyException();
        exception.setMessageId(messageId);
        exception.setText(text);
        if (variables != null) {
            exception.getVariables().addAll(Arrays.asList(variables));
        }

        return exception;

    }
}
