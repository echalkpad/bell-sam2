package com.vennetics.bell.sam.model.subscriber.thirdparty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A POJO to represent the SubscriberProfile
 *
 * @author aaronwatters
 */
@XmlRootElement(name = "subscriberProfile")
public class SubscriberProfile  {

    @XmlElement(name = "attributes")
    private List<Attribute> attributes;


    public SubscriberProfile() {
        // For JSON deserialsation.
    }

    public SubscriberProfile(final List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SubscriberProfile that = (SubscriberProfile) obj;

        return !(attributes != null ? !attributes.equals(that.attributes)
                        : that.attributes != null);

    }

    @Override
    public int hashCode()  {
        return attributes != null ? attributes.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SubscriberProfile [attributes=" + attributes + "]";
    }

}
