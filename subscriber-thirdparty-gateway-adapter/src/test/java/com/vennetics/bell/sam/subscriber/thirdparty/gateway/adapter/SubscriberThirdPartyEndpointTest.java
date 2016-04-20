package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.microservices.common.core.soap.gateway.adapter.errors.exceptions.InvalidServiceIndentifierException;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.Attribute;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.SearchFilter;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberThirdPartyEndpointTest {

    @InjectMocks
    @Autowired
    private SubscriberThirdPartyEndpoint subscriber;

    @Mock
    private ISubscriberThirdPartyAdapter mockSubxService;
    @Mock
    private WebServiceContext mockContext;

    @Rule
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldGetSubscriberProfile() throws Exception {

        deliverUrlFromMockWebServiceContext("http://192.168.99.100:8087/bell/subscriber/subscriber-thirdparty-sdm-server/subscriber/v1_0");

        when(mockSubxService.getSubscriberProfile("subscriber-thirdparty-sdm-server",
                                                  SearchFilterType.MDN,
                                                  "filterValue")).thenReturn(new ResponseEntity<SubscriberProfile>(new SubscriberProfile(Arrays.asList(newAttribute("Key1",
                                                                                                                                                                    "Value1"),
                                                                                                                                                       newAttribute("Key2",
                                                                                                                                                                    "Value2"))),
                                                                                                                   HttpStatus.ACCEPTED));

        final List<Attribute> result = subscriber.getSubscriberProfile(SearchFilter.MDN,
                                                                       "filterValue");

        assertThat(result.get(0).getKey(), is("Key1"));
        assertThat(result.get(1).getKey(), is("Key2"));
    }

    @Test
    public void shouldReportFeatureCodes() throws Exception {
        deliverUrlFromMockWebServiceContext("http://192.168.99.100:8087/bell/subscriber/subscriber-thirdparty-sdm-server/subscriber/v1_0");

        when(mockSubxService.hasFeatureCodes("subscriber-thirdparty-sdm-server",
                                             "12345",
                                             "AFF_ID",
                                             Arrays.asList("F1",
                                                           "F2"))).thenReturn(new ResponseEntity<FeatureCodes>(new FeatureCodes(Arrays.asList(Boolean.FALSE,
                                                                                                                                              Boolean.TRUE)),
                                                                                                               HttpStatus.ACCEPTED));

        final List<Boolean> result = subscriber.hasFeatureCodes("12345",
                                                                "AFF_ID",
                                                                Arrays.asList("F1", "F2"));

        assertThat(result.get(0), is(false));
        assertThat(result.get(1), is(true));
    }

    @Test
    public void shouldReturnDefaultValueIfUnsupportedMethodsCalled() throws Exception {
        assertThat(subscriber.changeSubscriberPassword("mdn", "old", "new", "new", "id"),
                   is(false));
        assertThat(subscriber.getSubscriberPassword("mdn", "id"), is(nullValue()));
    }

    @Test
    public void shouldRejectRequestIfServiceIdentifierIsNotRecognised() throws Exception {

        deliverUrlFromMockWebServiceContext("http://192.168.99.100:8087/bell/subscriber");

        thrown.expect(any(InvalidServiceIndentifierException.class));
        subscriber.getSubscriberProfile(SearchFilter.MDN, "filterValue");
    }

    private void deliverUrlFromMockWebServiceContext(final String url) {
        final MessageContext mockMessageContext = mock(MessageContext.class);
        final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockContext.getMessageContext()).thenReturn(mockMessageContext);
        when(mockMessageContext.get(MessageContext.SERVLET_REQUEST)).thenReturn(mockRequest);
        when(mockRequest.getRequestURI()).thenReturn(url);
    }

    private com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute newAttribute(final String key,
                                                                                      final String value) {
        return new com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute(key, value);
    }
}
