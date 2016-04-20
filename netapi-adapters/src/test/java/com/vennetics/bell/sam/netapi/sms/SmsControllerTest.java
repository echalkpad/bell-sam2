package com.vennetics.bell.sam.netapi.sms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import generated.oma.xml.rest.netapi.common._1.RequestError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.netapi.TestConfiguration;

import generated.oma.xml.rest.netapi.common._1.PolicyException;
import generated.oma.xml.rest.netapi.common._1.ServiceException;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;

/**
 * The Test class for SubxController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@WebIntegrationTest({ "server.port=0", "management.port=0", "spring.cloud.config.enabled=false"})
public class SmsControllerTest {

    private static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private static final MediaType XML_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_XML.getType(),
            MediaType.APPLICATION_XML.getSubtype(), Charset.forName("utf8"));

    public static final String OUTBOUND_REQUEST_URL = SmsController.REST_NETAPI_SMS_URL.replace("{serviceIdentifier}", "SVCID") + "/outbound/tel1234/requests";

    private MockMvc mockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    @Autowired
    @Qualifier("netapiErrorAdapter")
    private IErrorAdapter errorAdapter;

    @Autowired
    private ISmsService smsService;

    @Autowired
    private IExceptionTypeMapper exceptionTypeMapper;


    @Autowired
    private SmsController controller;

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(jsonMessageConverter, xmlMessageConverter)
                .build();
    }

    @Test
    public void shouldAcceptJson() throws Exception {

        final ResponseEntity<OutboundSMSMessageRequest> response = dummyJsonResponse();

        when(smsService.sendMessage(eq("SVCID"), eq("tel1234"), isA(OutboundSMSMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post(OUTBOUND_REQUEST_URL)
                .accept(JSON_MEDIA_TYPE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonBody())).andDo(print())
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.outboundSMSMessageRequest.senderName").value("ABC"))
               .andExpect(jsonPath("$.outboundSMSMessageRequest.address").isArray());
    }

    @Test
    public void shouldAcceptXml() throws Exception {

        final ResponseEntity<OutboundSMSMessageRequest> response = dummyJsonResponse();

        when(smsService.sendMessage(eq("SVCID"), eq("tel1234"), isA(OutboundSMSMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post(OUTBOUND_REQUEST_URL)
                .accept(XML_MEDIA_TYPE)
                .contentType(MediaType.APPLICATION_XML)
                .content(xmlBody())).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(xpath("//outboundSMSMessageRequest").exists())
                .andExpect(xpath("//outboundSMSMessageRequest/senderName").string("ABC"))
                .andExpect(xpath("//outboundSMSMessageRequest/address").nodeCount(2));
    }

    @Test
    public void shouldDeliverErrorAdapter() throws Exception {
        assertThat(controller.getErrorAdapter(), is(errorAdapter));
    }

    @Test
    public void shouldDeliverExceptionClasses() throws Exception {
        final List<Class<?>> exceptionClasses = controller.exceptionClasses();
        assertThat(exceptionClasses, containsInAnyOrder(ServiceException.class, PolicyException.class,
                                                        RequestError.class));
    }

    private Exception remoteServiceException() {
        return new RuntimeException();
    }

    private Exception remoteServiceException(final HttpStatus status, final String messageId) {
        return new RuntimeException(new HttpServerErrorException(status, null, serviceExceptionBody(messageId).getBytes(), null));
    }

    private String serviceExceptionBody(final String messageId) {
        return "{\"ServiceException\":{\"messageId\":\"SAM-SVC-9999\"}}";
    }

    private String jsonBody() {
        return sourceWithLines(
                "{\"outboundSMSMessageRequest\": {",
                "  \"address\": [",
                "    \"tel:+19585550101\",",
                "    \"tel:+19585550104\"",
                "  ],",
                "  \"clientCorrelator\": \"67893\",",
                "  \"outboundSMSTextMessage\": {\"message\": \"Example Text Message\"},",
                "  \"receiptRequest\": {\"notifyURL\": \"http://application.example.com/notifications/DeliveryInfoNotification\"},",
                "  \"senderAddress\": \"tel:+19585550151\",",
                "  \"senderName\": \"MyNameJSON\"",
                "}}"
        );
    }

    private String xmlBody() {
        return sourceWithLines(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<sms:outboundSMSMessageRequest xmlns:sms=\"urn:oma:xml:rest:netapi:sms:1\">",
                "    <address>tel:+19585550101</address>",
                "    <address>tel:+19585550104</address>",
                "    <senderAddress>tel:+19585550151</senderAddress>",
                "    <senderName>MyNameXML</senderName>",
                "    <receiptRequest>",
                "        <notifyURL>http://application.example.com/notifications/DeliveryInfoNotification</notifyURL>",
                "    </receiptRequest>",
                "    <outboundSMSTextMessage>",
                "        <message>Example Text Message </message>",
                "    </outboundSMSTextMessage>",
                "    <clientCorrelator>67893</clientCorrelator>",
                "</sms:outboundSMSMessageRequest>"
        );
    }

    private String sourceWithLines(final String ... lines) {
        return Arrays.stream(lines).collect(Collectors.joining("\n"));
    }

    private ResponseEntity<OutboundSMSMessageRequest> dummyJsonResponse() {
        final OutboundSMSMessageRequest response = new OutboundSMSMessageRequest();
        response.setSenderName("ABC");
        response.getAddresses().add("Addr1");
        response.getAddresses().add("Addr2");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<OutboundSMSMessageRequest>(response, headers, HttpStatus.CREATED);
    }
}


