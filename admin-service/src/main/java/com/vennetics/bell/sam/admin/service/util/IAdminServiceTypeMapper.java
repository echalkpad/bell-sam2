package com.vennetics.bell.sam.admin.service.util;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;

import java.util.Set;

/**
 * Type mapper interface translating between the API POJOs used by this service
 * and the data-model POJOs used for persistence.
 */
public interface IAdminServiceTypeMapper {

    /**
     * @param allPolicies
     * @return the mapped set or an empty set.
     */
    Set<ApplicationPolicy> toApplicationPolicies(Set<Policy> allPolicies);

    /**
     * @param allowedServiceIds
     * @param allPolicies
     * @return the mapped set or an empty set.
     */
    Set<ApplicationAllowedService> toAllowedServices(Set<String> allowedServiceIds,
                                                     Set<Policy> allPolicies);

    /**
     * @param cassandraApplication
     * @return the mapped set or an empty set.
     */
    Set<Policy> toServicePolicies(CassandraApplication cassandraApplication);

    /**
     * @param cassandraApplication
     * @return the mapped set or an empty set.
     */
    Set<Policy> toApplicationPolicies(CassandraApplication cassandraApplication);

    ApplicationPolicy toApplicationPolicy(final Policy policy);

    PolicyTypeList toPolicyTypeList(final Set<PolicyMetaData> policyMetaDataSet);
}
