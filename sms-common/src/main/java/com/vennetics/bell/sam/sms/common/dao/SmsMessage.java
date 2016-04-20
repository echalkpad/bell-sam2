package com.vennetics.bell.sam.sms.common.dao;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Arrays;
import java.util.UUID;

@Entity
public class SmsMessage {

    @Id
    @GeneratedValue
    private Long id;

    private UUID externalRequestId;
    private String clientCorrelator;
    private String sourceAddress;
    private String destinationAddress;
    private Integer sequenceNumber;
    private SmsCallbackData smsCallbackData;
    private String messageId;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 65535)
    private byte[] content;
    private boolean binary;
    @Enumerated(EnumType.STRING)
    private SmsDeliveryStatus deliveryStatus;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getExternalRequestId() {
        return externalRequestId;
    }

    public void setExternalRequestId(final UUID externalRequestId) {
        this.externalRequestId = externalRequestId;
    }

    public String getClientCorrelator() {
        return clientCorrelator;
    }

    public void setClientCorrelator(final String clientCorrelator) {
        this.clientCorrelator = clientCorrelator;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(final String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(final String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(final Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public SmsCallbackData getSmsCallbackData() {
        return smsCallbackData;
    }

    public void setSmsCallbackData(final SmsCallbackData smsCallbackData) {
        this.smsCallbackData = smsCallbackData;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(final boolean binary) {
        this.binary = binary;
    }

    public SmsDeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(final SmsDeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "SmsMessage [" + "id=" + id + ", externalRequestId=" + externalRequestId
                        + ", clientCorrelator='" + clientCorrelator + '\'' + ", sourceAddress='"
                        + sourceAddress + '\'' + ", destinationAddress='" + destinationAddress
                        + '\'' + ", sequenceNumber=" + sequenceNumber + ", smsCallbackData="
                        + smsCallbackData + ", messageId='" + messageId + '\'' + ", content="
                        + Arrays.toString(content) + ", binary=" + binary + ", deliveryStatus="
                        + deliveryStatus + ']';
    }
}
