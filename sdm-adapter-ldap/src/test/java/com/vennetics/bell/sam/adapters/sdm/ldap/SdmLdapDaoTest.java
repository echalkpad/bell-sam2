package com.vennetics.bell.sam.adapters.sdm.ldap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;

import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class SdmLdapDaoTest {

    @Mock
    private LdapOperations ldapTemplate;

    private SdmLdapDao testClass;

    @Before
    public void init() {
        testClass = new SdmLdapDao(ldapTemplate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportSingleAttributeQuery() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(SdmTestData.SDM_ACCOUNT));

        final SdmQueryFilter filter = new SdmQueryFilter("subId2",
                                                         "0000000017567858821617455151291839889960-023X_wap2.bellmobility.ca");

        final Observable<List<SdmAccount>> observableResult = testClass.getSubscriberProfile(filter,
                                                                                             new HashSet<>(Arrays.asList("ou",
                                                                                                                         "mdn")));

        final SdmAccount result = observableResult.toBlocking().single().get(0);

        assertEquals(SdmTestData.SDM_ACCOUNT, result);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportMultiAttributeQuery() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(SdmTestData.SDM_ACCOUNT));

        final Set<SdmQueryFilter> filters = new HashSet<>();
        filters.add(new SdmQueryFilter("subId2",
                                       "0000000017567858821617455151291839889960-023X_wap2.bellmobility.ca"));
        filters.add(new SdmQueryFilter("mdn", "4151234567"));

        final Observable<List<SdmAccount>> observableResult = testClass.getSubscriberProfile(filters,
                                                                                             new HashSet<>(Arrays.asList("ou",
                                                                                                                         "mdn")));

        final SdmAccount result = observableResult.toBlocking().single().get(0);

        assertEquals(SdmTestData.SDM_ACCOUNT, result);

    }

}
