package com.vennetics.bell.sam.model.smsx;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created on 02/03/2016.
 */
public class MessageResponse {

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

    @Override
    public String toString() {
        return "MessageResponse [address=" + address + ", status=" + status + "]";
    }

}
