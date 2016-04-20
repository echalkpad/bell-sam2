package com.vennetics.microservices.common.api.model.admin.register;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;

import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.ServiceMetaData;

public class ServiceRegistration implements IServiceRegistration {

    private final Set<ServiceMetaData> registeredServices = new HashSet<ServiceMetaData>();

    private final Set<PolicyMetaData> registeredApplicationPolicies = new HashSet<PolicyMetaData>();

    @Bean
    public ServiceRegistration serviceRegistration() {
        return new ServiceRegistration();
    }

    @Override
    public void registerService(ServiceMetaData serviceToRegister) {
        if (serviceToRegister != null) {
            registeredServices.add(serviceToRegister);
        }
    }

    @Override
    public void registerApplicationPolicy(PolicyMetaData applicationPolicyToRegister) {
        if (applicationPolicyToRegister != null) {
            registeredApplicationPolicies.add(applicationPolicyToRegister);
        }
    }

    @Override
    public Set<ServiceMetaData> getAllRegisteredServices() {
        return registeredServices;
    }

    @Override
    public ServiceMetaData findServiceByServiceId(String serviceId) {
        ServiceMetaData found = null;

        for (final ServiceMetaData serviceMetaData : registeredServices) {
            if (serviceId.contentEquals(serviceMetaData.getServiceId())) {
                found = serviceMetaData;
            }
        }
        return found;
    }

    @Override
    public Set<PolicyMetaData> getAllApplicationPolicies() {
        return registeredApplicationPolicies;
    }

    @Override
    public PolicyMetaData findApplicationPolicyById(String policyId) {
        PolicyMetaData found = null;

        for (final PolicyMetaData policyMetaData : registeredApplicationPolicies) {
            if (policyMetaData.getPolicyId().contentEquals(policyId)) {
                found = policyMetaData;
            }
        }

        return found;
    }

}
