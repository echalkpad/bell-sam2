package com.vennetics.microservices.common.api.model.admin.register;

import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.ServiceMetaData;

import java.util.Set;

public interface IServiceRegistration {

    void registerService(ServiceMetaData serviceToRegister);

    Set<ServiceMetaData> getAllRegisteredServices();

    ServiceMetaData findServiceByServiceId(String serviceId);

    Set<PolicyMetaData> getAllApplicationPolicies();

    PolicyMetaData findApplicationPolicyById(String policy);

    void registerApplicationPolicy(PolicyMetaData applicationPolicyToRegister);
}
