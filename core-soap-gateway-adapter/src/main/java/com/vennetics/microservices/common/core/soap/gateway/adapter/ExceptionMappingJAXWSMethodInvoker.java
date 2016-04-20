package com.vennetics.microservices.common.core.soap.gateway.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JAXWSMethodInvoker;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.invoker.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension to {@link JAXWSMethodInvoker} that wraps the service method calls, so that it can trap
 * any exceptions thrown by the method and map them to the appropriate Service/PolicyException wrapped in
 * a SOAP {@link Fault}.
 */

public class ExceptionMappingJAXWSMethodInvoker extends JAXWSMethodInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionMappingJAXWSMethodInvoker.class);

    private ISoapExceptionMapper exceptionHandler;

    public ExceptionMappingJAXWSMethodInvoker(final Factory factory,
                                              final ISoapExceptionMapper exceptionHandler) {
        super(factory);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    protected Object performInvocation(final Exchange exchange,
                                       final Object serviceObject,
                                       final Method method,
                                       final Object[] paramArray) throws Exception {
        LOG.debug(">>> performInvocation {}", method.getName());
        try {
            return super.performInvocation(exchange, serviceObject, method, paramArray);
        } catch (final InvocationTargetException e) {
            LOG.trace("Method {} failed:", method.getName(), e);
            LOG.debug("Caught exception {}", e.getCause().getClass());
            Fault f = exceptionHandler.mapExceptionToFault(e.getCause());
            LOG.debug("Throwing {}/{}", f.getMessage(), f.getStatusCode());
            throw f;
        }
    }
}
