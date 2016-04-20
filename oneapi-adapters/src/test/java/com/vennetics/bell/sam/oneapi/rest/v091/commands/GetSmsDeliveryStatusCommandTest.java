package com.vennetics.bell.sam.oneapi.rest.v091.commands;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;

import static org.mockito.Matchers.isNull;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.oneapi.rest.v091.TestConfiguration;


@RunWith(MockitoJUnitRunner.class)
public class GetSmsDeliveryStatusCommandTest {

    private static final Logger LOG = LoggerFactory.getLogger(GetSmsDeliveryStatusCommandTest.class);

    @Mock private RestTemplate mockTemplate;

    @Test
    public void shouldGetSmsDeliveryStatus() throws Exception {
        final String expectedUrl = String.format("http://oneapi-sms-enabler/outbound/requests?messageId=%s", TestConfiguration.MESSAGE_ID);
        LOG.debug(expectedUrl);
        ResponseEntity<DeliveryStatusResponse> response = TestConfiguration.buildDeliveryInfoResponse();
        when(mockTemplate.exchange(eq(expectedUrl), eq(HttpMethod.GET), isNull(HttpEntity.class), same(GetSmsDeliveryStatusCommand.DELIVERINFO_LIST_TYPE))).thenReturn(response);

        final GetSmsDeliveryStatusCommand command = new GetSmsDeliveryStatusCommand("oneapi-sms-enabler", TestConfiguration.MESSAGE_ID,  mockTemplate);

        assertThat(command.execute(), sameInstance(response));
    }
}
