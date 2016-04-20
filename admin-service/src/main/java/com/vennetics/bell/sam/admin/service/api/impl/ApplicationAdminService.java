package com.vennetics.bell.sam.admin.service.api.impl;

import com.datastax.driver.core.utils.UUIDs;
import com.vennetics.bell.sam.admin.service.api.IApplicationAdminService;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceEntityAlreadyExistsException;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceEntityNotFoundException;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceInvalidPolicyForServiceException;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceInvalidPolicyKeysException;
import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceNoTypesException;
import com.vennetics.bell.sam.admin.service.repositories.AllowedServicesRepository;
import com.vennetics.bell.sam.admin.service.repositories.ApplicationRepository;
import com.vennetics.bell.sam.admin.service.repositories.ClientRepository;
import com.vennetics.bell.sam.admin.service.repositories.ProtocolTypeRepository;
import com.vennetics.bell.sam.admin.service.repositories.RoleRepository;
import com.vennetics.bell.sam.admin.service.util.IAdminServiceTypeMapper;
import com.vennetics.bell.sam.model.admin.service.AccountState;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraAllowedService;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraProtocolType;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;
import com.vennetics.microservices.common.api.model.admin.register.IServiceRegistration;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.ServiceMetaData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetailsList;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolType;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.RoleList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceType;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceTypeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of ApplicationAdminService Interface
 */
