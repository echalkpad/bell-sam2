package com.vennetics.bell.sam.oneapi.rest.v091;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.error.adapters.OneapiErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.MessageResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.oneapi.rest.v091.config.OneApiAdaptersConfig;
import com.vennetics.bell.sam.oneapi.rest.v091.sms.ISmsService;
import com.vennetics.bell.sam.oneapi.rest.v091.sms.SmsService;
import com.vennetics.bell.sam.rest.config.RestConfig;
import generated.oma.xml.rest.netapi.sms._1.DeliveryStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Mockito.mock;

/**
 * Dummy mainline to define configuration for web integration testing.
 * Replace SmsService bean with a mock. TODO SJ Determine if there is a better way of doing this...
 */

@SpringBootApplication
@ComponentScan(basePackages = { "com.vennetics.bell.sam.oneapi" },
                excludeFilters = {
                                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SmsService.class),
                                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = OneApiAdaptersConfig.class) })
@Import({ SamErrorsConfig.class, RestConfig.class, CoreErrorsConfig.class })
public class TestConfiguration {

    public static final String ADDRESS = "tel:+442890269151";
    public static final String CORRELATOR = "correlator";
    public static final String MESSAGE = "message";
    public static final String NOTIFY_URL = "http://host:9999";
    public static final String SENDER_NAME = "senderName";
    public static final String CHARGING = "Charge 10c";
    public static final String MESSAGE_ID = "messageId";
    public static final String ADDRESS_KEY = "address";
    public static final String MESSAGE_KEY = "message";
    public static final String CORRELATOR_KEY = "correlator";
    public static final String NOTIFY_URL_KEY = "notifyUrl";
    public static final String CHARGING_KEY = "charging";
    public static final String SENDER_NAME_KEY = "senderName";
    public static final String MESSAGE_ID_KEY = "messageId";
    public static final String SERVICE = "oneapi-sms-enabler";

    public static void main(final String[] args) {
        SpringApplication.run(TestConfiguration.class, args);
    }

    @Bean(name = "oneApiSmsService")
    public ISmsService smsService() {
        return mock(ISmsService.class);
    }

    @Bean
    public IExceptionTypeMapper exceptionMapper() {
        return mock(IExceptionTypeMapper.class);
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        return mock(ObjectMapper.class);
    }

    @Bean(name = "oneapiErrorAdapter")
    public OneapiErrorAdapter errorAdapter() {
        return mock(OneapiErrorAdapter.class);
    }

    public static final MultiValueMap<String, String> createMap() {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add(ADDRESS_KEY, ADDRESS);
        map.add(MESSAGE_KEY, MESSAGE);
        map.add(SENDER_NAME_KEY, SENDER_NAME);
        map.add(NOTIFY_URL_KEY, NOTIFY_URL);
        map.add(CHARGING_KEY, CHARGING);
        map.add(CORRELATOR_KEY, CORRELATOR);
        return map;
    }

    public static final ResponseEntity<SendSmsResponse> dummyResponse() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        final SendSmsResponse response = new SendSmsResponse();
        response.setMessageId(MESSAGE_ID);
        return new ResponseEntity<SendSmsResponse>(response, headers, HttpStatus.CREATED);
    }

    public static final ResponseEntity<DeliveryStatusResponse> buildDeliveryInfoResponse() {
        final DeliveryStatusResponse deliveryInfo = new DeliveryStatusResponse();
        final MessageResponse messageResponse = new MessageResponse();
        messageResponse .setAddress(TestConfiguration.ADDRESS);
        final DeliveryStatus status = DeliveryStatus.DELIVERED_TO_TERMINAL;
        messageResponse.setStatus(status.value());
        deliveryInfo.getMessageResponses().add(messageResponse);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<DeliveryStatusResponse>(deliveryInfo, headers, HttpStatus.OK);
    }
}
