package com.vennetics.bell.sam.smsx.enabler;

import java.util.List;

/**
 * Pojo holding relevant pieces of information about a SMS request.
 */
public class SendSmsRequest {

    private String senderAddress;
    private String message;
    private List<String> addresses;
    private String notifyUrl;
    private String correlator;

    public SendSmsRequest(final String senderAddress,
                          final String message,
                          final List<String> addresses,
                          final String notifyUrl,
                          final String correlator) {
        this.senderAddress = senderAddress;
        this.message = message;
        this.addresses = addresses;
        this.notifyUrl = notifyUrl;
        this.correlator = correlator;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(final String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(final String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getCorrelator() {
        return correlator;
    }

    public void setCorrelator(final String correlator) {
        this.correlator = correlator;
    }

}
