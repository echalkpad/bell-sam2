package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import static com.vennetics.bell.sam.oneapi.rest.v091.sms.ErrorConstants.ERROR_TAG_END;
import static com.vennetics.bell.sam.oneapi.rest.v091.sms.ErrorConstants.ERROR_TAG_START;
import static com.vennetics.bell.sam.oneapi.rest.v091.sms.ErrorConstants.XML_DECLARATION;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.vennetics.bell.sam.error.Restv091Error;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.registry.ServiceConstants;
import com.vennetics.bell.sam.rest.controller.PassThruExceptionHandlingRestController;

import rx.Observable;

/**
 * Spring REST Controller for OneAPI SMS.
 * This implementation simply forwards to the internal SMS service.
 */

@RestController("oneApiSmsController")
@RequestMapping(SmsController.REST_ONEAPI_SMS_URL)
public class SmsController extends PassThruExceptionHandlingRestController {

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    protected static final String REST_ONEAPI_SMS_URL =
                    ServiceConstants.ONEAPI_SMS_URL + "/{serviceIdentifier}/sms";

    @Autowired
    @Qualifier("oneApiSmsService")
    private ISmsService smsService;

    @Autowired
    @Qualifier("oneapiErrorAsStringAdapter")
    private IErrorAdapter errorAdapter;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleAllExceptions(final Exception ex,
                                              final HttpServletRequest request) {

        logger.debug("Exception {}/{} request={}", ex, ex.getMessage(), request);

        final ResponseEntity entity = super.handleAllExceptions(ex, request);

        if (entity.getBody() != null && entity.getBody() instanceof Restv091Error) {

            final Restv091Error responseClass = (Restv091Error) entity.getBody();

            return new ResponseEntity(buildStringResponse(responseClass.getErrorMessage()),
                                      entity.getStatusCode());
        }

        return entity;
    }

    /**
     * POST endpoint used to create an outgoing sms message request.
     *
     * @param params            The parameters that make up the sendSms Request.
     * @param serviceIdentifier The identifier of the internal service to use.
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<SendSmsResponse>> sendSms(
                    @RequestParam final MultiValueMap<String, String> params,
                    @PathVariable final String serviceIdentifier) {

        final Observable<ResponseEntity<SendSmsResponse>> observable = forwardRequest(
                        serviceIdentifier,
                        params);
        final DeferredResult<ResponseEntity<SendSmsResponse>> deferred = new DeferredResult<>();
        observable.subscribe(deferred::setResult, deferred::setErrorResult);
        return deferred;
    }

    /**
     * GET endpoint used to retrieve the delivery status for a particular sendSms instance.
     *
     * @param messageId         The sendSms reference whose status is of interest.
     * @param serviceIdentifier The internal service for which delivery status is to be retrieved.
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<DeliveryStatusResponse>> getSmsDeliveryStatus(
                    @RequestParam final String messageId,
                    @PathVariable final String serviceIdentifier) {
        final Observable<ResponseEntity<DeliveryStatusResponse>> observable = smsService.getSmsDeliveryStatus(
                        serviceIdentifier,
                        messageId);
        final DeferredResult<ResponseEntity<DeliveryStatusResponse>> deferred = new DeferredResult<>();
        observable.subscribe(deferred::setResult, deferred::setErrorResult);
        return deferred;
    }

    private Observable<ResponseEntity<SendSmsResponse>> forwardRequest(final String serviceIdentifier,
                                                                       final MultiValueMap<String, String> params) {
        return smsService.sendSms(serviceIdentifier, params);
    }

    @Override
    protected List<Class<?>> exceptionClasses() {
        return Arrays.asList(Restv091Error.class);
    }

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }

    private static String buildStringResponse(final String string) {
        final StringBuilder sb = new StringBuilder(160);
        return sb.append(XML_DECLARATION)
                 .append(ERROR_TAG_START)
                 .append(string)
                 .append(ERROR_TAG_END)
                 .toString();
    }


}
