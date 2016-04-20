package com.vennetics.bell.sam.oneapi.rest.v091.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.microservices.common.core.gateway.adapter.command.AbstractHystrixGatewayCommand;

/**
 * A HystrixCommand to invoke HasFeatureCodes on the internal SUBX service
 */

public class GetSmsDeliveryStatusCommand extends AbstractHystrixGatewayCommand<ResponseEntity<DeliveryStatusResponse>> {

    private static final Logger LOG = LoggerFactory.getLogger(GetSmsDeliveryStatusCommand.class);

    public static final ParameterizedTypeReference<DeliveryStatusResponse> DELIVERINFO_LIST_TYPE = new ParameterizedTypeReference<DeliveryStatusResponse>() { };

    private final String messageId;
    private final RestTemplate template;
    private final String serviceIdentifier;

    /**
     *
     * @param serviceIdentifier
     *  The service for which delivery status is to be retrieved
     * @param messageId
     *   The sendSms reference whose status is of interest.
     * @param template
     *   The {@link RestTemplate} to be used to address the service.
     */
    public GetSmsDeliveryStatusCommand(final String serviceIdentifier,
                                       final String messageId,
                                       final RestTemplate template) {
        super(serviceIdentifier, "GetSmsDeliveryStatus");
        this.serviceIdentifier = serviceIdentifier;
        this.messageId = messageId;
        this.template = template;
    }

    @Override
    protected ResponseEntity<DeliveryStatusResponse> runCommand() {
        LOG.debug("GetSmsDeliveryStatusCommand messageId:{}", messageId);

        final ResponseEntity<DeliveryStatusResponse> responseEntity = template.exchange(buildUrl(), HttpMethod.GET, null, DELIVERINFO_LIST_TYPE);

        LOG.debug("GetSmsDeliveryStatusCommand result {}", responseEntity);
        return responseEntity;
    }

    private String buildUrl() {
        return String.format("http://%s/outbound/requests?messageId=%s", serviceIdentifier, messageId);
    }
}
