package com.vennetics.bell.sam.sms.common.dao;

/**
 * Notification status of a SMS message.
 *
 */
public enum SmsNotificationState {

    RECEIVED_FROM_NETWORK, /* Received from SMS-C, no subscription so not sent to TPA */
    MESSAGE_DELIVERED, /* Received from SMS-C and successfully sent to TPA */
    DELIVERY_IMPOSSIBLE; /* Error while sending notification to TPA */

}
