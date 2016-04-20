package com.vennetics.bell.sam.smsx.enabler;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.MessageResponse;
import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import com.vennetics.bell.sam.rest.config.converters.XmlConverter;
import com.vennetics.bell.sam.smsx.enabler.service.ISmsService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SmsXEnablerControllerRestDocTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation(
                    "build/generated-snippets");

    public static final String SENDER_NAME_VALUE = "short:12345";
    public static final String DESTINATION_ADDRESS_VALUE = "tel:+123456789";
    public static final String MESSAGE_TEXT_VALUE = "Hi there";
    public static final String DELIVERED_TO_TERMINAL = "DeliveredToTerminal";
    public static final String CORRELATOR_VALUE = "123456";
    public static final String NOTIFY_URL_VALUE = "http://examples.com/callback";

    // Object under test
    @InjectMocks
    private SmsXEnablerController controllerUnderTest;

    // Mock Objects
    @Mock
    private ISmsService smsService;

    private MockMvc mockMvc;

    // Concrete Objects
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    private JsonConverter jsonConverterFactory;

    private XmlConverter xmlConverterFactory;

    private static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                   MediaType.APPLICATION_JSON.getSubtype(),
                                                                   Charset.forName("utf8"));
    private static UUID requestId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        jsonConverterFactory = new JsonConverter();
        jsonMessageConverter = jsonConverterFactory.jsonMessageConverter();
        xmlConverterFactory = new XmlConverter();
        xmlMessageConverter = xmlConverterFactory.xmlMessageConverter();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerUnderTest)
                                      .setMessageConverters(jsonMessageConverter,
                                                            xmlMessageConverter)
                                      .apply(documentationConfiguration(this.REST_DOCUMENTATION))
                                      .build();
    }

    @Test
    public void shouldInvokeSmsServiceServiceForSendSms() throws Exception {
        when(smsService.sendSmsMessage(isA(UUID.class), isA(SendSmsRequest.class))).thenReturn(
                        Observable.just(null));
        final MvcResult mvcResult = mockMvc.perform(post(SmsXEnablerController.OUTBOUND_REQUESTS_PATH)
                                                                    .accept(JSON_MEDIA_TYPE)
                                                                    .contentType(JSON_MEDIA_TYPE)
                                                                    .param(SmsXEnablerController.SENDER_NAME,
                                                                           SENDER_NAME_VALUE)
                                                                    .param(SmsXEnablerController.ADDRESS,
                                                                           DESTINATION_ADDRESS_VALUE)
                                                                    .param(SmsXEnablerController.CORRELATOR,
                                                                           CORRELATOR_VALUE)
                                                                    .param(SmsXEnablerController.NOTIFY_URL,
                                                                           NOTIFY_URL_VALUE)
                                                                    .param(SmsXEnablerController.MESSAGE,
                                                                           MESSAGE_TEXT_VALUE))
                                           .andExpect(request().asyncStarted())
                                           .andExpect(request().asyncResult(notNullValue()))
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(content().contentType(JSON_MEDIA_TYPE))
               .andExpect(jsonPath("$.SmsResponse.messageId").exists())
               .andDo(document("sendSmsMessage",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               requestParameters(parameterWithName(SmsXEnablerController.SENDER_NAME)
                                                                 .description("(Required) Name of the sender to appear on the terminal"),
                                                 parameterWithName(SmsXEnablerController.ADDRESS).description(
                                                                 "(Required) List of addresses to which the SMS will be sent"),
                                                 parameterWithName(SmsXEnablerController.MESSAGE).description(
                                                                 "(Required) The message to be sent"),
                                                 parameterWithName(SmsXEnablerController.CORRELATOR)
                                                                 .description("(Optional) Correlator for inclusion in a delivery receipt notification"),
                                                 parameterWithName(SmsXEnablerController.NOTIFY_URL)
                                                                 .description("(Optional) URL to notify the application for delivery receipts")),
                               responseFields(fieldWithPath("SmsResponse").description(
                                               "Wrapper for the Response"),
                                              fieldWithPath("SmsResponse.messageId").description(
                                                              "Unique id of this request. Can be used to query the delivery status of the messages sent in this request."))));
    }

    @Test
    public void shouldInvokeSmsServiceServiceForGetDeliveryInfo() throws Exception {
        when(smsService.getDeliveryStatus(requestId.toString())).thenReturn(Observable.just(
                        createDefaultDeliveryStatusResponse()));
        final MvcResult mvcResult = mockMvc.perform(get(SmsXEnablerController.OUTBOUND_REQUESTS_PATH)
                                                                    .accept(JSON_MEDIA_TYPE)
                                                                    .contentType(JSON_MEDIA_TYPE)
                                                                    .param(SmsXEnablerController.MESSAGE_ID,
                                                                           requestId.toString()))
                                           .andExpect(request().asyncStarted())
                                           .andExpect(request().asyncResult(notNullValue()))
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_MEDIA_TYPE))
               .andExpect(jsonPath("$.DeliveryStatusResponse.Message[0].address").value(
                               DESTINATION_ADDRESS_VALUE))
               .andExpect(jsonPath("$.DeliveryStatusResponse.Message[0].status").value(
                               DELIVERED_TO_TERMINAL))
               .andDo(document("getDeliveryStatus",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               requestParameters(parameterWithName(SmsXEnablerController.MESSAGE_ID)
                                                                 .description("(Required) Identifies a specific SMS delivery request")),
                               responseFields(fieldWithPath("DeliveryStatusResponse").description(
                                               "Wrapper for the Response"),
                                              fieldWithPath("DeliveryStatusResponse.Message").description(
                                                              "List of Status for each destination address in original request"),
                                              fieldWithPath("DeliveryStatusResponse.Message[].address")
                                                              .description("Destination Address"),
                                              fieldWithPath("DeliveryStatusResponse.Message[].status")
                                                              .description("Delivery Status"))));
    }

    private DeliveryStatusResponse createDefaultDeliveryStatusResponse() {
        final DeliveryStatusResponse deliveryStatusResponse = new DeliveryStatusResponse();
        final MessageResponse messageResponse = new MessageResponse();
        messageResponse.setStatus(DELIVERED_TO_TERMINAL);
        messageResponse.setAddress(DESTINATION_ADDRESS_VALUE);
        deliveryStatusResponse.getMessageResponses().add(messageResponse);
        return deliveryStatusResponse;
    }
}
