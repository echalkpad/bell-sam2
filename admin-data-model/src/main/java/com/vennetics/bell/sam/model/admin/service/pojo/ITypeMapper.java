package com.vennetics.bell.sam.model.admin.service.pojo;

import java.util.Collection;
import java.util.Set;

/**
 * For mapping of admin data model pojos that are modelled as a string in
 * cassandra rather than as a strictly typed schema.
 */
public interface ITypeMapper {

    Policy toPolicy(String policyAsString);

    String toPolicyAsString(Policy policy);

    Set<Policy> toPolicies(Set<String> policiesAsString);

    Set<String> toPoliciesAsString(Collection<Policy> policies);

}
