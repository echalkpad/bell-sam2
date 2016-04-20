package com.vennetics.bell.sam.rest.controller;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Abstract base class of Rest controllers that implement the NetAPI specification, with generic exception handling function.
 * <p>
 */
@SuppressWarnings("unchecked")
public abstract class ExceptionHandlingOneapiRestController
                extends ExceptionHandlingRestController {

    @Autowired
    @Qualifier("oneapiErrorAdapter")
    private IErrorAdapter errorAdapter;

    /**
     * Deliver the {@link IErrorAdapter} used to create an exception that can be returned to the caller.
     */
    @Override
    protected IErrorAdapter getErrorAdapter() {

        return errorAdapter;

    }
}
