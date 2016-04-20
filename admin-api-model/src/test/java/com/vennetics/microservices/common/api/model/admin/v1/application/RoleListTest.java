package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.microservices.common.api.model.admin.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * RoleList Entity Test
 */
public class RoleListTest {

    private RoleList roleList;

    @Before
    public void setUp() {

        final Role role1 = TestUtils.createDummyRole();
        final Role role2 = TestUtils.createDummyRole();
        final ArrayList<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        roleList = new RoleList(roles);
    }

    @Test
    public void testGetRoles() throws Exception {
        assertEquals(2, roleList.getRoles().size());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("RoleList{roles=[Role{roleId='testRoleId1', description='testDescription', "
                         + "serviceTypeIds=[testServiceId1, testServiceId2], applicationIds="
                         + roleList.getRoles().get(0).getApplicationIds()
                         + "}, Role{roleId='testRoleId1', description='testDescription', "
                         + "serviceTypeIds=[testServiceId1, "
                         + "testServiceId2], applicationIds="
                         + roleList.getRoles().get(1).getApplicationIds() + "}]}",
                     roleList.toString());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(RoleList.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
