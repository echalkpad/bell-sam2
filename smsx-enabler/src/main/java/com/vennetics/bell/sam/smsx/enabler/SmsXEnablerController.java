package com.vennetics.bell.sam.smsx.enabler;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.rest.controller.ExceptionHandlingOneapiRestController;
import com.vennetics.bell.sam.smsx.enabler.service.ISmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.util.List;
import java.util.UUID;

/**
 * Controller that exposes REST API for SMS-X Enabler
 */
@RestController
@RequestMapping("/")
public class SmsXEnablerController extends ExceptionHandlingOneapiRestController {

    public static final String OUTBOUND_REQUESTS_PATH = "/outbound/requests";
    public static final String SENDER_NAME = "senderName";
    public static final String MESSAGE = "message";
    public static final String ADDRESS = "address";
    public static final String NOTIFY_URL = "notifyUrl";
    public static final String CORRELATOR = "correlator";
    public static final String MESSAGE_ID = "messageId";

    @Autowired
    private ISmsService smsService;

    @RequestMapping(method = RequestMethod.POST, value = OUTBOUND_REQUESTS_PATH)
    public DeferredResult<ResponseEntity<SendSmsResponse>> sendSmsMessage(
                    @RequestParam(name = SENDER_NAME) final String senderAddress,
                    @RequestParam(name = MESSAGE) final String message,
                    @RequestParam(name = ADDRESS) final List<String> addresses,
                    @RequestParam(name = NOTIFY_URL, required = false) final String notifyUrl,
                    @RequestParam(name = CORRELATOR, required = false) final String correlator) {
        final UUID externalRequestId = UUID.randomUUID();
        final SendSmsRequest outboundMessage = new SendSmsRequest(senderAddress,
                                                                  message,
                                                                  addresses,
                                                                  notifyUrl,
                                                                  correlator);
        final Observable<Void> observable = smsService.sendSmsMessage(externalRequestId,
                                                                      outboundMessage);
        final DeferredResult<ResponseEntity<SendSmsResponse>> deferred = new DeferredResult<>();
        final SendSmsResponse response = new SendSmsResponse();
        response.setMessageId(externalRequestId.toString());
        observable.subscribe(outBoundMessage -> deferred.setResult(new ResponseEntity<>(response,
                                                                                        HttpStatus.CREATED)),
                             deferred::setErrorResult);
        return deferred;
    }

    @RequestMapping(method = RequestMethod.GET, value = OUTBOUND_REQUESTS_PATH)
    public DeferredResult<ResponseEntity<DeliveryStatusResponse>> getDeliveryStatus(
                    @RequestParam(name = MESSAGE_ID) final String messageId) {
        final Observable<DeliveryStatusResponse> observable = smsService.getDeliveryStatus(messageId);
        final DeferredResult<ResponseEntity<DeliveryStatusResponse>> deferred = new DeferredResult<>();
        observable.subscribe(outBoundMessage -> deferred.setResult(new ResponseEntity<>(
                        outBoundMessage,
                        HttpStatus.OK)), deferred::setErrorResult);
        return deferred;

    }
}
