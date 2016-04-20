package com.vennetics.bell.sam.admin.service.controllers;

import com.vennetics.bell.sam.admin.service.api.IApplicationAdminService;
import com.vennetics.bell.sam.error.adapters.DefaultErrorAdapter;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.rest.controller.ExceptionHandlingRestController;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetailsList;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.RoleList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceTypeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.UUID;

import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_APPLICATIONS;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.VERSION_1;
/**
 * Spring REST Controller for the Orchestration Layer of the Admin AllowedService
 *
 */
@RestController
@RequestMapping("/" + VERSION_1 + "/" + PATH_APPLICATIONS)
public class ApplicationAdminServiceController extends ExceptionHandlingRestController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAdminServiceController.class);
    public static final String VERSION_1 = "v1";
    public static final String PATH_APPLICATIONS = "applications";
    public static final String PATH_APPLICATION = "application";
    public static final String PATH_SERVICES = "services";
    public static final String PATH_ROLES = "roles";
    public static final String PATH_SERVICE_TYPES = "serviceTypes";
    public static final String PATH_POLICIES = "policies";
    public static final String PATH_PROTOCOL_TYPES = "protocolTypes";
    public static final String PATH_POLICY_TYPES = "policyTypes";

    @Autowired
    private IApplicationAdminService applicationAdminService;

    @Autowired
    @Qualifier(DefaultErrorAdapter.SERVICE_NAME)
    private IErrorAdapter errorAdapter;

    @PostConstruct
    public void init() {
        logger.debug("Initialised ApplicationAdminServiceController");
    }

    /**
     * Get all Applications
     *
     * @return all the Applications
     */
    @RequestMapping(method = RequestMethod.GET, value = "/" + PATH_APPLICATION)
    public DeferredResult<ApplicationUserDetailsList> getAllApplications() {

        final DeferredResult<ApplicationUserDetailsList> deferedResult = new DeferredResult<>();
        applicationAdminService.getAllApplications()
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Create an Application
     *
     * @return the created Application
     */
    @RequestMapping(method = RequestMethod.POST, value = "/" + PATH_APPLICATION)
    public DeferredResult<HttpEntity<ApplicationUserDetails>> createApplication(@RequestBody final ApplicationUserDetails application,
                                                                                final UriComponentsBuilder builder) {
        final DeferredResult<HttpEntity<ApplicationUserDetails>> deferedResult = new DeferredResult<>();
        applicationAdminService.createApplication(application)
                               .subscribe(m ->
                                          { final HttpHeaders headers = new HttpHeaders();
                                              headers.setLocation(builder.pathSegment(PATH_APPLICATIONS,
                                                                                      PATH_APPLICATIONS,
                                                                                      "{applicationId}")
                                                                         .buildAndExpand(m.getApplicationId()).toUri());
                                              deferedResult.setResult(new ResponseEntity<>(m, headers, HttpStatus.CREATED));
                                          }, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Get Application by Id
     *
     * @return a single Application
     */
    @RequestMapping(method = RequestMethod.GET, value = "/" + PATH_APPLICATION + "/{applicationId}")
    public DeferredResult<ApplicationUserDetails> getApplicationById(
            @PathVariable("applicationId") final UUID applicationId) {
        final DeferredResult<ApplicationUserDetails> deferedResult = new DeferredResult<>();
        applicationAdminService.getApplicationById(applicationId)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Delete Application by Id
     *
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/" + PATH_APPLICATION + "/{applicationId}")
    public DeferredResult deleteApplication(@PathVariable("applicationId") final UUID applicationId) {
        final DeferredResult<Void> deferedResult = new DeferredResult<>();
        applicationAdminService.deleteApplication(applicationId)
                               .subscribe(deferedResult::setResult,
                                          deferedResult::setErrorResult);

        return deferedResult;
    }


    /**
     * Update an Application
     *
     * @return the Application object post update
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/" + PATH_APPLICATION + "/{applicationId}")
    public DeferredResult<ApplicationUserDetails> updateApplication(@PathVariable("applicationId") final UUID applicationId,
                                                                    @RequestBody final ApplicationUserDetails application) {
        final DeferredResult<ApplicationUserDetails> deferedResult = new DeferredResult<>();
        applicationAdminService.updateApplication(applicationId, application)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Add a AllowedService to an Application
     *
     * @return the AllowedService object post add
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_SERVICES + "/{serviceTypeId}")
    public DeferredResult<ApplicationAllowedService> addService(@PathVariable("applicationId") final UUID applicationId,
                                                                @PathVariable(value = "serviceTypeId") final String serviceTypeId) {
        final DeferredResult<ApplicationAllowedService> deferedResult = new DeferredResult<>();
        applicationAdminService.addService(applicationId, serviceTypeId)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Delete a AllowedService from an Application
     *
     * @return the AllowedService object post delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_SERVICES + "/{serviceTypeId}")
    public DeferredResult<Void> deleteService(@PathVariable("applicationId") final UUID applicationId,
                                                        @PathVariable("serviceTypeId") final String serviceTypeId) {

        final DeferredResult<Void> deferedResult = new DeferredResult<>();
        applicationAdminService.deleteService(applicationId, serviceTypeId)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Add a Role to an Application
     *
     * @return the Role object post add
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_ROLES + "/{roleId}")
    public DeferredResult<Role> addRole(@PathVariable("applicationId") final UUID applicationId,
                                        @PathVariable("roleId") final String roleId) {
        final DeferredResult<Role> deferedResult = new DeferredResult<>();
        applicationAdminService.addRole(applicationId, roleId)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Delete a Role from an Application
     *
     * @return the Role object post delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_ROLES + "/{roleId}")
    public DeferredResult<Void> deleteRole(@PathVariable("applicationId") final UUID applicationId,
                                           @PathVariable("roleId") final String roleId) {
        final DeferredResult<Void> deferedResult = new DeferredResult<>();
        applicationAdminService.deleteRole(applicationId, roleId)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Get all AllowedService Types
     *
     * @return all the service types
     */
    @RequestMapping(method = RequestMethod.GET, value = "/" + PATH_SERVICE_TYPES)
    public DeferredResult<ServiceTypeList> getAllServiceTypes() {

        final DeferredResult<ServiceTypeList> deferedResult = new DeferredResult<>();

        applicationAdminService.getAllServiceTypes()
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Get all Roles
     *
     * @return all the roles
     */
    @RequestMapping(method = RequestMethod.GET, value = "/" + PATH_ROLES)
    public DeferredResult<RoleList> getAllRoles() {
        final DeferredResult<RoleList> deferedResult = new DeferredResult<>();
        applicationAdminService.getAllRoles()
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }

    /**
     * Add an Application Policy to an Application
     *
     * @return the Policy object post add
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_POLICIES + "/{policyTypeId}")
    public DeferredResult<ApplicationPolicy> addApplicationPolicy(@PathVariable("applicationId") final UUID applicationId,
                                                                  @PathVariable("policyTypeId") final String policyTypeId,
                                                                  @RequestBody final ApplicationPolicy policy) {
        final DeferredResult<ApplicationPolicy> deferedResult = new DeferredResult<>();
        applicationAdminService.addApplicationPolicy(applicationId, policyTypeId, policy)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Delete an Application Policy from an Application
     *
     * @return the Policy object post delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_POLICIES + "/{policyTypeId}")
    public DeferredResult deleteApplicationPolicy(@PathVariable("applicationId") final UUID applicationId,
                                                  @PathVariable("policyTypeId") final String policyTypeId) {
        final DeferredResult<Void> deferedResult = new DeferredResult<>();
        applicationAdminService.deleteApplicationPolicy(applicationId, policyTypeId)
                               .subscribe(deferedResult::setResult,
                                          deferedResult::setErrorResult);

        return deferedResult;
    }


    /**
     * Add a AllowedService Policy to an Application
     *
     * @return the Policy object post add
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_SERVICES + "/{serviceTypeId}/" + PATH_POLICIES + "/{policyTypeId}")
    public DeferredResult<ApplicationPolicy> addServicePolicy(@PathVariable("applicationId") final UUID applicationId,
                                                              @PathVariable("serviceTypeId") final String serviceTypeId,
                                                              @PathVariable("policyTypeId") final String policyTypeId,
                                                              @RequestBody final ApplicationPolicy policy) {
        final DeferredResult<ApplicationPolicy> deferedResult = new DeferredResult<>();
        applicationAdminService.addServicePolicy(applicationId, serviceTypeId, policyTypeId, policy)
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Delete an AllowedService Policy from an Application
     *
     * @return the Policy object post delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/" + PATH_APPLICATION + "/{applicationId}/" + PATH_SERVICES + "/{serviceTypeId}/" + PATH_POLICIES + "/{policyTypeId}")
    public DeferredResult<Void> deleteServicePolicy(@PathVariable("applicationId") final UUID applicationId,
                                                    @PathVariable("serviceTypeId") final String serviceTypeId,
                                                    @PathVariable("policyTypeId") final String policyTypeId) {
        final DeferredResult<Void> deferedResult = new DeferredResult<>();
        applicationAdminService.deleteServicePolicy(applicationId, serviceTypeId, policyTypeId)
                               .subscribe(deferedResult::setResult,
                                          deferedResult::setErrorResult);

        return deferedResult;
    }


    /**
     * Get all Protocol Types
     *
     * @return all the protocol types
     */
    @RequestMapping(method = RequestMethod.GET, value = "/" + PATH_PROTOCOL_TYPES)
    public DeferredResult<ProtocolTypeList> getAllProtocolTypes() {
        final DeferredResult<ProtocolTypeList> deferedResult = new DeferredResult<>();
        applicationAdminService.getAllProtocolTypes()
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }


    /**
     * Get all Policy Types
     *
     * @return all the policy types
     */
    @RequestMapping(method = RequestMethod.GET, value = "/" + PATH_POLICY_TYPES)
    public DeferredResult<PolicyTypeList> getAllPolicyTypes() {
        final DeferredResult<PolicyTypeList> deferedResult = new DeferredResult<>();
        applicationAdminService.getAllPolicyTypes()
                               .subscribe(deferedResult::setResult, deferedResult::setErrorResult);
        return deferedResult;
    }

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }
}
