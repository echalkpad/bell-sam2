package com.vennetics.bell.sam.error;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Restv091Error")
public class Restv091Error {

    @SuppressWarnings({ "squid:S1165" })
    // Suppress warning about this final field. Cannot be final as we need the default constructor for ObjectMapping.
    private String errorMessage;

    protected Restv091Error() {
        super();
    }

    public Restv091Error(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @XmlElement(name = "error")
    public String getErrorMessage() {
        return errorMessage;
    }

}
