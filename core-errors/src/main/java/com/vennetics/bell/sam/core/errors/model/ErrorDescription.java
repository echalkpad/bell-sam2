package com.vennetics.bell.sam.core.errors.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error defined in an external configuration file and returned in API responses.
 */

public class ErrorDescription {

    private final String code;

    private final String type;

    @JsonProperty("statusCode")
    private final int statusCode;

    private final String message;

    @JsonCreator
    public ErrorDescription(@JsonProperty("code") final String code,
                            @JsonProperty("type") final String type,
                            @JsonProperty("statusCode") final int statusCode,
                            @JsonProperty("message") final String message) {
        this.code = code;
        this.type = type;
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * @return the error Code e.g. JBOX-00001.
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the error type e.g. HTTP_401_OPERATION_FORBIDDEN
     */
    public String getType() {
        return type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the message for this error as defined in the XML file. Note this
     * is NOT the formatted message
     * <P>
     * e.g. <code>Could not create account for username {0}. {1}</code>
     * 
     * @return the message as defined in the xml file.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (code == null ? 0 : code.hashCode());
        result = prime * result + (type == null ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ErrorDescription)) {
            return false;
        }
        final ErrorDescription other = (ErrorDescription) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Error [code=" + code + ", type=" + type + ", statusCode=" + statusCode
                        + ", message=" + message + "]";
    }

}
