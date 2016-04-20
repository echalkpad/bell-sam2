package com.vennetics.bell.sam.sms.common.dao;

/**
 * Delivery status of a message.
 *
 */
public enum SmsDeliveryStatus {

    DELIVERED_TO_TERMINAL("DeliveredToTerminal"),
    DELIVERY_UNCERTAIN("DeliveryUncertain"),
    DELIVERY_IMPOSSIBLE("DeliveryImpossible"),
    MESSAGE_WAITING("MessageWaiting"),
    DELIVERED_TO_NETWORK("DeliveredToNetwork");

    private final String value;

    SmsDeliveryStatus(final String suppliedValue) {
        value = suppliedValue;
    }

    public String value() {
        return value;
    }

    public static SmsDeliveryStatus fromValue(final String value) {
        for (SmsDeliveryStatus statusValue : SmsDeliveryStatus.values()) {
            if (statusValue.value.equals(value)) {
                return statusValue;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * Is this a notifiable state? i.e. can the message state trigger a message
     * to TPA?
     */
    public boolean isNotifiable(final boolean hasReceiptableUrl) {
        switch (this) {
            case DELIVERY_IMPOSSIBLE:
            case DELIVERED_TO_TERMINAL:
                return hasReceiptableUrl;
            default:
                return false;
        }
    }

}
