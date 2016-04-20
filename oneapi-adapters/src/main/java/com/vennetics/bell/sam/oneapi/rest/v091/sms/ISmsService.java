package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import rx.Observable;

/**
 * Defines an interface to the internal SMS REST service, for use by the Oneapi adapter.
 */

public interface ISmsService {
    /**
     *
     * @param serviceIdentifier
     *   The service to forward the message to.
     * @param params
     *   The parameters that constitute the sms is to be sent.
     * @return
     */
    Observable<ResponseEntity<SendSmsResponse>> sendSms(String serviceIdentifier,
                                                        MultiValueMap<String, String> params);

    /**
     *
     * @param serviceIdentifier
     *   The internal service for which delivery status is to be retrieved.
     * @param messageId
     *   The sendSms reference whose status is of interest.
     * @return
     */
    Observable<ResponseEntity<DeliveryStatusResponse>> getSmsDeliveryStatus(String serviceIdentifier,
                                                                            String messageId);
}
