package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter.commands;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;

public class HasFeatureCodesCommandTest {

    @Mock
    private RestTemplate mockTemplate;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetFeatureCodeEnablement() throws Exception {
        final String expectedUrl = "http://subx/api/subscriber/featureCodes?mdn=12345&affiliateId=AFF_ID&featureCode=F1&featureCode=F2&featureCode=F3&featureCode=F4";

        final ResponseEntity<FeatureCodes> dummyResult = new ResponseEntity<>(new FeatureCodes(Collections.singletonList(Boolean.TRUE)),
                                                                              HttpStatus.ACCEPTED);

        when(mockTemplate.getForEntity(expectedUrl, FeatureCodes.class)).thenReturn(dummyResult);

        final HasFeatureCodesCommand command = new HasFeatureCodesCommand("subx",
                                                                          "12345",
                                                                          "AFF_ID",
                                                                          Arrays.asList("F1",
                                                                                        "F2",
                                                                                        "F3",
                                                                                        "F4"),
                                                                          mockTemplate);

        assertThat(command.execute(), sameInstance(dummyResult));
    }

    @Test
    public void shouldGetFeatureCodeEnablementWithNullAffiliateId() throws Exception {
        final String expectedUrl = "http://subx/api/subscriber/featureCodes?mdn=12345&affiliateId&featureCode=F1&featureCode=F2&featureCode=F3&featureCode=F4";

        final ResponseEntity<FeatureCodes> dummyResult = new ResponseEntity<>(new FeatureCodes(Collections.singletonList(Boolean.TRUE)),
                                                                              HttpStatus.ACCEPTED);

        when(mockTemplate.getForEntity(expectedUrl, FeatureCodes.class)).thenReturn(dummyResult);

        final HasFeatureCodesCommand command = new HasFeatureCodesCommand("subx",
                                                                          "12345",
                                                                          null,
                                                                          Arrays.asList("F1",
                                                                                        "F2",
                                                                                        "F3",
                                                                                        "F4"),
                                                                          mockTemplate);
        assertThat(command.execute(), sameInstance(dummyResult));
    }
}
