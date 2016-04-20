package com.vennetics.bell.sam.callback.simulator;

import com.vennetics.bell.sam.smsx.enabler.notification.DeliveryStatus;
import generated.oma.xml.rest.netapi.sms._1.DeliveryInfoNotification;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created on 18/02/2016.
 */
public class CallbackControllerTest {

    // Object Under Test
    private CallbackController objectUnderTest;

    // Concrete Objects
    private DeliveryStatus deliveryStatus;
    private DeliveryInfoNotification deliveryInfoNotification;

    @Before
    public void setUp() throws Exception {
        objectUnderTest = new CallbackController();
        deliveryStatus = new DeliveryStatus();
        deliveryInfoNotification = new DeliveryInfoNotification();
    }

    @Test
    public void testOneApiDeliveryReceiptIncrementsCallCounter() throws Exception {
        assertThat(objectUnderTest.callCount(), is(0L));
        objectUnderTest.oneApiDeliveryReceipt(null, deliveryStatus);
        assertThat(objectUnderTest.callCount(), is(1L));
    }

    @Test
    public void testNetApiDeliveryReceiptIncrementsCallCounter() throws Exception {
        assertThat(objectUnderTest.callCount(), is(0L));
        objectUnderTest.netApiDeliveryReceipt(deliveryInfoNotification);
        assertThat(objectUnderTest.callCount(), is(1L));
    }

    @Test
    public void testRestFunctionResetCallCounter() throws Exception {
        assertThat(objectUnderTest.callCount(), is(0L));
        objectUnderTest.oneApiDeliveryReceipt(null, deliveryStatus);
        assertThat(objectUnderTest.callCount(), is(1L));
        objectUnderTest.resetCount();
        assertThat(objectUnderTest.callCount(), is(0L));
    }

}
