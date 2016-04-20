package com.vennetics.bell.sam.subscriber.thirdparty.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

/**
 * Spring REST Controller for SubX
 */
@Service
public class SubscriberThirdPartyService implements ISubscriberThirdPartyService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThirdPartyService.class);

    private static final String TEN_DIGIT_NUMBER = "[0-9]{10}";

    private static final String FIFTEEN_DIGIT_NUMBER = "[0-9]{15}";

    private final ISdmLdapDao subxLdapDao;

    private final SdmServiceProperties sdmServiceProperties;

    @Autowired
    public SubscriberThirdPartyService(final ISdmLdapDao subxLdapDao,
                                       final SdmServiceProperties sdmServiceProperties) {
        super();
        this.subxLdapDao = subxLdapDao;
        this.sdmServiceProperties = sdmServiceProperties;
    }

    @Override
    public Observable<SubscriberProfile> getSubscriberProfile(final SearchFilterType filterAttribute,
                                                              final String filterValue) {

        switch (filterAttribute) {
            case MDN:
                validateMdnRules(filterValue);
                break;
            case IMSI:
                validateImsiRules(filterValue);
                break;
            default:
                validateFilterValueNotEmpty(filterValue);
                break;
        }

        validateFilterValueRules(filterValue);

        // This will eventually be hooked up to return the attributes allowed
        // for each TPA by policy
        final Set<String> attributes = SubscriberThirdPartyConstants.getAllQueryableAttributes();

        return doGetSubscriber(filterAttribute, filterValue, attributes);
    }

    @Override
    public Observable<FeatureCodes> hasFeatureCodes(final String mdn,
                                                    final String affliateId,
                                                    final List<String> featureCodes) {
        // TODO: validate this should return POL0070 and not POL0010 as
        // confluence suggests
        validateMdnRules(mdn);

        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>();
        final SdmQueryFilter mdnQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_MDN, mdn);
        sdmQueryFilters.add(mdnQueryFilter);
        if (StringUtils.isNotBlank(affliateId)) {
            validateOuCode(affliateId);
            final SdmQueryFilter ouQueryFilter = new SdmQueryFilter(SdmConstants.ATTR_OU,
                                                                    affliateId);
            sdmQueryFilters.add(ouQueryFilter);
        }
        final Set<String> queryFeatureCodes = new HashSet<>(Arrays.asList(SdmConstants.ATTR_FEATURE_CODE));

        return doGetFeatureCodes(sdmQueryFilters, queryFeatureCodes, featureCodes);
    }

    private Observable<SubscriberProfile> doGetSubscriber(final SearchFilterType filterAttribute,
                                                          final String filterValue,
                                                          final Set<String> attributes) {
        final String searchAttribtue = SearchFilterToAttribtueMapper.searchFilterToAttribute(filterAttribute);
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter(searchAttribtue,
                                                                                                   filterValue)));
        return doGetSdmAccount(sdmQueryFilters,
                               attributes).map(SubscriberThirdPartyService::toSubscriberProfile);
    }

    private Observable<FeatureCodes> doGetFeatureCodes(final Set<SdmQueryFilter> filters,
                                                       final Set<String> attributes,
                                                       final List<String> featureCodes) {
        return doGetSdmAccount(filters, attributes).map(sdmAccount -> toFeatureCodes(sdmAccount,
                                                                                     featureCodes));
    }

    private Observable<SdmAccount> doGetSdmAccount(final Set<SdmQueryFilter> filters,
                                                   final Set<String> attributes) {
        return subxLdapDao.getSubscriberProfile(filters, attributes).map(queryResult -> {
            if (CollectionUtils.isEmpty(queryResult)) {
                throw new SubscriberNotFoundException(filters);
            } else if (queryResult.size() > 1) {
                throw new SubscriberNotUniqueException(filters);
            } else {
                // Return first element.
                return queryResult.get(0);
            }

        });
    }

    private static void validateFilterValueNotEmpty(final String filterValue) {
        if (StringUtils.isBlank(filterValue)) {
            throw new InvalidInputValueException(filterValue);
        }
    }

    private static void validateFilterValueRules(final String filterValue) {
        if (StringUtils.contains(filterValue, "*")) {
            throw new WildcardForbiddenException(filterValue);
        }
    }

    private static void validateImsiRules(final String filterValue) {
        if (!Pattern.matches(FIFTEEN_DIGIT_NUMBER, filterValue)) {
            throw new InvalidImsiException(filterValue);
        }
    }

    private static void validateMdnRules(final String filterValue) {
        if (!Pattern.matches(TEN_DIGIT_NUMBER, filterValue)) {
            throw new InvalidMdnException(filterValue);
        }
    }

    private void validateOuCode(final String affliateId) {
        if (!sdmServiceProperties.getValidOus().contains(affliateId)) {
            throw new InvalidOuException(affliateId);
        }
    }

    // Is used, sonar doesn't recognise this.
    @SuppressWarnings("squid:UnusedPrivateMethod")
    private static SubscriberProfile toSubscriberProfile(final SdmAccount queryResult) {

        final List<Attribute> attributes = queryResult.getAttributes()
                                                      .entrySet()
                                                      .stream()
                                                      .map(entry -> new Attribute(entry.getKey(),
                                                                                  entry.getValue()
                                                                                       .iterator()
                                                                                       .next()))
                                                      .collect(Collectors.toList());

        final SubscriberProfile result = new SubscriberProfile(attributes);

        logger.debug("Mapped account to result: {}", result);
        return result;
    }

    private static FeatureCodes toFeatureCodes(final SdmAccount queryResult,
                                               final List<String> featureCodes) {
        final FeatureCodes result = new FeatureCodes();
        final List<Boolean> attributes = new ArrayList<>();
        if (queryResult.getAttributes().containsKey(SdmConstants.ATTR_FEATURE_CODE)) {
            final Set<String> allCodes = queryResult.getAttributes().get(SdmConstants.ATTR_FEATURE_CODE);
            for (final String featureCode : featureCodes) {
                final Boolean hasCode = allCodes.contains(featureCode) ? Boolean.TRUE : Boolean.FALSE;
                attributes.add(hasCode);
            }
        } else {
            attributes.addAll(featureCodes.stream()
                                          .map(featureCode -> Boolean.FALSE)
                                          .collect(Collectors.toList()));
        }

        result.getCodesPresent().addAll(attributes);
        return result;
    }
}
