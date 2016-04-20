package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.error.Restv091Error;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;
import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.oneapi.rest.v091.TestConfiguration;
import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import com.vennetics.bell.sam.rest.config.converters.XmlConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;
import rx.Observable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Test class for SmsController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@WebIntegrationTest({ "server.port=0", "management.port=0", "spring.cloud.config.enabled=false" })
public class SmsControllerTest {

    private static final MediaType FORM_URLENCODED_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED
                                                                                              .getType(),
                                                                              MediaType.APPLICATION_FORM_URLENCODED
                                                                                              .getSubtype(),
                                                                              Charset.forName("utf8"));

    private static final MediaType XML_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_XML.getType(),
                                                                  MediaType.APPLICATION_XML.getSubtype(),
                                                                  Charset.forName("utf8"));

    public static final String OUTBOUND_REQUEST_URL = SmsController.REST_ONEAPI_SMS_URL.replace(
                    "{serviceIdentifier}",
                    TestConfiguration.SERVICE);
    public static final String ISSUE_WITH_MESSAGE_PART = "Issue with message part %1.";

    private MockMvc mockMvc;

    @Autowired
    @Qualifier("oneapiErrorAsStringAdapter")
    private IErrorAdapter errorAdapter;

    @Autowired
    @Qualifier("oneApiSmsService")
    private ISmsService smsService;

    @Autowired
    private IExceptionTypeMapper exceptionMapper;

    @Autowired
    private SmsController controller;

    @Mock
    private HttpServletRequest mockRequest;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    // Concrete Objects
    private MappingJackson2HttpMessageConverter jsonMessageConverter;

    private MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    private JsonConverter jsonConverterFactory;

    private XmlConverter xmlConverterFactory;

    private StringHttpMessageConverter stringConverter;

    @Before
    public void setup() throws Exception {
        jsonConverterFactory = new JsonConverter();
        jsonMessageConverter = jsonConverterFactory.jsonMessageConverter();
        xmlConverterFactory = new XmlConverter();
        xmlMessageConverter = xmlConverterFactory.xmlMessageConverter();
        stringConverter = new StringHttpMessageConverter();
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(controller, "exceptionMapper", exceptionMapper);
        ReflectionTestUtils.setField(controller, "jsonObjectMapper", jsonObjectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setMessageConverters(stringConverter,
                                                       jsonMessageConverter,
                                                       xmlMessageConverter)
                                 .build();
    }

    @Test
    public void shouldAcceptSendSmsFormUrlEncoded() throws Exception {

        final ResponseEntity<SendSmsResponse> response = TestConfiguration.dummyResponse();

        when(smsService.sendSms(eq(TestConfiguration.SERVICE), any())).thenReturn(Observable.just(
                        response));

        final MvcResult mvcResult = mockMvc.perform(post(OUTBOUND_REQUEST_URL).accept(XML_MEDIA_TYPE)
                                                                              .contentType(FORM_URLENCODED_MEDIA_TYPE)
                                                                              .param(TestConfiguration.ADDRESS_KEY,
                                                                                     TestConfiguration.ADDRESS)
                                                                              .param(TestConfiguration.SENDER_NAME_KEY,
                                                                                     TestConfiguration.SENDER_NAME)
                                                                              .param(TestConfiguration.MESSAGE_KEY,
                                                                                     TestConfiguration.MESSAGE)
                                                                              .param(TestConfiguration.CORRELATOR_KEY,
                                                                                     TestConfiguration.CORRELATOR)
                                                                              .param(TestConfiguration.NOTIFY_URL_KEY,
                                                                                     TestConfiguration.NOTIFY_URL)
                                                                              .param(TestConfiguration.CHARGING_KEY,
                                                                                     TestConfiguration.CHARGING))
                                           .andExpect(request().asyncStarted())
                                           .andExpect(request().asyncResult(notNullValue()))
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    public void shouldAcceptGetSmsDeliveryStatusFormUrlEncoded() throws Exception {

        final ResponseEntity<DeliveryStatusResponse> response = TestConfiguration.buildDeliveryInfoResponse();

        when(smsService.getSmsDeliveryStatus(eq(TestConfiguration.SERVICE),
                                             eq(TestConfiguration.MESSAGE_ID))).thenReturn(
                        Observable.just(response));

        final MvcResult mvcResult = mockMvc.perform(get(OUTBOUND_REQUEST_URL).accept(XML_MEDIA_TYPE)
                                                                             .contentType(FORM_URLENCODED_MEDIA_TYPE)
                                                                             .param(TestConfiguration.MESSAGE_ID_KEY,
                                                                                    TestConfiguration.MESSAGE_ID))
                                           .andExpect(request().asyncStarted())
                                           .andExpect(request().asyncResult(notNullValue()))
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    public void shouldAcceptGetSmsDeliveryStatusFormUrlEncodedException() throws Exception {

        final ResponseEntity<DeliveryStatusResponse> response = TestConfiguration.buildDeliveryInfoResponse();

        when(smsService.getSmsDeliveryStatus(eq(TestConfiguration.SERVICE),
                                             eq(TestConfiguration.MESSAGE_ID))).thenThrow(
                        remoteServiceException(HttpStatus.BAD_REQUEST, serviceExceptionBody()));

        mockMvc.perform(get(OUTBOUND_REQUEST_URL).accept(XML_MEDIA_TYPE)
                                                 .contentType(FORM_URLENCODED_MEDIA_TYPE)
                                                 .param(TestConfiguration.MESSAGE_ID_KEY,
                                                        TestConfiguration.MESSAGE_ID))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeliverErrorAdapter() throws Exception {
        assertThat(controller.getErrorAdapter(), is(errorAdapter));
    }

    @Test
    public void shouldDeliverExceptionClasses() throws Exception {
        final List<Class<?>> exceptionClasses = controller.exceptionClasses();
        assertThat(exceptionClasses, hasSize(1));
        assertThat(exceptionClasses, containsInAnyOrder(Restv091Error.class));
    }

    @Test
    public void shouldPassThroughKnownException() throws Exception {
        final Restv091Error error = new Restv091Error(ISSUE_WITH_MESSAGE_PART);
        when(jsonObjectMapper.readValue(eq(serviceExceptionBody()), any(Class.class))).thenReturn(
                        error);
        ResponseEntity<?> response = controller.handleAllExceptions(remoteServiceException(
                        HttpStatus.BAD_REQUEST,
                        serviceExceptionBody()), mockRequest);
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        Assert.assertThat(response.getBody(), is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>" + ISSUE_WITH_MESSAGE_PART + "</error>"));
    }

    @Test
    public void shouldNotPassThroughUnknownException() throws Exception {
        final ErrorDescription description = new ErrorDescription("code", "type", 400, "message");
        final MappedError error = new MappedError(description);
        when(jsonObjectMapper.readValue(eq(serviceExceptionBody()), any(Class.class))).thenReturn(
                        error);
        when(exceptionMapper.mapException(isA(RuntimeException.class),
                                          eq(mockRequest))).thenReturn(error);
        ResponseEntity<?> response = controller.handleAllExceptions(remoteServiceException(
                        HttpStatus.BAD_REQUEST,
                        unknownExceptionBody()), mockRequest);
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        Assert.assertThat(response.getBody(), is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>message</error>"));
    }

    @Test
    public void shouldHandleExceptionPassingThroughKnownException() throws Exception {
        final ErrorDescription description = new ErrorDescription("code", "type", 400, "message");
        final MappedError error = new MappedError(description);
        when(jsonObjectMapper.readValue(eq(serviceExceptionBody()),
                                        any(Class.class))).thenThrow(new IOException());
        when(exceptionMapper.mapException(isA(RuntimeException.class),
                                          eq(mockRequest))).thenReturn(error);
        ResponseEntity<?> response = controller.handleAllExceptions(remoteServiceException(
                        HttpStatus.BAD_REQUEST,
                        serviceExceptionBody()), mockRequest);
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        Assert.assertThat(response.getBody(), is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>message</error>"));
    }

    private Exception remoteServiceException(final HttpStatus status, final String message) {
        return new RuntimeException(new HttpServerErrorException(status,
                                                                 null,
                                                                 message.getBytes(),
                                                                 null));
    }

    private String unknownExceptionBody() {
        return "{\"unknown\": {\"error\": \"" + ISSUE_WITH_MESSAGE_PART + "\"}}";
    }

    private String serviceExceptionBody() {
        return "{\"Restv091Error\": {\"error\": \"" + ISSUE_WITH_MESSAGE_PART + "\"}}";
    }
}


