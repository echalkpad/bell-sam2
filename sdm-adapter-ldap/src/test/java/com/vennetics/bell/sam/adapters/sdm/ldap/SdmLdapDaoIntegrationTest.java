package com.vennetics.bell.sam.adapters.sdm.ldap;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vennetics.bell.sam.sdm.adapter.api.ISdmLdapDao;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

import rx.Observable;

/**
 * Can be used to test real LDAP connectivity.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SdmLdapConfig.class, SdmTestConfig.class })
@ActiveProfiles("production")
public class SdmLdapDaoIntegrationTest {

    @Autowired
    private ISdmLdapDao testClass;

    @Test
    public void test() {

        final Observable<List<SdmAccount>> result = testClass.getSubscriberProfile(new SdmQueryFilter("mdn",
                                                                                                      "4161231234"),
                                                                                   new HashSet<>(Arrays.asList(new String[] {
                                                                                           "ou",
                                                                                           "featureCode" })));

        // Real LDAP command won't execute if we don't subscribe so this will
        // verify all the spring-wiring worked.
        assertNotNull(result);

        // If we had real LDAP server for integration testing we could get
        // result as follows.
        // assertEquals("409",
        // result.toBlocking().single().getAttributes().get("ou").iterator().next());
    }

}
