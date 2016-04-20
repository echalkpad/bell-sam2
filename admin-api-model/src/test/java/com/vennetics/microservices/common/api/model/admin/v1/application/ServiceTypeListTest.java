package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.microservices.common.api.model.admin.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * ServiceTypeList Entity Test
 */
public class ServiceTypeListTest {

    private ServiceTypeList serviceTypeList;

    @Before
    public void setUp() {

        final ServiceType serviceType1 = TestUtils.createDummyServiceType();
        final ServiceType serviceType2 = TestUtils.createDummyServiceType();
        final ArrayList<ServiceType> serviceTypes = new ArrayList<>();
        serviceTypes.add(serviceType1);
        serviceTypes.add(serviceType2);
        serviceTypeList = new ServiceTypeList(serviceTypes);
    }

    @Test
    public void testGetServiceTypes() throws Exception {
        assertEquals(2, serviceTypeList.getServiceTypes().size());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("ServiceTypeList{serviceTypes=[ServiceType{serviceTypeId='testServiceId1', description='testName', "
                         + "policyTypeIds=[testPolicyId1, testPolicyId2], protocolTypeId='testProtocolId'}, "
                         + "ServiceType{serviceTypeId='testServiceId1', description='testName', "
                         + "policyTypeIds=[testPolicyId1, testPolicyId2], protocolTypeId='testProtocolId'}]}",
                     serviceTypeList.toString());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(ServiceTypeList.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
