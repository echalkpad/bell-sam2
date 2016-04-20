package com.vennetics.bell.sam.netapi.sms.enabler;

import com.vennetics.bell.sam.netapi.sms.enabler.service.ISmsService;
import com.vennetics.bell.sam.rest.controller.ExceptionHandlingNetapiRestController;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriComponentsBuilder;
import rx.Observable;

import java.net.URI;
import java.util.UUID;

/**
 * @author markcorkery
 */
@RestController
@RequestMapping("/")
public class NetApiSmsEnablerController extends ExceptionHandlingNetapiRestController {

    @Value("${enabler.uri.basePath:root}")
    private String basePath;

    @Autowired
    private ISmsService smsService;

    private static final Logger LOG = LoggerFactory.getLogger(NetApiSmsEnablerController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/outbound/{senderAddress}/requests")
    public DeferredResult<ResponseEntity<OutboundSMSMessageRequest>> sendSmsMessage(
                    @PathVariable final String senderAddress,
                    @RequestBody final OutboundSMSMessageRequest message,
                    final UriComponentsBuilder ucBuilder) {
        LOG.debug(">>> sendSmsMessage({}, {})" + senderAddress, message);
        final UUID externalRequestId = UUID.randomUUID();
        final Observable<OutboundSMSMessageRequest> observable = smsService.sendSmsMessage(
                        externalRequestId,
                        senderAddress,
                        message);
        final DeferredResult<ResponseEntity<OutboundSMSMessageRequest>> deferred = new DeferredResult<>();

        observable.subscribe(outBoundMessage -> {
            final URI resourceUri = ucBuilder.path(
                            "{base}/outbound/{senderAddress}/requests/{requestId}")
                                             .buildAndExpand(basePath,
                                                             senderAddress,
                                                             externalRequestId)
                                             .toUri();
            final HttpHeaders headers = new HttpHeaders();
            headers.setLocation(resourceUri);
            outBoundMessage.setResourceURL(resourceUri.toString());
            deferred.setResult(new ResponseEntity<OutboundSMSMessageRequest>(outBoundMessage,
                                                                             headers,
                                                                             HttpStatus.CREATED));
        }, deferred::setErrorResult);

        return deferred;
    }

}
