package com.vennetics.bell.sam.subscriber.thirdparty.server;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberThirdPartyModelConstants;
import com.vennetics.bell.sam.sdm.adapter.api.SdmConstants;
import com.vennetics.bell.sam.subscriber.thirdparty.service.ISubscriberThirdPartyService;
import com.vennetics.bell.sam.subscriber.thirdparty.service.util.test.SubscriberThirdPartyTestDataBuilder;
import com.vennetics.shared.test.utils.categories.IntegrationTest;

import rx.Observable;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { SubscriberThirdPartyApplication.class })
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
        "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
        "security.basic.enabled:false" })
@Category(IntegrationTest.class)
@ActiveProfiles("dev")
public class SubscriberThirdPartyApplicationTest {

    private static final String SEARCH_MDN = "4151234567";

    private static final String AFFILIATE_ID = "023";

    private static final List<String> FEATURE_CODES = Arrays.asList("3FEA", "3FEB", "XXX");

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    private RestTemplate template;

    @Autowired
    private ISubscriberThirdPartyService subscriberThirdPartyService;

    @Test
    public void shouldAllowEnvQuery() {

        template = new TestRestTemplate();

        final ResponseEntity<String> entity = template.getForEntity("http://127.0.0.1:" + port
                        + "/env", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        assertTrue("Body [" + entity.getBody() + "] should contain correct application name",
                   entity.getBody()
                         .contains("\"spring.application.name\":\"subscriber-thirdparty-sdm-server\""));
    }

    @Test
    public void shouldAllowGetSubscriberProfileCall() {

        template = new TestRestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Mock LDAP response
        when(subscriberThirdPartyService.getSubscriberProfile(SearchFilterType.MDN,
                                                              SEARCH_MDN)).thenReturn(Observable.just(SubscriberThirdPartyTestDataBuilder.SUBSCRIBER_PROFILE));

        final String requestUrl = String.format("http://127.0.0.1:%s/api/subscriber/profile?%s=%s&%s=%s",
                                                port,
                                                SubscriberThirdPartyModelConstants.QPARAM_SEARCH_FILTER,
                                                SearchFilterType.MDN,
                                                SubscriberThirdPartyModelConstants.QPARAM_FILTER_VALUE,
                                                SEARCH_MDN);

        template.setMessageConverters(Arrays.asList(jsonMessageConverter, xmlMessageConverter));

        final ResponseEntity<SubscriberProfile> entity = template.exchange(requestUrl,
                                                                           HttpMethod.GET,
                                                                           new HttpEntity<String>(headers),
                                                                           SubscriberProfile.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Expected JSON response",
                   MediaType.APPLICATION_JSON.isCompatibleWith(entity.getHeaders()
                                                                     .getContentType()));

        final SubscriberProfile result = entity.getBody();

        final List<String> attributeValues = result.getAttributes()
                                                   .stream()
                                                   .map(attribute -> attribute.getName())
                                                   .collect(Collectors.toList());

        assertTrue("Attributes [" + attributeValues + "] should contain " + "MDN",
                   attributeValues.contains("MDN"));
        assertTrue("Attributes [" + attributeValues + "] should contain "
                        + SdmConstants.ATTR_EAM_LANGUAGE,
                   attributeValues.contains(SdmConstants.ATTR_EAM_LANGUAGE));
    }

    @Test
    public void shouldAllowHasFeatureCodesCall() {

        template = new TestRestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Mock LDAP response
        when(subscriberThirdPartyService.hasFeatureCodes(SEARCH_MDN,
                                                         AFFILIATE_ID,
                                                         FEATURE_CODES)).thenReturn(Observable.just(SubscriberThirdPartyTestDataBuilder.FEATURE_CODES));

        final String requestUrl = String.format("http://127.0.0.1:%s/api/subscriber/featureCodes?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                                                port,
                                                SubscriberThirdPartyModelConstants.QPARAM_MDN,
                                                SEARCH_MDN,
                                                SubscriberThirdPartyModelConstants.QPARAM_AFFILIATE_ID,
                                                AFFILIATE_ID,
                                                SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                FEATURE_CODES.get(0),
                                                SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                FEATURE_CODES.get(1),
                                                SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                FEATURE_CODES.get(2));

        template.setMessageConverters(Arrays.asList(jsonMessageConverter, xmlMessageConverter));

        final ResponseEntity<FeatureCodes> entity = template.exchange(requestUrl,
                                                                      HttpMethod.GET,
                                                                      new HttpEntity<String>(headers),
                                                                      FeatureCodes.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Expected JSON response",
                   MediaType.APPLICATION_JSON.isCompatibleWith(entity.getHeaders()
                                                                     .getContentType()));

        final FeatureCodes result = entity.getBody();

        final List<Boolean> booleans = result.getCodesPresent();

        assertThat(booleans, contains(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void shouldAllowHasFeatureCodesCallWithoutAffiliateId() {

        template = new TestRestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Mock LDAP response
        when(subscriberThirdPartyService.hasFeatureCodes(SEARCH_MDN,
                                                         null,
                                                         FEATURE_CODES)).thenReturn(Observable.just(SubscriberThirdPartyTestDataBuilder.FEATURE_CODES));

        final String requestUrl = String.format("http://127.0.0.1:%s/api/subscriber/featureCodes?%s=%s&%s=%s&%s=%s&%s=%s",
                                                port,
                                                SubscriberThirdPartyModelConstants.QPARAM_MDN,
                                                SEARCH_MDN,
                                                SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                FEATURE_CODES.get(0),
                                                SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                FEATURE_CODES.get(1),
                                                SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                FEATURE_CODES.get(2));

        template.setMessageConverters(Arrays.asList(jsonMessageConverter, xmlMessageConverter));

        final ResponseEntity<FeatureCodes> entity = template.exchange(requestUrl,
                                                                      HttpMethod.GET,
                                                                      new HttpEntity<String>(headers),
                                                                      FeatureCodes.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Expected JSON response",
                   MediaType.APPLICATION_JSON.isCompatibleWith(entity.getHeaders()
                                                                     .getContentType()));

        final FeatureCodes result = entity.getBody();

        final List<Boolean> booleans = result.getCodesPresent();

        assertThat(booleans, contains(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE));
    }
}
