package com.vennetics.bell.sam.netapi.sms;

import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.registry.ServiceConstants;
import com.vennetics.bell.sam.rest.controller.PassThruExceptionHandlingRestController;
import generated.oma.xml.rest.netapi.common._1.PolicyException;
import generated.oma.xml.rest.netapi.common._1.RequestError;
import generated.oma.xml.rest.netapi.common._1.ServiceException;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Spring REST Controller for Netapi SMS.
 * This implementation simply forwards to the internal SMS service.
 */

@RestController("netApiSmsController")
@RequestMapping(SmsController.REST_NETAPI_SMS_URL)
public class SmsController extends PassThruExceptionHandlingRestController {

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    protected static final String REST_NETAPI_SMS_URL =
                    ServiceConstants.NETAPI_SMS_URL + "/{serviceIdentifier}/smsMessaging/v1";

    @Autowired
    @Qualifier("netApiSmsService")
    private ISmsService smsService;

    @Autowired
    @Qualifier("netapiErrorAdapter")
    private IErrorAdapter errorAdapter;

    /**
     * POST endpoint used to create an outgoing message request.
     *
     * @param senderAddress     Address of the sender
     * @param serviceIdentifier the identifier of the internal service to use
     * @param message           Defines the message to be sent
     * @return an {@link OutboundSMSMessageRequest} including delivery information.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/outbound/{senderAddress}/requests")
    public ResponseEntity<OutboundSMSMessageRequest> sendSmsMessage(
                    @PathVariable final String senderAddress,
                    @PathVariable final String serviceIdentifier,
                    @RequestBody final OutboundSMSMessageRequest message,
                    final HttpServletRequest request) {

        logger.debug("sendSmsMessage senderAddress:{} service:{}",
                     senderAddress,
                     serviceIdentifier);

        final ResponseEntity<OutboundSMSMessageRequest> internalResponse = forwardRequest(
                        serviceIdentifier,
                        senderAddress,
                        message);

        final ResponseEntity<OutboundSMSMessageRequest> response = adaptResponseFromInternalToExternal(
                        request,
                        internalResponse);

        logger.debug("sendSmsMessage response:{}", response);
        return response;
    }

    private static ResponseEntity<OutboundSMSMessageRequest> adaptResponseFromInternalToExternal(
                    final HttpServletRequest request,
                    final ResponseEntity<OutboundSMSMessageRequest> internalResponse) {

        final HttpHeaders headers = externalResponseHeaders(internalResponse.getHeaders(),
                                                            request.getContentType());
        return new ResponseEntity<>(internalResponse.getBody(),
                                    headers,
                                    internalResponse.getStatusCode());
    }

    private static HttpHeaders externalResponseHeaders(final HttpHeaders headers,
                                                       final String contentType) {
        final HttpHeaders newHeaders = new HttpHeaders();
        headers.entrySet()
               .stream()
               .forEach(entry -> newHeaders.put(entry.getKey(), entry.getValue()));
        newHeaders.put("Content-Type", Collections.singletonList(contentType));
        return newHeaders;
    }

    private ResponseEntity<OutboundSMSMessageRequest> forwardRequest(final String serviceIdentifier,
                                                                     final String senderAddress,
                                                                     final OutboundSMSMessageRequest message) {
        return smsService.sendMessage(serviceIdentifier, senderAddress, message);
    }

    @Override
    protected List<Class<?>> exceptionClasses() {
        return Arrays.asList(ServiceException.class, PolicyException.class, RequestError.class);
    }

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }

}
