package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Policy Type Entity Test
 */
public class CassandraPolicyTypeTest {

    private CassandraPolicyType cassandraPolicyType;

    @Before
    public void setUp() {
        // Create test data
        cassandraPolicyType = new CassandraPolicyType();
        cassandraPolicyType.setPolicyTypeId(TestUtils.TEST_POLICY_TYPE_ID);
        cassandraPolicyType.setName(TestUtils.TEST_NAME);
        cassandraPolicyType.setPolicyOrder(TestUtils.TEST_POLICY_ORDER);
        cassandraPolicyType.setServiceTypeIds(TestUtils.TEST_SERVICES);
    }

    @Test
    public void testPolicyType() {

        assertEquals(cassandraPolicyType.getPolicyTypeId(), TestUtils.TEST_POLICY_TYPE_ID);
        assertEquals(cassandraPolicyType.getName(), TestUtils.TEST_NAME);
        assertEquals(cassandraPolicyType.getPolicyOrder(), TestUtils.TEST_POLICY_ORDER);
        assertEquals(cassandraPolicyType.getServiceTypeIds(), TestUtils.TEST_SERVICES);
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraPolicyType.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
