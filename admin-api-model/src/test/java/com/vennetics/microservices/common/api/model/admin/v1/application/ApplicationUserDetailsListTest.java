package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.microservices.common.api.model.admin.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * ApplicationList Entity Test
 */
public class ApplicationUserDetailsListTest {

    private ApplicationUserDetailsList applicationList;

    @Before
    public void setUp() {

        final ApplicationUserDetails application1 = TestUtils.createDummyApplicationFull();
        final ApplicationUserDetails application2 = TestUtils.createDummyApplicationFull();
        final ArrayList<ApplicationUserDetails> applications = new ArrayList<>();
        applications.add(application1);
        applications.add(application2);
        applicationList = new ApplicationUserDetailsList(applications);
    }

    @Test
    public void testGetApplications() throws Exception {
        assertEquals(2, applicationList.getApplications().size());
    }

    @Test
    public void testToString() throws Exception {
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_CLIENT_ID_1));
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_ACCOUNT_STATE));
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_NAME));
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_EMAIL));
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_ROLES.toString()));
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_APPLICATION_ALLOWED_SERVICES.toString()));
        assertThat(applicationList.toString(), containsString(TestUtils.TEST_APPLICATION_ALLOWED_SERVICES.toString()));
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(ApplicationUserDetailsList.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
