package com.vennetics.bell.sam.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;

/**
 * Abstract base class of Rest controllers that implement the NetAPI specification, with generic exception handling function.
 * <p/>
 */
public abstract class ExceptionHandlingNetapiRestController extends ExceptionHandlingRestController {

    @Autowired
    @Qualifier("netapiErrorAdapter")
    private IErrorAdapter errorAdapter;

    /**
     * Deliver the {@link IErrorAdapter} used to create an exception that can be returned to the caller.
     */
    @Override
    protected IErrorAdapter getErrorAdapter() {

        return errorAdapter;

    }
}
