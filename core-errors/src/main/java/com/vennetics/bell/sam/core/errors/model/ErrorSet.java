package com.vennetics.bell.sam.core.errors.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Container for the list of errorDescriptions defined in the external configuration file.
 * 
 */
public class ErrorSet {

    @JsonProperty("error")
    private List<ErrorDescription> errorDescriptions;

    public List<ErrorDescription> getErrorDescriptions() {
        return errorDescriptions;
    }

    public void setErrorDescriptions(final List<ErrorDescription> errorDescriptions) {
        this.errorDescriptions = errorDescriptions;
    }
}
