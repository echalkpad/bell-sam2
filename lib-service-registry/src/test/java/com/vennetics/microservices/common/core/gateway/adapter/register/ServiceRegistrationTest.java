package com.vennetics.microservices.common.core.gateway.adapter.register;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.vennetics.microservices.common.api.model.admin.register.ServiceRegistration;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyKeyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.ServiceMetaData;

public class ServiceRegistrationTest {

    private ServiceRegistration serviceRegistration;

    private static final String SERVICEID = "serviceId1";
    private static final String SERVICEID2 = "serviceId2";
    private static final String SERVICEID3 = "serviceId3";
    private static final String SERVICE_DESCRIPTION = "serviceDescription";
    private static final String SERVICE_DESCRIPTION2 = "serviceDescription2";
    private static final String SERVICE_DESCRIPTION3 = "serviceDescription3";

    private static final String POLICY1 = "policy1";
    private static final String POLICY2 = "policy2";
    private static final String POLICY3 = "policy3";

    private static final String POLICY_DESCRIPION = "policyDescription";
    private static final String POLICY_DESCRIPION2 = "policyDescription2";
    private static final String POLICY_DESCRIPION3 = "policyDescription3";

    private static final String POLICY_KEY = "policyKey";
    private static final String POLICY_KEY2 = "policyKey2";
    private static final String POLICY_KEY3 = "policyKey3";

    private static final String POLICY_KEY_DESCRIPTION = "policyDescription";
    private static final String POLICY_KEY_DESCRIPTION2 = "policyDescription2";
    private static final String POLICY_KEY_DESCRIPTION3 = "policyDescription3";

    private static final PolicyKeyMetaData POLICY_KEY_META_DATA = new PolicyKeyMetaData(POLICY_KEY,
                                                                                        POLICY_KEY_DESCRIPTION);

    private static final PolicyKeyMetaData POLICY_KEY_META_DATA_2 = new PolicyKeyMetaData(POLICY_KEY2,
                                                                                          POLICY_KEY_DESCRIPTION2);
    private static final PolicyKeyMetaData POLICY_KEY_META_DATA_3 = new PolicyKeyMetaData(POLICY_KEY3,
                                                                                          POLICY_KEY_DESCRIPTION3);

    private static final Set<PolicyKeyMetaData> POLICY_KEYS = ImmutableSet.of(POLICY_KEY_META_DATA,
                                                                             POLICY_KEY_META_DATA_2,
                                                                             POLICY_KEY_META_DATA_3);

    private static final PolicyMetaData POLICY_META_DATA = new PolicyMetaData(POLICY1,
                                                                              POLICY_DESCRIPION,
                                                                              POLICY_KEYS);
    private static final PolicyMetaData POLICY_META_DATA_2 = new PolicyMetaData(POLICY2,
                                                                                POLICY_DESCRIPION2,
                                                                                POLICY_KEYS);
    private static final PolicyMetaData POLICY_META_DATA_3 = new PolicyMetaData(POLICY3,
                                                                                POLICY_DESCRIPION3,
                                                                                POLICY_KEYS);

    private static final Set<PolicyMetaData> POLICY_META_DATAS = ImmutableSet.of(POLICY_META_DATA);
    private static final Set<PolicyMetaData> POLICY_META_DATAS_2 = ImmutableSet.of(POLICY_META_DATA_2);
    private static final Set<PolicyMetaData> POLICY_META_DATAS_3 = ImmutableSet.of(POLICY_META_DATA_3);

    private static final ServiceMetaData SERVICE_META_DATA = new ServiceMetaData(SERVICEID,
                                                                                 SERVICE_DESCRIPTION,
                                                                                 POLICY_META_DATAS);
    private static final ServiceMetaData SERVICE_META_DATA_2 = new ServiceMetaData(SERVICEID2,
                                                                                   SERVICE_DESCRIPTION2,
                                                                                   POLICY_META_DATAS_2);
    private static final ServiceMetaData SERVICE_META_DATA_3 = new ServiceMetaData(SERVICEID3,
                                                                                   SERVICE_DESCRIPTION3,
                                                                                   POLICY_META_DATAS_3);

    @Before
    public void setup() {
        serviceRegistration = new ServiceRegistration();
    }

    @Test
    public void checkRegisteredServicesEmptyOnInitialisation() {
        assertTrue(serviceRegistration.getAllRegisteredServices().isEmpty());
    }

