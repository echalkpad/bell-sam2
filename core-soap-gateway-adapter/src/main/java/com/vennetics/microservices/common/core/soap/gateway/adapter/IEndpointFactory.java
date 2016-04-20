package com.vennetics.microservices.common.core.soap.gateway.adapter;

import org.apache.cxf.jaxws.EndpointImpl;

/**
 * Delivers CXF endpoint implementations.
 */
@SuppressWarnings("squid:S1609") // Avoid Sonarqube suggestion about FunctionalInterface. Other methods likely to follow...
public interface IEndpointFactory {

    /**
     * Delivers an {@link EndpointImpl} that will map exceptions arising from
     * the invocation of service methods to SOAP
     * {@link org.apache.cxf.interceptor.Fault}s.
     * 
     * @param address
     *            The address of the service
     * @param implementor
     *            the object that implements the service
     * @param soapExceptionMapper
     *            the {@link ISoapExceptionMapper} to be used to map errors to
     *            the appropriate Service/PolicyExceptions.
     * @param shouldAddLoggingInterceptors
     *            indicates whether logging interceptors should be added to the
     *            interceptor chains.
     * @return the created {@link EndpointImpl}
     */
    EndpointImpl exceptionMappingEndpoint(final String address,
                                          final Object implementor,
                                          final ISoapExceptionMapper soapExceptionMapper,
                                          final boolean shouldAddLoggingInterceptors);

}
