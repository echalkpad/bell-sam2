package com.vennetics.bell.sam.smsx.enabler;

import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.smsx.enabler.service.ISmsService;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class SmsXEnablerControllerTest {

    // Object under test
    private SmsXEnablerController objectUnderTest;

    // Mock/Spy Objects
    @Mock private ISmsService mockSmsService;
    @Mock private HttpServletResponse mockServletResponse;

    // Concrete Objects
    private String senderAddress = "12345";
    private String issueCausingField = "something";
    private String message = "some message";
    private List<String> addresses = Arrays.asList("1234567");
    private String notifyUrl = "http://bbc.co.uk";
    private String correlator = "abc123";

    @Before public void setUp() {
        objectUnderTest = new SmsXEnablerController();
        ReflectionTestUtils.setField(objectUnderTest, "smsService", mockSmsService);
    }

    @Test public void testExceptionFromSmsServiceShouldBubbleUp() {
        when(mockSmsService.sendSmsMessage(isA(UUID.class), isA(SendSmsRequest.class))).thenThrow(
                        new IllegalArgumentException(issueCausingField));
        try {
            objectUnderTest.sendSmsMessage(senderAddress,
                                           message,
                                           addresses,
                                           notifyUrl,
                                           correlator);
            fail("Exception should have been thrown and this point should not be reached");
        } catch (final IllegalArgumentException iae) {
            assertEquals(iae.getMessage(), issueCausingField);
        }
    }

    @Test public void testLocationHeaderAndResourceUrlGenerationAsExpected() {
        when(mockSmsService.sendSmsMessage(isA(UUID.class), isA(SendSmsRequest.class))).thenReturn(
                        Observable.just(null));
        // Use concrete UriComponentBuilder
        final UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        final DeferredResult<ResponseEntity<SendSmsResponse>> result = objectUnderTest.sendSmsMessage(
                        senderAddress,
                        message,
                        addresses,
                        notifyUrl,
                        correlator);
        assertNotNull(result);
        assertTrue(result.hasResult());
    }

}
