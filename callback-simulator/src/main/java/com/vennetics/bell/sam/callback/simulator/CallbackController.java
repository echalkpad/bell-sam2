package com.vennetics.bell.sam.callback.simulator;

import com.vennetics.bell.sam.smsx.enabler.notification.DeliveryStatus;
import generated.oma.xml.rest.netapi.sms._1.DeliveryInfoNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 17/02/2016.
 */
@RestController
@RequestMapping("/")
public class CallbackController {

    private final AtomicLong callCount = new AtomicLong();

    private static final Logger logger = LoggerFactory.getLogger(CallbackController.class);

    @RequestMapping(method = RequestMethod.POST, path = "/oneapi/deliveryreceipt")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void oneApiDeliveryReceipt(
                    @RequestParam(name = "correlator", required = false) final String correlator,
                    @RequestBody final DeliveryStatus delivery) {
        logger.debug(">>> oneApiDeliveryReceipt({}, {})", correlator, delivery);
        logger.debug("Calls made: {}", callCount.incrementAndGet());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/netapi/deliveryreceipt")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void netApiDeliveryReceipt(@RequestBody final DeliveryInfoNotification delivery) {
        logger.debug(">>> netApiDeliveryReceipt({})", delivery);
        logger.debug("Calls made: {}", callCount.incrementAndGet());
    }

    @RequestMapping(method = RequestMethod.GET, path = "callcount")
    public Long callCount() {
        return callCount.longValue();
    }

    @RequestMapping(method = RequestMethod.POST, path = "resetcount")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void resetCount() {
        logger.debug("Calls before reset: {}", callCount.longValue());
        callCount.set(0L);
    }

}
