package com.vennetics.bell.sam.subscriber.thirdparty.service.util.test;

import java.util.Arrays;

import com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute;
import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;

/**
 * Re-usable test data in unit tests
 */
public final class SubscriberThirdPartyTestDataBuilder {

    public static final SubscriberProfile SUBSCRIBER_PROFILE = new SubscriberProfile(Arrays.asList(new Attribute[] {
            new Attribute("MDN", "123456789"), new Attribute("eamLanguage", "E") }));

    public static final FeatureCodes FEATURE_CODES = new FeatureCodes(Arrays.asList(Boolean.TRUE,
                                                                                    Boolean.TRUE,
                                                                                    Boolean.FALSE));

    private SubscriberThirdPartyTestDataBuilder() {

    }

}
