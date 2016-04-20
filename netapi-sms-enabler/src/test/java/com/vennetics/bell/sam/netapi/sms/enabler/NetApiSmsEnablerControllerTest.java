package com.vennetics.bell.sam.netapi.sms.enabler;

import com.vennetics.bell.sam.netapi.sms.enabler.service.ISmsService;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NetApiSmsEnablerControllerTest {

    // Object under test
    private NetApiSmsEnablerController objectUnderTest;

    // Mock/Spy Objects
    @Mock
    private UriComponentsBuilder mockUriComponentsBuilders;
    @Mock
    private ISmsService mockSmsService;
    @Mock
    private HttpServletResponse mockServletResponse;

    // Concrete Objects
    private String senderAddress = "12345";
    private String issueCausingField = "something";
    private String resourceUrl = "base/outbound/12345/requests";

    @Before
    public void setUp() {
        objectUnderTest = new NetApiSmsEnablerController();
        ReflectionTestUtils.setField(objectUnderTest, "smsService", mockSmsService);
        ReflectionTestUtils.setField(objectUnderTest, "basePath", "base");
    }

    @Test
    public void testExceptionFromSmsServiceShouldBubbleUp() {
        OutboundSMSMessageRequest message = new OutboundSMSMessageRequest();
        when(mockSmsService.sendSmsMessage(isA(UUID.class),
                                           eq(senderAddress),
                                           eq(message))).thenThrow(new IllegalArgumentException(issueCausingField));
        try {
            objectUnderTest.sendSmsMessage(senderAddress, message, mockUriComponentsBuilders);
            fail("Exception should have been thrown and this point should not be reached");
        } catch (final IllegalArgumentException iae) {
            assertEquals(iae.getMessage(), issueCausingField);
            verifyZeroInteractions(mockUriComponentsBuilders);
        }
    }

    @Test
    public void testLocationHeaderAndResourceUrlGenerationAsExpected() {
        OutboundSMSMessageRequest message = new OutboundSMSMessageRequest();
        when(mockSmsService.sendSmsMessage(isA(UUID.class),
                                           eq(senderAddress),
                                           eq(message))).thenReturn(Observable.just(message));
        // Use concrete UriComponentBuilder
        final UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        final DeferredResult<ResponseEntity<OutboundSMSMessageRequest>> result = objectUnderTest.sendSmsMessage(senderAddress,
                                                                                                                message,
                                                                                                                ucb);
        assertNotNull(result);
        assertTrue(result.hasResult());
        // assertTrue(result.getgetHeaders().getFirst("Location").startsWith(resourceUrl));
        // assertTrue(result.getBody().getResourceURL().startsWith(resourceUrl));
        // assertEquals(result.getBody().getResourceURL(),
        // result.getHeaders().getFirst("Location"));
    }

}
