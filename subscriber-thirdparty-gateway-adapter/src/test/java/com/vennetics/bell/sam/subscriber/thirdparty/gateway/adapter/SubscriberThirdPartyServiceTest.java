package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute;
import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberThirdPartyServiceTest {

    @InjectMocks
    @Autowired
    private SubscriberThirdPartyAdapter service;

    @Mock
    private RestTemplate mockTemplate;

    @Test
    public void shouldGetSubscriberProfile() throws Exception {
        final String expectedUrl = "http://subscriber-thirdparty-sdm-server/api/subscriber/profile?searchFilter=SUBID&filterValue=VALUE";
        final ResponseEntity<SubscriberProfile> dummyResult = new ResponseEntity<>(new SubscriberProfile(Collections.singletonList(new Attribute("SUBID",
                                                                                                                                                 "123456789"))),
                                                                                   HttpStatus.ACCEPTED);

        when(mockTemplate.getForEntity(expectedUrl,
                                       SubscriberProfile.class)).thenReturn(dummyResult);

        final ResponseEntity<SubscriberProfile> response = service.getSubscriberProfile("subscriber-thirdparty-sdm-server",
                                                                                        SearchFilterType.SUBID,
                                                                                        "VALUE");
        assertThat(response, is(sameInstance(dummyResult)));
    }

    @Test
    public void shouldReportFeatureCodeEnablement() throws Exception {
        final String expectedUrl = "http://subscriber-thirdparty-sdm-server/api/subscriber/featureCodes?mdn=12345&affiliateId=AFF_ID&featureCode=F1&featureCode=F2";

        final ResponseEntity<FeatureCodes> dummyResult = new ResponseEntity<>(new FeatureCodes(),
                                                                               HttpStatus.ACCEPTED);

        when(mockTemplate.getForEntity(expectedUrl,
                                       FeatureCodes.class)).thenReturn(dummyResult);

        final ResponseEntity<FeatureCodes> response = service.hasFeatureCodes("subscriber-thirdparty-sdm-server",
                                                                              "12345",
                                                                              "AFF_ID",
                                                                              Arrays.asList("F1",
                                                                                             "F2"));
        assertThat(response, is(sameInstance(dummyResult)));

    }
}
