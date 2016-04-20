package com.vennetics.microservices.common.core.soap.gateway.adapter;

import org.apache.cxf.interceptor.Fault;

/**
 * A mapper of exceptions to SOAP {@link Fault}s.
 */

@FunctionalInterface
public interface ISoapExceptionMapper {

    /**
     * Constructs a SOAP {@link Fault} from an exception.
     *
     * @param exception
     *            the exception to be mapped.
     */
    Fault mapExceptionToFault(final Throwable exception);

}
