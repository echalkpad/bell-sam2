package com.vennetics.bell.sam.model.subscriber.thirdparty;

/**
 * Constants used in the internal sub-x API as accessed from the API GW
 */
public final class SubscriberThirdPartyModelConstants {

    public static final String PATH_FRAGMENT_SUBCRIBER_PROFILE = "profile";

    public static final String PATH_FRAGMENT_FEATURE_CODES = "featureCodes";

    public static final String PATH_FRAGMENT_SUBSCRIBER = "subscriber";

    public static final String PATH_FRAGMENT_API = "api";

    public static final String PATH_SUBSCRIBER_THIRD_PARTY = "/" + PATH_FRAGMENT_API + "/"
                    + PATH_FRAGMENT_SUBSCRIBER + "";

    public static final String PATH_GET_SUB_PROFILE = "/" + PATH_FRAGMENT_SUBCRIBER_PROFILE;

    public static final String PATH_HAS_FEATURE_CODES = "/" + PATH_FRAGMENT_FEATURE_CODES;

    public static final String QPARAM_SEARCH_FILTER = "searchFilter";

    public static final String QPARAM_FILTER_VALUE = "filterValue";

    public static final String QPARAM_MDN = "mdn";

    public static final String QPARAM_AFFILIATE_ID = "affiliateId";

    public static final String QPARAM_FEATURE_CODE = "featureCode";

    private SubscriberThirdPartyModelConstants() {

    }
}
