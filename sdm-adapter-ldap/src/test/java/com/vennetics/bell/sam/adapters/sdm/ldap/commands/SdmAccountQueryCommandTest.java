package com.vennetics.bell.sam.adapters.sdm.ldap.commands;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;

import com.vennetics.bell.sam.adapters.sdm.ldap.SdmTestData;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class SdmAccountQueryCommandTest {

    private static final String BELL_OU = "023";

    private static final String SUB_ID_VALUE = "0000000017567858821617455151291839889960-023X_wap2.bellmobility.ca";

    private static final String SUB_ID2 = "subId2";

    @Mock
    private LdapOperations ldapTemplate;

    @Mock
    private AttributesMapper<String> attributesMapper;

    private SdmAccountQueryCommand testClass;

    @Before
    public void init() {
        testClass = new SdmAccountQueryCommand(ldapTemplate,
                                               new SdmQueryFilter(SUB_ID2,
                                                                  SUB_ID_VALUE),
                                               new HashSet<>(Arrays.asList("ou", "mdn")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnQueryAsSdmAccount() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(SdmTestData.SDM_ACCOUNT));

        final Observable<List<SdmAccount>> observableResult = testClass.toObservable();

        assertEquals(SdmTestData.SDM_ACCOUNT, observableResult.toBlocking().single().get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeNullSafe() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(null);

        final Observable<List<SdmAccount>> observableResult = testClass.toObservable();

        assertEquals(new ArrayList<SdmAccount>(0), observableResult.toBlocking().single());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMapEmptyResult() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(new SdmAccount[0]));

        final Observable<List<SdmAccount>> observableResult = testClass.toObservable();

        assertEquals(new ArrayList<SdmAccount>(0), observableResult.toBlocking().single());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportOuAsMultipleConditionQuery() {

        final ArgumentCaptor<LdapQuery> argument = ArgumentCaptor.forClass(LdapQuery.class);

        final Set<SdmQueryFilter> filters = new HashSet<>();
        filters.add(new SdmQueryFilter(SUB_ID2,
                                       SUB_ID_VALUE));

        filters.add(new SdmQueryFilter(SdmConstants.ATTR_OU, BELL_OU));

        testClass = new SdmAccountQueryCommand(ldapTemplate,
                                               filters,
                                               new HashSet<>(Arrays.asList("ou", "mdn")));


        when(ldapTemplate.search(argument.capture(),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(SdmTestData.SDM_ACCOUNT));

        final Observable<List<SdmAccount>> observableResult = testClass.toObservable();

        assertEquals(SdmTestData.SDM_ACCOUNT, observableResult.toBlocking().single().get(0));

        // Check query setup correctly
        assertThat(argument.getValue().base().toString(), equalTo(""));
        assertThat(argument.getValue().filter().encode(), containsString(SUB_ID2));
        assertThat(argument.getValue().filter().encode(),
                   containsString(SUB_ID_VALUE));
        assertThat(argument.getValue().filter().encode(), containsString(SdmConstants.ATTR_OU));
        assertThat(argument.getValue().filter().encode(), containsString(BELL_OU));

        assertThat(Arrays.asList(argument.getValue().attributes()),
                   containsInAnyOrder("ou", "mdn"));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnMultipleEntriesIfFound() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(SdmTestData.SDM_ACCOUNT,
                                                                                              SdmTestData.SDM_ACCOUNT));

        final Observable<List<SdmAccount>> observableResult = testClass.toObservable();

        assertEquals(SdmTestData.SDM_ACCOUNT, observableResult.toBlocking().single().get(0));
        assertEquals(SdmTestData.SDM_ACCOUNT, observableResult.toBlocking().single().get(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAllowNullAttributes() {

        testClass = new SdmAccountQueryCommand(ldapTemplate,
                                               new SdmQueryFilter(SUB_ID2, SUB_ID_VALUE),
                                               null);

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 isNotNull(AttributesMapper.class))).thenReturn(Arrays.asList(SdmTestData.SDM_ACCOUNT));

        final Observable<List<SdmAccount>> observableResult = testClass.toObservable();

        assertEquals(SdmTestData.SDM_ACCOUNT, observableResult.toBlocking().single().get(0));
    }
}