@Service
public class ApplicationAdminService implements IApplicationAdminService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAdminService.class);
    public static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    public static final String BCRYPT_PREFIX = "$2a$10$";
    public static final String POLICY = "Policy";
    public static final String SERVICE = "Service";

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AllowedServicesRepository allowedServicesRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProtocolTypeRepository protocolTypeRepository;
    @Autowired
    private IAdminServiceTypeMapper adminServiceTypeMapper;
    @Autowired
    private ITypeMapper typeMapper;
    @Autowired
    private IServiceRegistration serviceRegistration;

    @Override
    public Observable<ApplicationUserDetailsList> getAllApplications() {
        ArrayList<ApplicationUserDetails> applications = new ArrayList<>();
        try (Stream<CassandraApplication> stream = StreamSupport.stream(applicationRepository.findAll().spliterator(), false)) {
            stream.forEach(cassApp -> {
              final ApplicationUserDetails application = new ApplicationUserDetails(cassApp.getClientId(),
                                                              cassApp.getName(),
                                                              cassApp.getEmail(),
                                                              cassApp.getAccountState(),
                                                              cassApp.getApplicationId());
              applications.add(application);
            });
        }

        return Observable.just(new ApplicationUserDetailsList(applications));
    }

    @Override
    public Observable<ApplicationUserDetails> createApplication(final ApplicationUserDetails applicationToCreate) {

        verifyClientIdIsNotInUse(applicationToCreate.getClientId());

        final UUID applicationId = UUIDs.timeBased();
        // Create client
        final CassandraClient newCassandraClient = new CassandraClient(applicationToCreate.getClientId(),
                                                                       applicationId,
                                                                       getCurrentZonedDateTime(),
                                                                       getCurrentZonedDateTime());
        clientRepository.save(newCassandraClient);

        logger.debug("CassandraClient created for ClientId {}.", applicationToCreate.getClientId());

        final CassandraApplication newCassandraApplication = new CassandraApplication(applicationId,
                                                                                      applicationToCreate.getClientId(),
                                                                                      applicationToCreate.getName(),
                                                                                      applicationToCreate.getEmail(),
                                                                                      encodeSecret(applicationToCreate.getSecret()),
                                                                                      AccountState.valueOf(applicationToCreate.getState().toUpperCase()).toString(),
                                                                                      getCurrentZonedDateTime(),
                                                                                      getCurrentZonedDateTime());

        applicationRepository.save(newCassandraApplication);
        logger.debug("CassandraApplication {} created for ClientId {}.", applicationId, applicationToCreate.getClientId());

        final ApplicationUserDetails application = new ApplicationUserDetails(newCassandraApplication.getClientId(),
                                                        newCassandraApplication.getName(),
                                                        newCassandraApplication.getEmail(),
                                                        newCassandraApplication.getAccountState(),
                                                        newCassandraApplication.getApplicationId());
        return Observable.just(application);
    }


    @Override
    public Observable<ApplicationUserDetails> getApplicationById(final UUID applicationId) {
        final CassandraApplication cassandraApplication = applicationRepository.findOne(applicationId);
        if (null == cassandraApplication) {
            throw new AdminServiceEntityNotFoundException(applicationId.toString());
        }

        final ApplicationUserDetails application = new ApplicationUserDetails(cassandraApplication.getClientId(),
                                                                              cassandraApplication.getName(),
                                                                              cassandraApplication.getEmail(),
                                                                              cassandraApplication.getAccountState(),
                                                                              cassandraApplication.getApplicationId(),
                                                                              cassandraApplication.getRoleIds(),
                                                                              adminServiceTypeMapper.toApplicationPolicies(
                                                                                  adminServiceTypeMapper.toApplicationPolicies(
                                                                                  cassandraApplication)),
                                                                              adminServiceTypeMapper.toAllowedServices(
                                                                                  cassandraApplication.getAllowedServiceIds(),
                                                                                  adminServiceTypeMapper.toServicePolicies(cassandraApplication)));
        return Observable.just(application);
    }


    @Override
    public Observable<Void> deleteApplication(final UUID applicationId) {

        final CassandraApplication cassandraApplicationFound = applicationRepository.findOne(applicationId);
        if (null == cassandraApplicationFound) {
            throw new AdminServiceEntityNotFoundException(applicationId.toString());
        }

        // Delete CassandraClient
        clientRepository.delete(cassandraApplicationFound.getClientId());
        logger.debug("CassandraClient {} deleted.", cassandraApplicationFound.getClientId());

        // Remove AllowedServices applicationIds references
        if (null != cassandraApplicationFound.getAllowedServiceIds()) {
            for (String id : cassandraApplicationFound.getAllowedServiceIds()) {
                CassandraAllowedService cassandraAllowedService = allowedServicesRepository.findOne(id);
                removeAppIdFromAllowedService(applicationId, cassandraAllowedService);
            }
        }

        // Remove CassandraRole applicationIds references
        if (null != cassandraApplicationFound.getRoleIds()) {
            for (String id : cassandraApplicationFound.getRoleIds()) {
                CassandraRole cassandraRole = roleRepository.findOne(id);
                removeAppIdFromRole(applicationId, cassandraRole);
            }
        }
        applicationRepository.delete(applicationId);
        logger.debug("CassandraApplication {} deleted.", applicationId);
        return Observable.just(null);
    }


    @Override
    public Observable<ApplicationUserDetails> updateApplication(final UUID applicationId, final ApplicationUserDetails updatedApplication) {

        CassandraApplication foundCassandraApplication = findCassandraApplication(applicationId);

        // use the found applications id to find client
        final CassandraClient foundCassandraClient = clientRepository.findOne(foundCassandraApplication.getClientId());
        if (null == foundCassandraClient) {
            throw new AdminServiceEntityNotFoundException(foundCassandraApplication.getClientId());
        }

        // Update ClientId if necessary
        if (!foundCassandraClient.getClientId().equals(updatedApplication.getClientId())) {
            verifyClientIdIsNotInUse(updatedApplication.getClientId());
            foundCassandraClient.setClientId(updatedApplication.getClientId());
            foundCassandraClient.setLastModifiedTimestamp(getCurrentZonedDateTime());
            foundCassandraApplication.setClientId(updatedApplication.getClientId());
            clientRepository.save(foundCassandraClient);
        }

        foundCassandraApplication.setName(updatedApplication.getName());
        foundCassandraApplication.setEmail(updatedApplication.getEmail());
        foundCassandraApplication.setSecret(encodeSecret(updatedApplication.getSecret()));
        foundCassandraApplication.setAccountState(AccountState.valueOf(updatedApplication.getState().toUpperCase()).toString());
        foundCassandraApplication.setLastModifiedTimestamp(getCurrentZonedDateTime());
        applicationRepository.save(foundCassandraApplication);
        logger.debug("CassandraApplication {} updated.", applicationId);

        final ApplicationUserDetails application = new ApplicationUserDetails(foundCassandraApplication.getClientId(),
                                                                              foundCassandraApplication.getName(),
                                                                              foundCassandraApplication.getEmail(),
                                                                              foundCassandraApplication.getAccountState(),
                                                                              foundCassandraApplication.getApplicationId(),
                                                                              foundCassandraApplication.getRoleIds(),
                                                                              adminServiceTypeMapper.toApplicationPolicies(
                                                                                  adminServiceTypeMapper.toApplicationPolicies(foundCassandraApplication)),
                                                                              adminServiceTypeMapper.toAllowedServices(
                                                                                  foundCassandraApplication.getAllowedServiceIds(),
                                                                                  adminServiceTypeMapper.toServicePolicies(foundCassandraApplication)));
        return Observable.just(application);
    }

    @Override
    public Observable<ApplicationAllowedService> addService(final UUID applicationId, final String serviceId) {

        final CassandraApplication foundCassandraApplication = applicationRepository.findOne(applicationId);
        CassandraAllowedService cassandraAllowedService = allowedServicesRepository.findOne(serviceId);

        if (null == cassandraAllowedService) {
            throw new AdminServiceEntityNotFoundException(serviceId);
        }

        // get all the applications currently allowed services
        Set<String> appAllowedServiceIds = new HashSet<>();
        if (null != foundCassandraApplication.getAllowedServiceIds()) {
            appAllowedServiceIds.addAll(foundCassandraApplication.getAllowedServiceIds());
        }
        // add the new service to the allowedServices Set on the application
        appAllowedServiceIds.add(serviceId);
        foundCassandraApplication.setAllowedServiceIds(appAllowedServiceIds);

        // get all the current applicationIds associated with this service
        Set<UUID> allowedServiceAppIds = new HashSet<>();
        if (null != cassandraAllowedService.getApplicationIds()) {
            allowedServiceAppIds.addAll(cassandraAllowedService.getApplicationIds());
        }
        // add the applicationId to the services applicationIds Set
        allowedServiceAppIds.add(applicationId);
        cassandraAllowedService.setApplicationIds(allowedServiceAppIds);

        foundCassandraApplication.setLastModifiedTimestamp(getCurrentZonedDateTime());
        cassandraAllowedService.setLastModifiedTimestamp(getCurrentZonedDateTime());

        applicationRepository.save(foundCassandraApplication);
        allowedServicesRepository.save(cassandraAllowedService);
        logger.debug("AllowedService {} added to CassandraApplication {}.", serviceId, applicationId);

        return Observable.just(new ApplicationAllowedService(
            cassandraAllowedService.getAllowedServiceId(), null));
    }

    @Override
    public Observable<Void>  deleteService(final UUID applicationId, final String serviceId) {

        final CassandraApplication foundCassandraApplication = applicationRepository.findOne(applicationId);
        CassandraAllowedService cassandraAllowedService = allowedServicesRepository.findOne(serviceId);

        if (null == cassandraAllowedService) {
            throw new AdminServiceEntityNotFoundException(serviceId);
        }

        Set<String> appAllowedServiceIds = new HashSet<>();
        if (null != foundCassandraApplication.getAllowedServiceIds()) {
            appAllowedServiceIds.addAll(foundCassandraApplication.getAllowedServiceIds());
        }
        appAllowedServiceIds.remove(serviceId);
        foundCassandraApplication.setAllowedServiceIds(appAllowedServiceIds);

        Set<UUID> allowedServiceAppIds = new HashSet<>();
        if (null != cassandraAllowedService.getApplicationIds()) {
            allowedServiceAppIds.addAll(cassandraAllowedService.getApplicationIds());
        }
        allowedServiceAppIds.remove(applicationId);
        cassandraAllowedService.setApplicationIds(allowedServiceAppIds);

        foundCassandraApplication.setLastModifiedTimestamp(getCurrentZonedDateTime());
        cassandraAllowedService.setLastModifiedTimestamp(getCurrentZonedDateTime());

        applicationRepository.save(foundCassandraApplication);
        allowedServicesRepository.save(cassandraAllowedService);
        logger.debug("AllowedService {} deleted from CassandraApplication {}.", serviceId, applicationId);
        return Observable.just(null);
    }

    @Override
    public Observable<Role> addRole(final UUID applicationId, final String roleId) {

        CassandraApplication foundCassandraApplication = applicationRepository.findOne(applicationId);
        CassandraRole cassandraRole = roleRepository.findOne(roleId);

        if (null == foundCassandraApplication || null == cassandraRole) {
            throw new AdminServiceEntityNotFoundException(applicationId.toString());
        }

        Set<String> appRoleIds = new HashSet<>();
        if (null != foundCassandraApplication.getRoleIds()) {
            appRoleIds.addAll(foundCassandraApplication.getRoleIds());
        }
        appRoleIds.add(roleId);
        foundCassandraApplication.setRoleIds(appRoleIds);

        Set<UUID> roleAppIds = new HashSet<>();
        if (null != cassandraRole.getApplicationIds()) {
            roleAppIds.addAll(cassandraRole.getApplicationIds());
        }
        roleAppIds.add(applicationId);
        cassandraRole.setApplicationIds(roleAppIds);

        foundCassandraApplication.setLastModifiedTimestamp(getCurrentZonedDateTime());

        applicationRepository.save(foundCassandraApplication);
        roleRepository.save(cassandraRole);
        logger.debug("Role {} added to Application {}.", roleId, applicationId);

        Role role = new Role(cassandraRole.getRoleId(),
                                   cassandraRole.getDescription(),
                                   cassandraRole.getServiceTypeIds(),
                                   cassandraRole.getApplicationIds());

        return Observable.just(role);
    }

    @Override
    public Observable<Void> deleteRole(final UUID applicationId, final String roleId) {

        CassandraApplication foundCassandraApplication = applicationRepository.findOne(applicationId);
        CassandraRole cassandraRole = roleRepository.findOne(roleId);

        if (null == foundCassandraApplication || null == cassandraRole) {
            throw new AdminServiceEntityNotFoundException(applicationId.toString());
        }

        Set<String> appRoleIds = new HashSet<>();
        if (null != foundCassandraApplication.getRoleIds()) {
            appRoleIds.addAll(foundCassandraApplication.getRoleIds());
        }
        appRoleIds.remove(roleId);
        foundCassandraApplication.setRoleIds(appRoleIds);

        Set<UUID> roleAppIds = new HashSet<>();
        if (null != cassandraRole.getApplicationIds()) {
            roleAppIds.addAll(cassandraRole.getApplicationIds());
        }
        roleAppIds.remove(applicationId);
        cassandraRole.setApplicationIds(roleAppIds);

        foundCassandraApplication.setLastModifiedTimestamp(getCurrentZonedDateTime());

        applicationRepository.save(foundCassandraApplication);
        roleRepository.save(cassandraRole);
        logger.debug("Role {} deleted from Application {}.", roleId, applicationId);

        return Observable.just(null);
    }


    @Override
    public Observable<ServiceTypeList> getAllServiceTypes() {

        ArrayList<ServiceType> serviceTypes = new ArrayList<>();
        Set<ServiceMetaData> registeredServices = serviceRegistration.getAllRegisteredServices();
        if (null == registeredServices) {
            throw new AdminServiceNoTypesException(SERVICE);
        }

        registeredServices.stream()
                    .filter(serviceMetaData -> null != serviceMetaData)
                    .forEach(serviceMetaData -> {
                        Set<String> policyTypeIds = new HashSet<>();

                        serviceMetaData.getPolicies().stream()
                                       .filter(policyMetaData -> null != policyMetaData)
                                       .forEach(policyMetaData -> policyTypeIds.add(policyMetaData.getPolicyId()));

                        ServiceType serviceType = new ServiceType(serviceMetaData.getServiceId(),
                                                                  serviceMetaData.getServiceDescription(),
                                                                  policyTypeIds);
                        serviceTypes.add(serviceType);
                    });

        return Observable.just(new ServiceTypeList(serviceTypes));
    }

    @Override
    public Observable<RoleList> getAllRoles() {
        ArrayList<Role> roles = new ArrayList<>();
        try (Stream<CassandraRole> stream = StreamSupport.stream(roleRepository.findAll().spliterator(), false)) {
            stream.forEach(cassRole -> {
                Role role = new Role(cassRole.getRoleId(),
                                     cassRole.getDescription(),
                                     cassRole.getServiceTypeIds(),
                                     cassRole.getApplicationIds());
                roles.add(role);
            });
        }
        return Observable.just(new RoleList(roles));
    }

    @Override
    public Observable<ApplicationPolicy> addApplicationPolicy(
        final UUID applicationId, final String policyTypeId, final ApplicationPolicy policyToAdd) {

        CassandraApplication foundCassandraApplication = findCassandraApplication(applicationId);

        // verify policy type exists
        PolicyMetaData policyMetaData = serviceRegistration.findApplicationPolicyById(policyTypeId);
        if (null == policyMetaData) {
            throw new AdminServiceEntityNotFoundException(policyTypeId);
        }

        verifyKeys(policyToAdd.getValues().keySet(), policyMetaData.getPolicyMandatoryKeys());

        // get all current policies
        Set<Policy> policies = Optional.ofNullable(typeMapper.toPolicies(foundCassandraApplication.getGatewayPolicies())).orElse(new HashSet<>());
        policies.removeIf(policy1 -> policy1.getPolicyId().equals(policyTypeId));
        // add policyToAdd
        Policy newPolicy = new Policy(policyTypeId, policyToAdd.getValues(), null);
        policies.add(newPolicy);

        // convert policies to json strings
        foundCassandraApplication.setGatewayPolicies(typeMapper.toPoliciesAsString(policies));
        // save application
        applicationRepository.save(foundCassandraApplication);

        return Observable.just(adminServiceTypeMapper.toApplicationPolicy(newPolicy));
    }


    @Override
    public Observable<Void> deleteApplicationPolicy(final UUID applicationId, final String policyTypeId) {

        CassandraApplication foundCassandraApplication = findCassandraApplication(applicationId);

        // verify that application has policies
        Set<String> applicationPoliciesStrings = foundCassandraApplication.getGatewayPolicies();
        if (null == applicationPoliciesStrings || applicationPoliciesStrings.isEmpty()) {
            throw new AdminServiceNoTypesException(POLICY);
        }

        Set<Policy> applicationPolicies = typeMapper.toPolicies(applicationPoliciesStrings);

        // find policy on application with policyTypeId
        removePolicyFromSetIfFound(policyTypeId, applicationPolicies);

        foundCassandraApplication.setGatewayPolicies(typeMapper.toPoliciesAsString(applicationPolicies));
        applicationRepository.save(foundCassandraApplication);
        logger.debug("Policy {} deleted from Application {}.", policyTypeId, applicationId);
        return Observable.just(null);
    }

    @Override
    public Observable<ApplicationPolicy> addServicePolicy(final UUID applicationId,
                                                          final String serviceTypeId,
                                                          final String policyTypeId,
                                                          final ApplicationPolicy policyToAdd) {

        CassandraApplication foundCassandraApplication = findCassandraApplication(applicationId);

        // find serviceMetaData with serviceTypeId if it exists
        ServiceMetaData serviceMetaData = findServiceMetaData(serviceTypeId);

        // verify policy type exists
        PolicyMetaData policyMetaData = serviceRegistration.findApplicationPolicyById(policyTypeId);
        if (null == policyMetaData) {
            throw new AdminServiceEntityNotFoundException(policyTypeId);
        }

        // verify valid policyType for service
        Set<String> serviceLvlPolicyIds = new HashSet<>();
        serviceMetaData.getPolicies()
                       .stream()
                       .filter(serviceLvlPolicy -> null != serviceLvlPolicy)
                       .forEach(serviceLvlPolicy -> serviceLvlPolicyIds.add(serviceLvlPolicy.getPolicyId()));

        if (!serviceLvlPolicyIds.contains(policyTypeId)) {
            throw new AdminServiceInvalidPolicyForServiceException(policyTypeId, serviceTypeId);
        }

        verifyKeys(policyToAdd.getValues().keySet(), policyMetaData.getPolicyMandatoryKeys());

        // get all current policies
        Set<Policy> policies = Optional.ofNullable(typeMapper.toPolicies(foundCassandraApplication.getPolicies())).orElse(new HashSet<>());
        policies.removeIf(policy1 -> policy1.getPolicyId().equals(policyTypeId));

        // add policyToAdd
        Policy newPolicy = new Policy(policyTypeId, policyToAdd.getValues(), null);
        policies.add(newPolicy);

        // convert policies to json strings
        foundCassandraApplication.setPolicies(typeMapper.toPoliciesAsString(policies));
        // save application
        applicationRepository.save(foundCassandraApplication);

        return Observable.just(adminServiceTypeMapper.toApplicationPolicy(newPolicy));
    }

    @Override
    public Observable<Void> deleteServicePolicy(final UUID applicationId, final String serviceTypeId, final String policyTypeId) {

        // find and verify application
        CassandraApplication foundCassandraApplication = findCassandraApplication(applicationId);

        // verify application has service with serviceTypeId
        Set<String> cassandraAllowedServices = foundCassandraApplication.getAllowedServiceIds();
        if (null == cassandraAllowedServices || !cassandraAllowedServices.contains(serviceTypeId)) {
            throw new AdminServiceEntityNotFoundException(applicationId.toString());
        }

        // verify application has any service policies
        Set<Policy> servicePolicies = typeMapper.toPolicies(foundCassandraApplication.getPolicies());
        if (null == servicePolicies || servicePolicies.isEmpty()) {
            throw new AdminServiceNoTypesException(POLICY);
        }
        removePolicyFromSetIfFound(policyTypeId, servicePolicies);

        foundCassandraApplication.setPolicies(typeMapper.toPoliciesAsString(servicePolicies));
        applicationRepository.save(foundCassandraApplication);
        logger.debug("Policy {} deleted from Service {} and Application {}.", policyTypeId, serviceTypeId, applicationId);
        return Observable.just(null);
    }

    @Override
    public Observable<ProtocolTypeList> getAllProtocolTypes() {
        ArrayList<ProtocolType> protocolTypes = new ArrayList<>();
        try (Stream<CassandraProtocolType> stream = StreamSupport.stream(protocolTypeRepository.findAll().spliterator(), false)) {
            stream.forEach(cassProtocolType -> {
                ProtocolType protocolType = new ProtocolType(cassProtocolType.getProtocolTypeId(),
                                                             cassProtocolType.getDescription(),
                                                             cassProtocolType.getServiceTypeIds());
                protocolTypes.add(protocolType);
            });
        }
        return Observable.just(new ProtocolTypeList(protocolTypes));
    }

    @Override
    public Observable<PolicyTypeList> getAllPolicyTypes() {

        // get all the application policies
        Set<PolicyMetaData> allPolicies = Optional.ofNullable(serviceRegistration.getAllApplicationPolicies()).orElse(new HashSet<>());

        Set<ServiceMetaData> registeredServices = serviceRegistration.getAllRegisteredServices();
        if (null != registeredServices) {
            //get all the service level policies
            Set<PolicyMetaData> serviceLvlPolicies = new HashSet<>();
            registeredServices.stream()
                              .filter(serviceMetaData -> null != serviceMetaData)
                              .forEach(serviceMetaData -> serviceMetaData.getPolicies()
                                                                         .stream()
                                                                         .filter(serviceMetaDataPolicies -> null != serviceMetaDataPolicies)
                                                                         .forEach(serviceLvlPolicies::add));
            allPolicies.addAll(serviceLvlPolicies);
        }

        if (allPolicies.isEmpty()) {
            throw new AdminServiceNoTypesException(POLICY);
        }

        return Observable.just(adminServiceTypeMapper.toPolicyTypeList(allPolicies));
    }

    /**
     * Throws an error if one set of policy keys does not match the other
     *
     * @param policyToAddKeySet the keys from the policy to add
     * @param validKeys the valid keys from meta data
     */
    private void verifyKeys(final Set<String> policyToAddKeySet, final Set<String> validKeys) {
        if (policyToAddKeySet.equals(validKeys)) {
            throw new AdminServiceInvalidPolicyKeysException(policyToAddKeySet.toString());
        }
    }

    private void removePolicyFromSetIfFound(final String policyTypeId, final Set<Policy> servicePolicies) {
        // find and remove policy  with policyTypeId
        boolean wasPolicyFound = false;
        for (Policy policy : servicePolicies) {
            if (policy.getPolicyId().equalsIgnoreCase(policyTypeId)) {
                wasPolicyFound = true;
                servicePolicies.remove(policy);
            }
        }

        if (!wasPolicyFound) {
            throw new AdminServiceNoTypesException(POLICY);
        }
    }

    private ServiceMetaData findServiceMetaData(final String serviceTypeId) {
        // verify service type exists
        ServiceMetaData serviceMetaData = serviceRegistration.findServiceByServiceId(serviceTypeId);
        if (null == serviceMetaData) {
            throw new AdminServiceEntityNotFoundException(serviceTypeId);
        }
        return serviceMetaData;
    }

    private CassandraApplication findCassandraApplication(final UUID applicationId) {
        CassandraApplication foundCassandraApplication = applicationRepository.findOne(applicationId);
        if (null == foundCassandraApplication) {
            throw new AdminServiceEntityNotFoundException(applicationId.toString());
        }
        return foundCassandraApplication;
    }

    private void removeAppIdFromRole(final UUID applicationId, final CassandraRole cassandraRole) {
        if (null != cassandraRole && null != cassandraRole.getApplicationIds()) {
            Set<UUID> roleApplicationIds = cassandraRole.getApplicationIds();
            roleApplicationIds.stream().filter(appId -> applicationId == appId).forEach(roleApplicationIds::remove);
            cassandraRole.setApplicationIds(roleApplicationIds);
            roleRepository.save(cassandraRole);
        }
    }

    private void removeAppIdFromAllowedService(final UUID applicationId, final CassandraAllowedService cassandraAllowedService) {
        if (null != cassandraAllowedService && null != cassandraAllowedService.getApplicationIds()) {
            Set<UUID> servicesApplicationIds = cassandraAllowedService.getApplicationIds();
            servicesApplicationIds.stream().filter(appId -> applicationId == appId).forEach(servicesApplicationIds::remove);
            cassandraAllowedService.setApplicationIds(servicesApplicationIds);
            cassandraAllowedService.setLastModifiedTimestamp(getCurrentZonedDateTime());
            allowedServicesRepository.save(cassandraAllowedService);
        }
    }

    /**
     * Returns the current ZonedDateTime as Date
     *
     * @return the current zoned date time
     */
    private static Date getCurrentZonedDateTime() {
        return Date.from(java.time.ZonedDateTime.now().toInstant());
    }

    /**
     * Verify a Client with the Id provided doesn't already exist.
     *
     * @param clientId
     *              the clientId to verify
     */
    private void verifyClientIdIsNotInUse(final String clientId) {
        if (clientRepository.exists(clientId)) {
            throw new AdminServiceEntityAlreadyExistsException(clientId);
        }
    }

    /**
     * Encode's a string using BCrypt if it is not already encoded.
     *
     * @param secret the string to potentially encode
     * @return the BCrypt encoded secret
     */
    private static String encodeSecret(final String secret) {
        String newSecret = secret;
        if (!secret.startsWith(BCRYPT_PREFIX)) {
            newSecret = PASSWORD_ENCODER.encode(secret);
        }
        return newSecret;
    }
}
