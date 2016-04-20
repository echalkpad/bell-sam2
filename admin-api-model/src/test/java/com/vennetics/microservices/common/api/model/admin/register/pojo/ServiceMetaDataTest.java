package com.vennetics.microservices.common.api.model.admin.register.pojo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceMetaDataTest {

    private static final String POLICYNAME = "policyname";
    private static final String SERVICENAME = "servicename";
    private static final String SERVICEDESCRIPTION = "description";
    private final Set<PolicyMetaData> policies = new HashSet<PolicyMetaData>();

    @Before
    public void setup() {
        final PolicyMetaData policyMetaData = new PolicyMetaData(POLICYNAME, null, null);
        policies.add(policyMetaData);
    }

    @Test
    public void testConstructorSetsFields() {
        final ServiceMetaData adaptorService = new ServiceMetaData(SERVICENAME,
                                                                   SERVICEDESCRIPTION,
                                                                   policies);
        assertEquals(SERVICENAME, adaptorService.getServiceId());
        assertEquals(SERVICEDESCRIPTION, adaptorService.getServiceDescription());
        assertEquals(policies, adaptorService.getPolicies());
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(ServiceMetaData.class).verify();
    }

    @Test
    public void toStringTest() {
        final ServiceMetaData adaptorService = new ServiceMetaData(SERVICENAME,
                                                                   SERVICEDESCRIPTION,
                                                                   policies);

        assertTrue(adaptorService.toString().contains(SERVICEDESCRIPTION));
        assertTrue(adaptorService.toString().contains(POLICYNAME));
        assertTrue(adaptorService.toString().contains(SERVICENAME));
    }

}
