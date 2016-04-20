package com.vennetics.bell.sam.smsx.enabler.notification;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Outgoing notification for OneAPI.
 */
@XmlRootElement(name = "deliveryStatus")
public class DeliveryStatus {

    @XmlElement(name = "Message", required = true)
    private List<Message> messages;

    public List<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public void setMessages(final List<Message> messages) {
        this.messages = messages;
    }
}
