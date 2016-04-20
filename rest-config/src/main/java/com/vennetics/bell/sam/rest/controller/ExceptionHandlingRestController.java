package com.vennetics.bell.sam.rest.controller;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract base class of Rest controllers, with generic exception handling function.
 * <p/>
 * Otherwise, the exception is passed to the generic {@link IExceptionTypeMapper} and the generic {@link MappedError}
 * that it produces is passed to an {@link IErrorAdapter} provided by concrete implementation    to create the exception
 * entity that is returned to the caller.
 */
@SuppressWarnings("unchecked")
public abstract class ExceptionHandlingRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlingRestController.class);

    @Autowired
    private IExceptionTypeMapper exceptionMapper;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleAllExceptions(final Exception ex,
                                              final HttpServletRequest request) {

        LOG.debug("Exception {}/{} request={}", ex, ex.getMessage(), request);

        final MappedError error = exceptionMapper.mapException(ex, request);
        return new ResponseEntity(getErrorAdapter().adapt(error), error.httpStatusCode());
    }

    /**
     * Deliver the {@link IErrorAdapter} used to create an exception that can be returned to the caller.
     */
    protected abstract IErrorAdapter getErrorAdapter();
}
