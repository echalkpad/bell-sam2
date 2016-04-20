package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * AllowedService Type Entity Test
 */
public class CassandraServiceTypeTest {
    public static final String SERVICE_TYPE_ID = "service_type_id";
    public static final String NAME = "name";
    public static final String POLICY_TYPE_IDS = "policy_type_ids";
    public static final String PROTOCOL_TYPE_ID = "protocol_type_id";
    private CassandraServiceType policyType;

    @Before
    public void setUp() {
        // Create test data
        policyType = new CassandraServiceType();
        policyType.setServiceTypeId(TestUtils.TEST_SERVICE_TYPE_ID_1);
        policyType.setName(TestUtils.TEST_NAME);
        policyType.setPolicyTypeIds(TestUtils.TEST_POLICIES);
        policyType.setProtocolTypeId(TestUtils.TEST_PROTOCOL_TYPE_ID);
    }

    @Test
    public void testServiceType() {

        assertEquals(policyType.getServiceTypeId(), TestUtils.TEST_SERVICE_TYPE_ID_1);
        assertEquals(policyType.getName(), TestUtils.TEST_NAME);
        assertEquals(policyType.getPolicyTypeIds(), TestUtils.TEST_POLICIES);
        assertEquals(policyType.getProtocolTypeId(), TestUtils.TEST_PROTOCOL_TYPE_ID);
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraServiceType.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
