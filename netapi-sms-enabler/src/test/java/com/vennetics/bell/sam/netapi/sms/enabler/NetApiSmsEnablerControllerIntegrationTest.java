package com.vennetics.bell.sam.netapi.sms.enabler;

import com.vennetics.bell.sam.adapters.messaging.smpp.exception.SendSmppException;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;
import com.vennetics.bell.sam.netapi.sms.enabler.service.ISmsService;
import com.vennetics.shared.test.utils.categories.IntegrationTest;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetApiSmsEnablerApplication.class)
@WebIntegrationTest({ "server.port=0", "management.port=0", "spring.cloud.config.enabled=false",
        "eureka.client.registerWithEureka:false", "eureka.client.fetchRegistry:false",
        "smpp.bindType=TRANSCEIVER", "smpp.primarySmppConnection.host=localhost",
        "smpp.windowSize=1", "smpp.primarySmppConnection.connectionTimeout=10000",
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
        "smpp.submitMessageTimeoutMilliseconds=10000" })
@Category(IntegrationTest.class)
public class NetApiSmsEnablerControllerIntegrationTest {

    public static final String SENDER_NAME = "ABC";
    public static final String DESTINATION_ADDRESS_ONE = "Addr1";
    public static final String DESTINATION_ADDRESS_TWO = "Addr2";
    public static final String SENDER_ADDRESS = "tel1234";
    @InjectMocks
    @Autowired
    private NetApiSmsEnablerController controllerUnderTest;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    private MockMvc mockMvc;

    @Autowired
    private ISmsService smsService;

    private String jsonBody = "{\"outboundSMSMessageRequest\": {"
                    + "\"address\": [\"tel:+19585550101\",\"tel:+19585550104\"],"
                    + "\"clientCorrelator\": \"67893\","
                    + "\"outboundSMSTextMessage\": {\"message\": \"Example Text Message\"},"
                    + "\"receiptRequest\": {\"notifyURL\": \"http://application.example.com/notifications/DeliveryInfoNotification\"},"
                    + "\"senderAddress\": \"tel:+19585550151\"," + "\"senderName\": \"MyName\""
                    + "}}";

    private static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                   MediaType.APPLICATION_JSON.getSubtype(),
                                                                   Charset.forName("utf8"));

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerUnderTest)
                                      .setMessageConverters(jsonMessageConverter,
                                                            xmlMessageConverter)
                                      .build();
    }

    @Test
    public void shouldInvokeSmsServiceService() throws Exception {
        final OutboundSMSMessageRequest response = new OutboundSMSMessageRequest();
        response.setSenderName(SENDER_NAME);
        response.getAddresses().add(DESTINATION_ADDRESS_ONE);
        response.getAddresses().add(DESTINATION_ADDRESS_TWO);
        when(smsService.sendSmsMessage(isA(UUID.class),
                                       eq(SENDER_ADDRESS),
                                       isA(OutboundSMSMessageRequest.class))).thenReturn(Observable.just(response));
        final MvcResult mvcResult = mockMvc.perform(post("/outbound/tel1234/requests").accept(JSON_MEDIA_TYPE)
                                                                                      .contentType(MediaType.APPLICATION_JSON)
                                                                                      .content(jsonBody))
                                           .andExpect(request().asyncStarted())
                                           .andExpect(request().asyncResult(notNullValue()))
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(content().contentType(JSON_MEDIA_TYPE))
               .andExpect(jsonPath("$.outboundSMSMessageRequest").exists())
               .andExpect(jsonPath("$.outboundSMSMessageRequest.senderName").value(SENDER_NAME))
               .andExpect(jsonPath("$.outboundSMSMessageRequest.address").isArray())
               .andExpect(jsonPath("$.outboundSMSMessageRequest.resourceURL").exists())
               .andExpect(header().string("Location", containsString("/outbound/tel1234/request")));
    }

    @Test
    public void shouldHandleInvalidAddressException() throws Exception {
        final OutboundSMSMessageRequest response = new OutboundSMSMessageRequest();
        response.setSenderName("ABC");
        response.getAddresses().add("Addr1");
        response.getAddresses().add("Addr2");
        when(smsService.sendSmsMessage(isA(UUID.class),
                                       eq("tel1234"),
                                       isA(OutboundSMSMessageRequest.class))).thenThrow(new InvalidAddressException("Addr1"));
        mockMvc.perform(post("/outbound/tel1234/requests").accept(JSON_MEDIA_TYPE)
                                                          .contentType(MediaType.APPLICATION_JSON)
                                                          .content(jsonBody))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(JSON_MEDIA_TYPE))
               .andExpect(jsonPath("$.requestError").exists())
               .andExpect(jsonPath("$.requestError.serviceException").exists())
               .andExpect(jsonPath("$.requestError.serviceException.messageId").value("SVC0004"))
               .andExpect(jsonPath("$.requestError.serviceException.text").value("No valid addresses provided in message part %1"))
               .andExpect(jsonPath("$.requestError.serviceException.variables").isArray());
    }

    @Test
    public void shouldHandleSendExceptionException() throws Exception {
        final OutboundSMSMessageRequest response = new OutboundSMSMessageRequest();
        response.setSenderName("ABC");
        response.getAddresses().add("Addr1");
        response.getAddresses().add("Addr2");
        when(smsService.sendSmsMessage(isA(UUID.class),
                                       eq("tel1234"),
                                       isA(OutboundSMSMessageRequest.class))).thenThrow(new SendSmppException("Addr1",
                                                                                                              new RuntimeException()));
        mockMvc.perform(post("/outbound/tel1234/requests").accept(JSON_MEDIA_TYPE)
                                                          .contentType(MediaType.APPLICATION_JSON)
                                                          .content(jsonBody))
               .andDo(print())
               .andExpect(status().isInternalServerError())
               .andExpect(content().contentType(JSON_MEDIA_TYPE))
               .andExpect(jsonPath("$.requestError").exists());

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
