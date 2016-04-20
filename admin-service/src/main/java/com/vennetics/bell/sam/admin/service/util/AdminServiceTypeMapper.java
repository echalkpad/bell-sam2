package com.vennetics.bell.sam.admin.service.util;

import com.google.common.collect.ImmutableSet;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyType;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AdminServiceTypeMapper implements IAdminServiceTypeMapper {

    private static final Set<Policy> EMPTY_POLICIES = ImmutableSet.of();

    private final ITypeMapper typeMapper;

    @Autowired
    public AdminServiceTypeMapper(final ITypeMapper typeMapper) {
        super();
        this.typeMapper = typeMapper;
    }

    @Override
    public Set<ApplicationPolicy> toApplicationPolicies(final Set<Policy> allPolicies) {

        final Set<ApplicationPolicy> result = new HashSet<>();

        allPolicies.stream()
                   .forEach(policy -> result.add(new ApplicationPolicy(policy.getPolicyId(),
                                                                       policy.getValues())));

        return result;
    }

    @Override
    public Set<ApplicationAllowedService> toAllowedServices(final Set<String> allowedServiceIds,
                                                            final Set<Policy> allPolicies) {

        final Set<ApplicationAllowedService> result = new HashSet<>();

        final Map<String, Set<ApplicationPolicy>> servicePolicyMap = mapPoliciesByServiceId(allPolicies);

        allowedServiceIds.stream()
                         .forEach(serviceId -> result.add(new ApplicationAllowedService(serviceId,
                                                                                        servicePolicyMap.get(serviceId))));

        return result;
    }

    @Override
    public Set<Policy> toServicePolicies(final CassandraApplication cassandraApplication) {
        if (cassandraApplication.getPolicies() == null) {
            return EMPTY_POLICIES;
        }

        return typeMapper.toPolicies(cassandraApplication.getPolicies());
    }

    @Override
    public Set<Policy> toApplicationPolicies(final CassandraApplication cassandraApplication) {
        if (cassandraApplication.getGatewayPolicies() == null) {
            return EMPTY_POLICIES;
        }

        return typeMapper.toPolicies(cassandraApplication.getGatewayPolicies());
    }

    @Override
    public ApplicationPolicy toApplicationPolicy(final Policy policy) {
        return new ApplicationPolicy(policy.getPolicyId(), policy.getValues());
    }

    @Override
    public PolicyTypeList toPolicyTypeList(final Set<PolicyMetaData> policyMetaDataSet) {
        ArrayList<PolicyType> policyTypes = new ArrayList<>();
        policyMetaDataSet.stream()
                         .filter(policy -> null != policy)
                         .forEach(policy -> policyTypes.add(new PolicyType(policy.getPolicyId(), policy.getPolicyDescription(), 0, null)));

        return new PolicyTypeList(policyTypes);
    }

    private static Map<String, Set<ApplicationPolicy>> mapPoliciesByServiceId(final Set<Policy> allPolicies) {
        final Map<String, Set<ApplicationPolicy>> servicePolicyMap = new HashMap<>();

        if (allPolicies == null) {
            return servicePolicyMap;
        }
        allPolicies.stream().forEach(policy -> {

            if (servicePolicyMap.get(policy.getServiceTypeId()) == null) {
                servicePolicyMap.put(policy.getServiceTypeId(), new HashSet<>());
            }

            final ApplicationPolicy applicationPolicy = new ApplicationPolicy(policy.getPolicyId(),
                                                                              policy.getValues());
            servicePolicyMap.get(policy.getServiceTypeId()).add(applicationPolicy);
        });

        return servicePolicyMap;
    }
}
