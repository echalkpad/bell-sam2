package com.vennetics.bell.sam.adapters.sdm.stub;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

public class SdmLdapDaoTest {

    private SdmLdapDao testClass;

    @Before
    public void init() {
        testClass = new SdmLdapDao();
        testClass.init();
    }

    @Test
    public void shouldSupportSingleFilterQuery() {

        final SdmAccount result = testClass.getSubscriberProfile(new SdmQueryFilter("filter",
                                                                                    "value"),
                                                                 new HashSet<>(0))
                                           .toBlocking()
                                           .single()
                                           .get(0);
        assertTrue(result.getAttributes().get("eamLanguage").contains("E"));

    }

    @Test
    public void shouldSupportMultiFilterQuery() {

        final SdmAccount result = testClass.getSubscriberProfile(new HashSet<>(Arrays.asList(new SdmQueryFilter("filter",
                                                                                                                "value"),
                                                                                             new SdmQueryFilter("filter1",
                                                                                                                "value1"))),
                                                                 new HashSet<>(0))
                                           .toBlocking()
                                           .single()
                                           .get(0);
        assertTrue(result.getAttributes().get("eamLanguage").contains("E"));

    }

}
