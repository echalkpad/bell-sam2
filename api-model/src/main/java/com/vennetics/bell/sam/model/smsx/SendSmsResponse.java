package com.vennetics.bell.sam.model.smsx;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity representing the response to an SMS send request.
 */
@XmlRootElement(name = "SmsResponse")
public class SendSmsResponse {

    @XmlAttribute
    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

}
