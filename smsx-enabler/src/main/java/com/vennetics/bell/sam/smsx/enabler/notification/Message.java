package com.vennetics.bell.sam.smsx.enabler.notification;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created on 17/02/2016.
 */
public class Message {

    @XmlAttribute
    private String address;

    @XmlAttribute
    private String status;

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
