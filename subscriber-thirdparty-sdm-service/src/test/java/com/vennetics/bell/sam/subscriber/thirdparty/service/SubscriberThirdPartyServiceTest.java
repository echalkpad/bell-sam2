package com.vennetics.bell.sam.subscriber.thirdparty.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vennetics.bell.sam.error.exceptions.InvalidInputValueException;
import com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute;
import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.bell.sam.sdm.adapter.api.ISdmLdapDao;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;
import com.vennetics.bell.sam.subscriber.thirdparty.service.autoconfigure.SdmServiceProperties;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.InvalidImsiException;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.InvalidMdnException;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.InvalidOuException;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.SubscriberNotFoundException;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.SubscriberNotUniqueException;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.WildcardForbiddenException;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberThirdPartyServiceTest {

    private static final String AFFILIATE_ID = "022";

    private static final String TEN_DIGITS = "4161112222";

    private static final String FIFTEEN_DIGITS = "416111222233333";

    private static final List<Attribute> ATTRIBUTES = Arrays.asList(new Attribute[] {
            new Attribute("MDN", "123456789"), new Attribute("eamLanguage", "E") });

    private static final List<Boolean> FEATURE_ATTRIBUTES = Arrays.asList(Boolean.TRUE,
                                                                          Boolean.FALSE);

    private static final Map<String, Set<String>> SDM_ATTRIBUTES = new HashMap<>();

    private static final List<String> VALID_FEATURE_LIST = Arrays.asList("3WC", "ARW", "BOIC");

    private static final List<String> VALID_OU_LIST = Arrays.asList(AFFILIATE_ID,
                                                                    "023",
                                                                    "206",
                                                                    "409",
                                                                    "410",
                                                                    "502");

    static {
        SDM_ATTRIBUTES.put("MDN", new HashSet<>(Arrays.asList("123456789")));
        SDM_ATTRIBUTES.put("eamLanguage", new HashSet<>(Arrays.asList("E")));
    }

    private static final Map<String, Set<String>> SDM_FEATURE_ATTRIBUTES = new HashMap<>();

    static {
        SDM_FEATURE_ATTRIBUTES.put(SdmConstants.ATTR_FEATURE_CODE,
                                   new HashSet<>(Arrays.asList("3WC", "ARW", "BOIC")));
    }

    private static final SdmAccount SDM_ACCOUNT = new SdmAccount(SDM_ATTRIBUTES);

    private static final List<SdmAccount> SDM_ACCOUNTS = Arrays.asList(SDM_ACCOUNT);

    private static final SdmAccount SDM_FEATURES_ACCOUNT = new SdmAccount(SDM_FEATURE_ATTRIBUTES);

    private static final List<SdmAccount> SDM_FEATURES_ACCOUNTS = Arrays.asList(SDM_FEATURES_ACCOUNT);

    private static final List<SdmAccount> MULTIPLE_SDM_ACCOUNTS = Arrays.asList(SDM_ACCOUNT,
                                                                                SDM_ACCOUNT);

    @Mock
    private ISdmLdapDao ldapDao;

    private SubscriberThirdPartyService testClass;

    @Mock
    private SdmServiceProperties sdmServiceProperties;

    @Before
    public void init() {
        testClass = new SubscriberThirdPartyService(ldapDao, sdmServiceProperties);
    }

    @Test
    public void shouldReturnMappedAttributesOnSuccessForGetSubscriberProfile() {
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter(SdmConstants.ATTR_MDN,
                                                                                                   TEN_DIGITS)));
        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          SubscriberThirdPartyConstants.getAllQueryableAttributes())).thenReturn(Observable.just(SDM_ACCOUNTS));

        final Observable<SubscriberProfile> result = testClass.getSubscriberProfile(SearchFilterType.MDN,
                                                                                    TEN_DIGITS);

        final SubscriberProfile profile = result.toBlocking().single();

        assertEquals(ATTRIBUTES, profile.getAttributes());
    }

    @Test
    public void shouldReturnMappedAttributesOnSuccessForHasFeatureCodes() {
        final Set<SdmQueryFilter> sdmQueryFilters = setUpSdmQueryFilters();
        final Set<String> queryFeatureCodes = new HashSet<>(Arrays.asList(SdmConstants.ATTR_FEATURE_CODE));

        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          queryFeatureCodes)).thenReturn(Observable.just(SDM_FEATURES_ACCOUNTS));
        when(sdmServiceProperties.getValidOus()).thenReturn(VALID_OU_LIST);

        final Observable<FeatureCodes> result = testClass.hasFeatureCodes(TEN_DIGITS,
                                                                          AFFILIATE_ID,
                                                                          Arrays.asList("ARW",
                                                                                        "FEAB"));

        final FeatureCodes featureCodes = result.toBlocking().single();

        assertEquals(FEATURE_ATTRIBUTES, featureCodes.getCodesPresent());
    }

    @Test
    public void shouldReturnMappedAttributesOnSuccessForHasFeatureCodesWithAffiliateCodeUndefined() {
        final SdmQueryFilter mdnQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_MDN, TEN_DIGITS);
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(mdnQueryFilter));
        final Set<String> queryFeatureCodes = new HashSet<>(Arrays.asList(SdmConstants.ATTR_FEATURE_CODE));

        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          queryFeatureCodes)).thenReturn(Observable.just(SDM_FEATURES_ACCOUNTS));
        when(sdmServiceProperties.getValidOus()).thenReturn(VALID_OU_LIST);

        final Observable<FeatureCodes> result = testClass.hasFeatureCodes(TEN_DIGITS,
                                                                          null,
                                                                          Arrays.asList("ARW",
                                                                                        "FEAB"));

        final FeatureCodes featureCodes = result.toBlocking().single();

        assertEquals(FEATURE_ATTRIBUTES, featureCodes.getCodesPresent());
    }

    @Test(expected = SubscriberNotFoundException.class)
    public void shouldThrowExceptionForNullResultForGetSubscriberProfile() {
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter(SdmConstants.ATTR_MDN,
                                                                                                   TEN_DIGITS)));
        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          SubscriberThirdPartyConstants.getAllQueryableAttributes())).thenReturn(Observable.just(null));

        testClass.getSubscriberProfile(SearchFilterType.MDN, TEN_DIGITS).toBlocking().single();

    }

    @Test(expected = SubscriberNotFoundException.class)
    public void shouldThrowExceptionForNullResultForHasFeatureCodes() {
        final Set<SdmQueryFilter> sdmQueryFilters = setUpSdmQueryFilters();
        final Set<String> queryFeatureCodes = new HashSet<>(Arrays.asList(SdmConstants.ATTR_FEATURE_CODE));
        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          queryFeatureCodes)).thenReturn(Observable.just(null));
        when(sdmServiceProperties.getValidOus()).thenReturn(VALID_OU_LIST);
        testClass.hasFeatureCodes(TEN_DIGITS, AFFILIATE_ID, Arrays.asList("ARW", "FEAB"))
                 .toBlocking()
                 .single();

    }

    @Test(expected = SubscriberNotUniqueException.class)
    public void shouldThrowExceptionIfMultipleAccountsFoundForGetSubscriberProfile() {
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter(SdmConstants.ATTR_MDN,
                                                                                                   TEN_DIGITS)));
        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          SubscriberThirdPartyConstants.getAllQueryableAttributes())).thenReturn(Observable.just(MULTIPLE_SDM_ACCOUNTS));

        testClass.getSubscriberProfile(SearchFilterType.MDN, TEN_DIGITS).toBlocking().single();

    }

    @Test(expected = SubscriberNotUniqueException.class)
    public void shouldThrowExceptionIfMultipleAccountsFoundForHasFeatureCodes() {
        final Set<SdmQueryFilter> sdmQueryFilters = setUpSdmQueryFilters();
        final Set<String> queryFeatureCodes = new HashSet<>(Arrays.asList(SdmConstants.ATTR_FEATURE_CODE));
        when(sdmServiceProperties.getValidOus()).thenReturn(VALID_OU_LIST);
        when(ldapDao.getSubscriberProfile(sdmQueryFilters,
                                          queryFeatureCodes)).thenReturn(Observable.just(MULTIPLE_SDM_ACCOUNTS));
        testClass.hasFeatureCodes(TEN_DIGITS, AFFILIATE_ID, Arrays.asList("ARW", "FEAB"))
                 .toBlocking()
                 .single();

    }

    @Test(expected = WildcardForbiddenException.class)
    public void shouldThrowExceptionForWildcardCriteriaForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.MEID, "*").toBlocking().single();

    }

    @Test(expected = WildcardForbiddenException.class)
    public void shouldThrowExceptionForPartialWildcardCriteriaForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.MEID, "ABV*123").toBlocking().single();

    }

    @Test(expected = InvalidMdnException.class)
    public void shouldRejectInvalidNumberForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.MDN, "ABV*123").toBlocking().single();

    }

    @Test(expected = InvalidMdnException.class)
    public void shouldRejectInvalidNumberForHasFeatureCodes() {
        testClass.hasFeatureCodes("ABV*123", AFFILIATE_ID, Arrays.asList("ARW", "FEAB"))
                 .toBlocking()
                 .single();

    }

    @Test(expected = InvalidMdnException.class)
    public void shouldRejectNumberTooLongForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.MDN, FIFTEEN_DIGITS).toBlocking().single();

    }

    @Test(expected = InvalidMdnException.class)
    public void shouldRejectNumberTooShortForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.MDN, "12314").toBlocking().single();

    }

    @Test(expected = InvalidInputValueException.class)
    public void shouldRejectEmptySubIdForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.SUBID, "").toBlocking().single();

    }

    @Test(expected = InvalidInputValueException.class)
    public void shouldRejectEmptyUuidForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.UUID, "").toBlocking().single();

    }

    @Test(expected = InvalidInputValueException.class)
    public void shouldRejectEmptyMeidForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.MEID, "").toBlocking().single();

    }

    @Test(expected = InvalidImsiException.class)
    public void shouldRejectInvalidImsiForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.IMSI, "ABV*123").toBlocking().single();

    }

    @Test(expected = InvalidImsiException.class)
    public void shouldRejectImsiTooLongForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.IMSI, FIFTEEN_DIGITS + "1")
                 .toBlocking()
                 .single();

    }

    @Test(expected = InvalidImsiException.class)
    public void shouldRejectImsiTooShortForGetSubscriberProfile() {

        testClass.getSubscriberProfile(SearchFilterType.IMSI, TEN_DIGITS).toBlocking().single();

    }

    @Test(expected = InvalidOuException.class)
    public void shouldRejectInvalidAffiliateForHasFeatureCodes() {
        testClass.hasFeatureCodes(TEN_DIGITS, "212", Arrays.asList("ARW", "FEAB"))
                 .toBlocking()
                 .single();
    }

    private Set<SdmQueryFilter> setUpSdmQueryFilters() {
        final SdmQueryFilter ouQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_OU, AFFILIATE_ID);
        final SdmQueryFilter mdnQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_MDN, TEN_DIGITS);
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(ouQueryFilter,
                                                                                mdnQueryFilter));
        return sdmQueryFilters;
    }
}
