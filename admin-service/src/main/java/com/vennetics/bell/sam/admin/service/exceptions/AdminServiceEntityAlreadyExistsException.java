package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;

/**
 *  Admin Service an entity already exists with this Id exception
 */
public class AdminServiceEntityAlreadyExistsException extends BadRequestException {

    private static final long serialVersionUID = -1978341613123836547L;

    /**
     * Constructor defining entity already exists.
     *
     * @param entityId the id's of the entity that already exists.
     */
    public AdminServiceEntityAlreadyExistsException(final String entityId) {
        super(AdminServiceErrorTypes.ID_IN_USE, entityId);
    }
}
