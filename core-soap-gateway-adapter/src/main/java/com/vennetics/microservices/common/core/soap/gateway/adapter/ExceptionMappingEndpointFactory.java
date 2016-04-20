package com.vennetics.microservices.common.core.soap.gateway.adapter;

import org.apache.cxf.BusFactory;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.service.invoker.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExceptionMappingEndpointFactory implements IEndpointFactory {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionMappingEndpointFactory.class);

    @Override
    public EndpointImpl exceptionMappingEndpoint(final String address,
                                                 final Object implementor,
                                                 final ISoapExceptionMapper soapExceptionMapper,
                                                 final boolean shouldAddLoggingInterceptors) {
        logger.debug(">>> exceptionMappingEndpoint {}/{}", address, implementor);

        final EndpointImpl endpoint = new EndpointImpl(BusFactory.getThreadDefaultBus(),
                                                       implementor,
                                                       newExceptionMappingServerFactoryBean(soapExceptionMapper));
        endpoint.publish(address);
        if (shouldAddLoggingInterceptors) {
            endpoint.getInInterceptors().add(new LoggingInInterceptor());
            endpoint.getInFaultInterceptors().add(new LoggingInInterceptor());
            endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
            endpoint.getOutFaultInterceptors().add(new LoggingOutInterceptor());
        }
        return endpoint;
    }

    private static JaxWsServerFactoryBean newExceptionMappingServerFactoryBean(final ISoapExceptionMapper soapExceptionMapper) {

        return new ExceptionMappingServerFactoryBean(soapExceptionMapper);
    }

    protected static class ExceptionMappingServerFactoryBean extends JaxWsServerFactoryBean {
        private final ISoapExceptionMapper exceptionHandler;

        public ExceptionMappingServerFactoryBean(final ISoapExceptionMapper exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        protected Invoker createInvoker() {
            return new ExceptionMappingJAXWSMethodInvoker(new SingletonFactory(getServiceBean()),
                                                          exceptionHandler);
        }
    }
}
