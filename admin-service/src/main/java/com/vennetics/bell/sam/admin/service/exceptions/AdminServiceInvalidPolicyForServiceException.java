package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;


/**
 *  Admin Service - the policy that you're trying to add is not valid on this service
 */
public class AdminServiceInvalidPolicyForServiceException extends BadRequestException {

    private static final long serialVersionUID = -8681335794696646252L;

    /**
     * Constructor defining invalid policy policyTypeId for service serviceTypeId.
     *
     * @param policyTypeId the policyTypeId that is not valid
     * @param serviceTypeId the serviceTypeId that the policy is not valid on
     */
    public AdminServiceInvalidPolicyForServiceException(final String policyTypeId, final String serviceTypeId) {
        super(AdminServiceErrorTypes.INVALID_POLICY_FOR_SERVICE, policyTypeId, serviceTypeId);
    }
}