    @Test
    public void checkOneServiceRegistered() {
        serviceRegistration.registerService(SERVICE_META_DATA);

        assertTrue(serviceRegistration.getAllRegisteredServices().contains(SERVICE_META_DATA));
    }

    @Test
    public void checkThreeServicesAreRegistered() {
        serviceRegistration.registerService(SERVICE_META_DATA);
        serviceRegistration.registerService(SERVICE_META_DATA_2);
        serviceRegistration.registerService(SERVICE_META_DATA_3);

        assertTrue(serviceRegistration.getAllRegisteredServices().contains(SERVICE_META_DATA));
        assertTrue(serviceRegistration.getAllRegisteredServices().contains(SERVICE_META_DATA_2));
        assertTrue(serviceRegistration.getAllRegisteredServices().contains(SERVICE_META_DATA_3));
    }

    @Test
    public void checkTheSameServiceRegisteredTwiceHasOnlyOneRegistration() {
        serviceRegistration.registerService(SERVICE_META_DATA);
        serviceRegistration.registerService(SERVICE_META_DATA);

        final Set<ServiceMetaData> registeredServices = serviceRegistration.getAllRegisteredServices();
        int i = 0;
        for (final ServiceMetaData serviceItem : registeredServices) {
            if (serviceItem.equals(SERVICE_META_DATA)) {
                i++;
            }
        }

        assertEquals(1, i);
    }

    @Test
    public void checkNullNotRegisteredIfPassedIn() {
        serviceRegistration.registerService(null);

        assertTrue(serviceRegistration.getAllRegisteredServices().isEmpty());
    }

    @Test
    public void testFindServiceByServiceId() {
        serviceRegistration.registerService(SERVICE_META_DATA);
        serviceRegistration.registerService(SERVICE_META_DATA_2);
        serviceRegistration.registerService(SERVICE_META_DATA_3);

        assertEquals(SERVICE_META_DATA_2,
                     serviceRegistration.findServiceByServiceId(SERVICE_META_DATA_2.getServiceId()));
    }

    @Test
    public void testFindServiceByServiceIdReturnsNullIfNotFound() {
        serviceRegistration.registerService(SERVICE_META_DATA);
        serviceRegistration.registerService(SERVICE_META_DATA_3);

        assertNull(serviceRegistration.findServiceByServiceId(SERVICE_META_DATA_2.getServiceId()));
    }

    @Test
    public void testFindServiceByServiceIdReturnsNullIfNullPassedIn() {
        assertNull(serviceRegistration.findServiceByServiceId(null));
    }

    @Test
    public void testGetAllApplicationPolicies() {
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA);
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA_2);
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA_3);

        final Collection<PolicyMetaData> collectionOfPolicies = new ArrayList<PolicyMetaData>();
        collectionOfPolicies.add(POLICY_META_DATA);
        collectionOfPolicies.add(POLICY_META_DATA_2);
        collectionOfPolicies.add(POLICY_META_DATA_3);

        assertTrue(serviceRegistration.getAllApplicationPolicies()
                   .containsAll(collectionOfPolicies));
    }

    @Test
    public void testGetAllApplicationPoliciesReturnsEmptySetWhenNoneAreAdded() {
        assertTrue(serviceRegistration.getAllApplicationPolicies().isEmpty());
    }

    @Test
    public void testRegisteredApplicationsPoliciesAreEmptyWhenNullPassedIn() {
        serviceRegistration.registerApplicationPolicy(null);

        assertTrue(serviceRegistration.getAllApplicationPolicies().isEmpty());
    }

    @Test
    public void testFindApplicationPolicyById() {
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA);
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA_2);
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA_3);

        assertEquals(POLICY_META_DATA,
                     serviceRegistration.findApplicationPolicyById(POLICY_META_DATA.getPolicyId()));

    }

    @Test
    public void testFindApplicationPolicyByIdReturnsNullWhenPolicyNotFound() {
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA);
        serviceRegistration.registerApplicationPolicy(POLICY_META_DATA_2);

        assertNull(serviceRegistration.findApplicationPolicyById(POLICY_META_DATA_3.getPolicyId()));
    }

    @Test
    public void testFindApplicationPolicyByIdReturnsNullWhenNullPassedIn() {
        assertNull(serviceRegistration.findApplicationPolicyById(null));
    }
}
