package com.vennetics.bell.sam.model.admin.service.pojo;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

@Service
public class TypeMapper implements ITypeMapper {

    private final ObjectMapper objectMapper;

    public TypeMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper#toPolicy(java
     * .lang.String)
     */
    @Override
    public Policy toPolicy(final String policyAsString) {
        try {
            return objectMapper.readValue(policyAsString, Policy.class);
        } catch (final IOException e) {
            // Do we want a specific exception here.
            // As long as this POJO is only used internally it's a platform
            // fail.
            // If we push into API EVER then it could be a bad request.
            throw new PlatformFailedException("Failed to map policy JSON string: " + policyAsString,
                                              e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper#
     * toPolicyAsString(com.vennetics.bell.sam.model.admin.service.pojo.Policy)
     */
    @Override
    public String toPolicyAsString(final Policy policy) {

        try {
            return objectMapper.writeValueAsString(policy);
        } catch (final JsonProcessingException e) {
            throw new PlatformFailedException("Failed to map policy to JSON string: " + policy, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper#toPolicies(
     * java.lang.String)
     */
    @Override
    public Set<Policy> toPolicies(final Set<String> policiesAsString) {

        final Set<Policy> result = new HashSet<>();

        if (CollectionUtils.isEmpty(policiesAsString)) {
            return result;
        }

        policiesAsString.stream().forEach(policyAsString -> result.add(toPolicy(policyAsString)));

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper#
     * toPoliciesAsString(java.util.Collection)
     */
    @Override
    public Set<String> toPoliciesAsString(final Collection<Policy> policies) {

        final Set<String> result = new HashSet<>();

        if (CollectionUtils.isEmpty(policies)) {
            return result;
        }

        policies.stream().forEach(policy -> result.add(toPolicyAsString(policy)));

        return result;

    }
}
