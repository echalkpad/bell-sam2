package com.vennetics.bell.sam.rest.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.error.adapters.HttpStatusCodeExceptionEntityMapper;

/**
 * Abstract base class of Rest controllers in the API-Gateway, extending the {@link ExceptionHandlingRestController}
 * with functionality that recognises exceptions returned from an internal service
 * (recognised by an HttpStatusCodeException), and returns them unchanged to the caller.
 */

public abstract class PassThruExceptionHandlingRestController
                extends ExceptionHandlingRestController {

    private static final Logger LOG = LoggerFactory.getLogger(
                    PassThruExceptionHandlingRestController.class);

    @Autowired
    private ObjectMapper jsonObjectMapper;

    private HttpStatusCodeExceptionEntityMapper entityMapper;

    @PostConstruct
    public void init() {
        entityMapper = new HttpStatusCodeExceptionEntityMapper(jsonObjectMapper,
                                                               exceptionClasses());
    }

    @SuppressWarnings("rawtypes")
    @Override
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleAllExceptions(final Exception ex,
                                              final HttpServletRequest request) {

        LOG.debug("Exception {}/{} request={}", ex, ex.getMessage(), request);

        final ResponseEntity internalResponse = entityMapper.captureEntityFromException(ex);

        if (internalResponse != null) {
            return internalResponse;
        }

        return super.handleAllExceptions(ex, request);
    }




    /**
     * Deliver list of classes that might be returned from an internal service, in the event of
     * an exception.
     */
    protected abstract List<Class<?>> exceptionClasses();

}
