package com.vennetics.bell.sam.sms.common.dao;

import javax.persistence.Embeddable;

@Embeddable
public class SmsCallbackData {

    private String deliveryReceiptUrl;
    private String callbackData;
    private NotificationFormat notificationFormat;

    public String getDeliveryReceiptUrl() {
        return deliveryReceiptUrl;
    }

    public void setDeliveryReceiptUrl(final String deliveryReceiptUrl) {
        this.deliveryReceiptUrl = deliveryReceiptUrl;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public void setCallbackData(final String callbackData) {
        this.callbackData = callbackData;
    }

    public NotificationFormat getNotificationFormat() {
        return notificationFormat;
    }

    public void setNotificationFormat(final NotificationFormat notificationFormat) {
        this.notificationFormat = notificationFormat;
    }

    @Override
    public String toString() {
        return "SmsCallbackData [deliveryReceiptUrl=" + deliveryReceiptUrl + ", callbackData="
                        + callbackData + ", notificationFormat=" + notificationFormat + "]";
    }
    
    
}
