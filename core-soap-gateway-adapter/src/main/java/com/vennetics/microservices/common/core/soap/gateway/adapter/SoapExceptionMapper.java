package com.vennetics.microservices.common.core.soap.gateway.adapter;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.cxf.interceptor.Fault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;

/**
 * Default implementation of {@link ISoapExceptionMapper}.
 */

public class SoapExceptionMapper implements ISoapExceptionMapper {

    private static final Logger logger = LoggerFactory.getLogger(SoapExceptionMapper.class);

    private final IExceptionTypeMapper exceptionMapper;
    private final IErrorAdapter errorAdapter;

    public SoapExceptionMapper(final IExceptionTypeMapper exceptionMapper, final IErrorAdapter errorAdapter) {
        this.exceptionMapper = exceptionMapper;
        this.errorAdapter = errorAdapter;
    }

    @Override
    public Fault mapExceptionToFault(final Throwable cause) {
        logger.debug(">>> mapExceptionToFault {}", cause.getClass());

        final MappedError mappedError = exceptionMapper.mapException(cause);
        logger.debug("mappedError:{}", mappedError);
        return faultFromMappedError(mappedError);
    }

    private Fault faultFromMappedError(final MappedError mappedError) {
        final Fault fault = new Fault((Throwable) errorAdapter.adapt(mappedError));
        fault.setStatusCode(mappedError.getStatusCode());
        return fault;
    }

    protected IExceptionTypeMapper getExceptionMapper() {
        return exceptionMapper;
    }

    protected static Fault newFault(final Throwable exception, final int statusCode) {
        final Fault fault = new Fault(exception);
        fault.setStatusCode(statusCode);
        return fault;
    }

    protected HttpClientErrorException getCauseIfHttpClientErrorException(final Throwable cause) {

        // Sub-X service commands will wrap any thrown HttpClientErrorException
        // as a bad request.
        // Therefore this should be in the nested cause of the exception.
        final int indexOfHttpClientErrorException = ExceptionUtils.indexOfThrowable(cause,
                                                                                    HttpClientErrorException.class);
        if (indexOfHttpClientErrorException > -1) {

            return (HttpClientErrorException) ExceptionUtils.getThrowableList(cause)
                                                            .get(indexOfHttpClientErrorException);

        }

        return null;
    }
}
