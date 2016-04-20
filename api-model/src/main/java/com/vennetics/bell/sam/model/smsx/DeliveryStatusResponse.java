package com.vennetics.bell.sam.model.smsx;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created on 02/03/2016.
 */
public class DeliveryStatusResponse {

    @XmlElement(name = "Message")
    private List<MessageResponse> messageResponses;

    public List<MessageResponse> getMessageResponses() {
        if (messageResponses == null) {
            messageResponses = new ArrayList<>();
        }
        return messageResponses;
    }

    public void setMessageResponses(final List<MessageResponse> messageResponses) {
        this.messageResponses = messageResponses;
    }

    @Override
    public String toString() {
        return "DeliveryStatusResponse [messageResponses=" + messageResponses + "]";
    }

}
