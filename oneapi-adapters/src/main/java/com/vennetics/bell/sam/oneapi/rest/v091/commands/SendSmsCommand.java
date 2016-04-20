package com.vennetics.bell.sam.oneapi.rest.v091.commands;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.microservices.common.core.gateway.adapter.command.AbstractHystrixGatewayCommand;

/**
 * A HystrixCommand to invoke SendMessage on the internal SMS service
 */

public class SendSmsCommand extends AbstractHystrixGatewayCommand<ResponseEntity<SendSmsResponse>> {

    private static final Logger LOG = LoggerFactory.getLogger(SendSmsCommand.class);

    private final RestTemplate template;
    private final String internalServiceName;
    private final MultiValueMap<String, String> map;

    /**
     *
     * @param internalServiceName
     *   The internal service to forward the message to.
     * @param map
     *   The parameters that constitute the sms is to be sent.
     * @param template
     *   The {@link RestTemplate} to be used to address the service.
     */
    public SendSmsCommand(final String internalServiceName,
                          final MultiValueMap<String, String> map,
                          final RestTemplate template) {
        super(internalServiceName, "SendSms");
        this.internalServiceName = internalServiceName;
        this.map = map;
        this.template = template;
    }

    @Override
    protected ResponseEntity<SendSmsResponse> runCommand() {
        LOG.debug("SendMessageCommand Parameters:{}", map);
        final ResponseEntity<SendSmsResponse> response = template.postForEntity(buildUrl(), buildEntity(), SendSmsResponse.class);
        LOG.debug("SendMessageCommand result {}", response);
        return response;
    }

    private HttpEntity<MultiValueMap<String, String>> buildEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(map, headers);
    }

    private String buildUrl() {
        return String.format("http://%s/outbound/requests", internalServiceName);
    }
}
