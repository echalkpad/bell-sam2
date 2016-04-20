package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.microservices.common.api.model.admin.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * ProtocolTypeList test
 */
public class ProtocolTypeListTest {

    private ProtocolTypeList protocolTypeList;

    @Before
    public void setUp() {
        final ProtocolType protocolType1 = TestUtils.createDummyProtocolType();
        final ProtocolType protocolType2 = TestUtils.createDummyProtocolType();
        final ArrayList<ProtocolType> protocolTypes = new ArrayList<>();
        protocolTypes.add(protocolType1);
        protocolTypes.add(protocolType2);
        protocolTypeList = new ProtocolTypeList(protocolTypes);
    }

    @Test
    public void testGetProtocolTypes() throws Exception {
        assertEquals(2, protocolTypeList.getProtocolTypes().size());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("ProtocolTypeList{protocolTypes=[ProtocolType{protocolTypeId='testPolicyId', "
                         + "description='testDescription', "
                         + "serviceTypeIds=[testServiceTypeId2, testServiceTypeId1]}, "
                         + "ProtocolType{protocolTypeId='testPolicyId', "
                         + "description='testDescription', "
                         + "serviceTypeIds=[testServiceTypeId2, testServiceTypeId1]}]}",
                     protocolTypeList.toString());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(ProtocolTypeList.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
