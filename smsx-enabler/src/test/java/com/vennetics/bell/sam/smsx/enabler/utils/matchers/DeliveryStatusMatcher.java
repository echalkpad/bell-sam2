package com.vennetics.bell.sam.smsx.enabler.utils.matchers;

import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import static org.mockito.Matchers.argThat;

/**
 * Matcher which will verify if two {@link SmsMessage} objects can be matched on their SmsDeliveryStatus.
 */
public class DeliveryStatusMatcher extends ArgumentMatcher<SmsMessage> {

    private SmsMessage expected;

    public DeliveryStatusMatcher(final SmsMessage expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(final Object actual) {
        return ((SmsMessage) actual).getDeliveryStatus().equals(expected.getDeliveryStatus());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(expected == null ? null : expected.toString());
    }

    public static SmsMessage deliveryStatusEq(final SmsMessage expected) {
        return argThat(new DeliveryStatusMatcher(expected));
    }
}


