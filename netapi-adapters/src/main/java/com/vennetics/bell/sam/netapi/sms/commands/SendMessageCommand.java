package com.vennetics.bell.sam.netapi.sms.commands;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vennetics.microservices.common.core.gateway.adapter.command.AbstractHystrixGatewayCommand;

import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;

/**
 * A HystrixCommand to invoke SendMessage on the internal SMS service
 */

public class SendMessageCommand extends AbstractHystrixGatewayCommand<ResponseEntity<OutboundSMSMessageRequest>> {

    private static final Logger LOG = LoggerFactory.getLogger(SendMessageCommand.class);

    private final String senderAddress;
    private final OutboundSMSMessageRequest request;
    private final RestTemplate template;
    private final String internalServiceName;

    /**
     * Sole constructor.
     * @param internalServiceName
     *      the name of the service to use.
     * @param senderAddress
     *      The address of the sender.
     * @param request
 *      Defines the message to be sent.
     * @param template
*      The {@link RestTemplate} to be used to address the service.
     */
    public SendMessageCommand(final String internalServiceName,
                              final String senderAddress,
                              final OutboundSMSMessageRequest request,
                              final RestTemplate template) {
        super(internalServiceName, "SendMessage");

        this.internalServiceName = internalServiceName;
        this.senderAddress = senderAddress;
        this.request = request;
        this.template = template;
    }

    @Override
    protected ResponseEntity<OutboundSMSMessageRequest> runCommand() {
        LOG.debug("SendMessageCommand senderAddress:{} request:{}", senderAddress, request);

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        final HttpEntity<OutboundSMSMessageRequest> entity = new HttpEntity<>(request, headers);
        final ResponseEntity<OutboundSMSMessageRequest> response =
                template.postForEntity(buildUrl(), entity, OutboundSMSMessageRequest.class);

        LOG.debug("SendMessageCommand result {}", response);
        return response;
    }

    private String buildUrl() {
        return String.format("http://%s/outbound/%s/requests", internalServiceName, senderAddress);
    }
}
