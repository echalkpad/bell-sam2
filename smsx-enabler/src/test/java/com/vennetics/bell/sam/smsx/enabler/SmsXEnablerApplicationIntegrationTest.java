package com.vennetics.bell.sam.smsx.enabler;

import com.vennetics.bell.sam.adapters.messaging.smpp.exception.SendSmppException;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;
import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.MessageResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.smsx.enabler.service.ISmsService;
import com.vennetics.shared.test.utils.categories.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Case to verify that we can run up the application and pull sensible
 * values back.
 *
 * @author markcorkery
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmsxEnablerApplication.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
                "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
                "security.basic.enabled:false", "smpp.bindType=TRANSCEIVER",
                "smpp.primarySmppConnection.host=localhost", "smpp.windowSize=1",
                "smpp.primarySmppConnection.connectionTimeout=10000",
                "smpp.maxConcurrentSessions=1", "smpp.requestExpiryTimeout=5000",
                "smpp.systemId=smppclient1", "smpp.password=password",
                "smpp.enquireLinkPollingInterval=15000", "smpp.sourceAddress.ton=3",
                "smpp.sourceAddress.npi=0", "smpp.destinationAddress.ton=1",
                "smpp.destinationAddress.npi=1", "address.defaultRegion=CA",
                "smpp.enquireLinkTimeoutMilliseconds=5000",
                "smpp.outBoundThrottlingRate.intervalInMilliseconds=10000",
                "smpp.outBoundThrottlingRate.maxTasksPerInterval=100",
                "smpp.inBoundThrottlingRate.intervalInMilliseconds=10000",
                "smpp.inBoundThrottlingRate.maxTasksPerInterval=100",
                "smpp.smppUnbindTimeoutInMilliseconds=5000",
                "smpp.submitMessageTimeoutMilliseconds=10000"})
@Category(IntegrationTest.class)
public class SmsXEnablerApplicationIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    private final RestTemplate restTemplate = new TestRestTemplate();

    // Object under test
    @InjectMocks
    @Autowired
    private SmsXEnablerController controllerUnderTest;

    // Mock Objects
    @Autowired
    private ISmsService smsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    // Concrete Objects
    public static final String SENDER_NAME_VALUE = "short:12345";
    public static final String DESTINATION_ADDRESS_VALUE = "tel:+123456789";
    public static final String MESSAGE_TEXT_VALUE = "Hi there";
    public static final String DELIVERED_TO_TERMINAL = "DeliveredToTerminal";
    public static final String CORRELATOR_VALUE = "123456";
    public static final String NOTIFY_URL_VALUE = "http://examples.com/callback";
    private static UUID requestId = UUID.randomUUID();

    @Test
    public void contextLoads() {
    }

    @Test
    public void testEnvLoads() {
        ResponseEntity<String> entity = restTemplate.getForEntity(
                        "http://localhost:" + port + "/env", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("\"spring.application.name\":\"smsx-enabler\""));
    }

    @Test
    public void shouldInvokeSmsServiceServiceForSendSms() throws Exception {
        when(smsService.sendSmsMessage(isA(UUID.class), isA(SendSmsRequest.class))).thenReturn(
                        Observable.just(null));
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(SmsXEnablerController.SENDER_NAME, SENDER_NAME_VALUE);
        requestParams.add(SmsXEnablerController.ADDRESS, DESTINATION_ADDRESS_VALUE);
        requestParams.add(SmsXEnablerController.CORRELATOR, CORRELATOR_VALUE);
        requestParams.add(SmsXEnablerController.NOTIFY_URL, NOTIFY_URL_VALUE);
        requestParams.add(SmsXEnablerController.MESSAGE, MESSAGE_TEXT_VALUE);
        ResponseEntity<SendSmsResponse> entity = restTemplate.postForEntity(
                        "http://localhost:" + port + SmsXEnablerController.OUTBOUND_REQUESTS_PATH,
                        requestParams,
                        SendSmsResponse.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(entity.getBody().getMessageId(), is(not(nullValue())));
    }

    @Test
    public void shouldHandleInvalidAddressExceptionForSendSms() throws Exception {
        when(smsService.sendSmsMessage(isA(UUID.class),
                                       isA(SendSmsRequest.class))).thenThrow(new InvalidAddressException(
                        DESTINATION_ADDRESS_VALUE));
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(SmsXEnablerController.SENDER_NAME, SENDER_NAME_VALUE);
        requestParams.add(SmsXEnablerController.ADDRESS, DESTINATION_ADDRESS_VALUE);
        requestParams.add(SmsXEnablerController.MESSAGE, MESSAGE_TEXT_VALUE);
        ResponseEntity<SendSmsResponse> entity = restTemplate.postForEntity(
                        "http://localhost:" + port + SmsXEnablerController.OUTBOUND_REQUESTS_PATH,
                        requestParams,
                        SendSmsResponse.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldHandleSendExceptionExceptionForSendSms() throws Exception {
        when(smsService.sendSmsMessage(isA(UUID.class),
                                       isA(SendSmsRequest.class))).thenThrow(new SendSmppException(
                        DESTINATION_ADDRESS_VALUE,
                        new RuntimeException()));
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(SmsXEnablerController.SENDER_NAME, SENDER_NAME_VALUE);
        requestParams.add(SmsXEnablerController.ADDRESS, DESTINATION_ADDRESS_VALUE);
        requestParams.add(SmsXEnablerController.MESSAGE, MESSAGE_TEXT_VALUE);
        ResponseEntity<SendSmsResponse> entity = restTemplate.postForEntity(
                        "http://localhost:" + port + SmsXEnablerController.OUTBOUND_REQUESTS_PATH,
                        requestParams,
                        SendSmsResponse.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void shouldInvokeSmsServiceServiceForGetDeliveryInfo() throws Exception {
        when(smsService.getDeliveryStatus(requestId.toString())).thenReturn(Observable.just(
                        createDefaultDeliveryStatusResponse()));
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        ResponseEntity<String> entity = restTemplate.getForEntity(buildUrl(),
                                                                  String.class,
                                                                  requestParams);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody().contains("address=\"" + DESTINATION_ADDRESS_VALUE + "\""),
                   is(true));
        assertThat(entity.getBody().contains("status=\"DeliveredToTerminal\""), is(true));
    }

    private String buildUrl() {
        return String.format(
                        "http://localhost:" + port + SmsXEnablerController.OUTBOUND_REQUESTS_PATH
                                        + "?messageId=%s", requestId.toString());
    }

    private DeliveryStatusResponse createDefaultDeliveryStatusResponse() {
        final DeliveryStatusResponse deliveryStatusResponse = new DeliveryStatusResponse();
        final MessageResponse messageResponse = new MessageResponse();
        messageResponse.setStatus(DELIVERED_TO_TERMINAL);
        messageResponse.setAddress(DESTINATION_ADDRESS_VALUE);
        deliveryStatusResponse.getMessageResponses().add(messageResponse);
        return deliveryStatusResponse;
    }

    @Configuration
    public static class TestConfig {

        @Bean
        /**
         * Utility to ensure mocked service is inject into controller rather
         * than the real instanc wired in.
         */
        ISmsService smsService() {
            return mock(ISmsService.class);
        }
    }

}
