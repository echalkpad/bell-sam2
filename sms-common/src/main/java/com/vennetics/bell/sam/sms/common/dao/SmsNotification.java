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

@Entity
public class SmsNotification {

    @Id
    @GeneratedValue
    private Long id;
    private String sourceAddress;
    private String destinationAddress;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 65535)
    private byte[] content;
    private boolean binary;
    @Enumerated(EnumType.STRING)
    private SmsNotificationState notificationState;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(final String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(final String sourceAddress) {
        this.sourceAddress = sourceAddress;
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

    public SmsNotificationState getNotificationState() {
        return notificationState;
    }

    public void setNotificationState(final SmsNotificationState notificationState) {
        this.notificationState = notificationState;
    }

    @Override
    public String toString() {
        return "SmsNotification [id=" + id + ", sourceAddress=" + sourceAddress
                        + ", destinationAddress=" + destinationAddress + ", content="
                        + Arrays.toString(content) + ", binary=" + binary + ", notificationState="
                        + notificationState + "]";
    }

}
