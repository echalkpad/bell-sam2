package com.vennetics.bell.sam.admin.service.api;

import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetailsList;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.RoleList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceTypeList;
import rx.Observable;

import java.util.UUID;

/**
 * ApplicationAdminService Interface
 */
public interface IApplicationAdminService {

    /**
     * Returns a list of all Applications on the system
     *
     * @return A list of all the applications
     */
    Observable<ApplicationUserDetailsList> getAllApplications();

    /**
     * Creates an Application and corresponding entities
     *
     * @param applicationToCreate
     *          the details of the new application
     * @return the new applications UUID
     */
    Observable<ApplicationUserDetails> createApplication(final ApplicationUserDetails applicationToCreate);

    /**
     * Gets an Application by the Id provided
     *
     * @param applicationId
     *          the id of the Application to find
     * @return the found application
     */
    Observable<ApplicationUserDetails> getApplicationById(final UUID applicationId);

    /**
     * Delete an Application by the Id provided
     *
     * @param applicationId
     *          the id of the Application to delete
     */
    Observable<Void> deleteApplication(final UUID applicationId);

    /**
     * Updates an Application
     *
     * @param applicationId
     *          the id of the Application to update
     * @param updatedApplication
     *          the details of the application to update
     * @return the updated Application
     */
    Observable<ApplicationUserDetails> updateApplication(final UUID applicationId, final ApplicationUserDetails updatedApplication);

    /**
     * Add a AllowedService on an Application
     *
     * @param applicationId
     *          the id of the Application
     * @param serviceId
     *          the Id of the service to add on this application
     * @return the Application which now has the specified service added.
     */
    Observable<ApplicationAllowedService> addService(final UUID applicationId, final String serviceId);

    /**
     * Delete a AllowedService on an Application
     *
     * @param applicationId
     *          the id of the Application
     * @param serviceId
     *          the Id of the service to delete from this application
     */
    Observable<Void> deleteService(final UUID applicationId, final String serviceId);

    /**
     * Add a Role to an Application
     *
     * @param applicationId
     *          the id of the Application
     * @param roleId
     *          the Id of the role to add to this application
     * @return the Application which now has the specified role added.
     */
    Observable<Role> addRole(final UUID applicationId, final String roleId);

    /**
     * Delete a Role on an Application
     *
     * @param applicationId
     *          the id of the Application
     * @param roleId
     *          the Id of the role to delete from this application
     */
    Observable<Void> deleteRole(final UUID applicationId, final String roleId);

    /**
     * Returns a list of all AllowedService Types on the system
     *
     * @return A list of all the service types
     */
    Observable<ServiceTypeList> getAllServiceTypes();

    /**
     * Returns a list of all Roles within the system
     *
     * @return A list of all the roles
     */
    Observable<RoleList> getAllRoles();

    /**
     * Add a Policy to an Application
     *
     * @param applicationId
     *          the id of the Application
     * @param policyTypeId
     *          the Id of the PolicyType
     * @param policy
     *          the Policy to add to the Application
     * @return the Application which now has the specified policy added.
     */
    Observable<ApplicationPolicy> addApplicationPolicy(
        final UUID applicationId, final String policyTypeId, final ApplicationPolicy policy);

    /**
     * Delete the Application level Policy from the Application.
     *
     * @param applicationId
     *          the id of the Application that the Policy is to be removed from
     * @param policyTypeId
     *          the id of the Policy that is to be removed from the Application
     */
    Observable<Void> deleteApplicationPolicy(final UUID applicationId, final String policyTypeId);


    /**
     * Add a Policy to a ServiceType on a Application
     *
     * @param applicationId
     *          the id of the Application
     * @param serviceTypeId
     *          the Id of the ServiceType
     * @param policyTypeId
     *          the Id of the PolicyType
     * @param policy
     *          the Policy to add to the Application
     * @return the Application which now has the specified policy added.
     */
    Observable<ApplicationPolicy> addServicePolicy(
        final UUID applicationId, final String serviceTypeId, final String policyTypeId, final ApplicationPolicy policy);


    /**
     * Delete the Service level Policy from the Application.
     *
     * @param applicationId
     *          the id of the Application that the Service level Policy is to be removed from
     * @param serviceTypeId
     *          the id of the Service from which the Policy is to be removed from
     * @param policyTypeId
     *          the id of the Policy that is to be removed
     */
    Observable<Void> deleteServicePolicy(final UUID applicationId, final String serviceTypeId, final String policyTypeId);


    /**
     * Returns a list of all the Protocol Types within the system
     *
     * @return A list of all the Protocol Types
     */
    Observable<ProtocolTypeList> getAllProtocolTypes();

    /**
     * Returns a list of all the Policy Types within the system
     *
     * @return A list of all the Policy Types
     */
    Observable<PolicyTypeList> getAllPolicyTypes();


}
