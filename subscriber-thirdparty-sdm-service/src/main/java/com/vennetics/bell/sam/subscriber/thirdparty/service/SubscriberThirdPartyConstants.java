package com.vennetics.bell.sam.subscriber.thirdparty.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;

/**
 * Defines constants in use by this service
 */
public final class SubscriberThirdPartyConstants {

    private static final Set<String> ALL_QUERYABLE_ATTRIBUTES = new HashSet<>(Arrays.asList(SdmConstants.ATTR_MDN,
                                                                                            SdmConstants.ATTR_MIN,
                                                                                            SdmConstants.ATTR_SUB_ID,
                                                                                            SdmConstants.ATTR_EAM_LANGUAGE,
                                                                                            SdmConstants.ATTR_OU,
                                                                                            SdmConstants.ATTR_MEID,
                                                                                            SdmConstants.ATTR_UA_PROF_URL,
                                                                                            SdmConstants.ATTR_USERAGENT,
                                                                                            SdmConstants.ATTR_PPD_PROC_INDICATOR,
                                                                                            SdmConstants.ATTR_NETWORKID));

    private SubscriberThirdPartyConstants() {

    }

    /**
     * @return a set of all attributes allowed in a getSubscriberProfile query.
     */
    public static Set<String> getAllQueryableAttributes() {
        return ALL_QUERYABLE_ATTRIBUTES;
    }
}
