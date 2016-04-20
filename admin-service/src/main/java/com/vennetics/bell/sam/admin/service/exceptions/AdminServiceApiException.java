package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;

/**
 * The Main exception class for the Admin Service API
 */
public class AdminServiceApiException extends BadRequestException {

    private static final long serialVersionUID = 8680413937025484752L;

    /**
     * Constructor defining invalid field.
     *
     * @param invalidField
     *            the name of the invalidField.
     */
    public AdminServiceApiException(final String invalidField) {
        super(AdminServiceErrorTypes.INVALID_DATA, invalidField);
    }

}
