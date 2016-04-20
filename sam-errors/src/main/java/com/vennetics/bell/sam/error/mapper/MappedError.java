package com.vennetics.bell.sam.error.mapper;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;

/**
 * POJO defining the details of an exception instance.
 */
@SuppressWarnings("squid:S1165") // Deter sonarqube from complaining about
                                 // non-final fields in an "Exception" class
                                 // (triggered by class name?)
@XmlRootElement(name = "mappedError")
public class MappedError {

    private static final Logger LOG = LoggerFactory.getLogger(MappedError.class);

    @XmlElement(name = "errorCode")
    private String errorCode;

    @XmlElement(name = "messageTemplate")
    private String messageTemplate;

    @XmlElement(name = "code")
    private String code;

    @XmlElement(name = "statusCode")
    private int statusCode;

    @XmlElement(name = "policyError")
    private boolean isPolicyError;

    @XmlElement(name = "messageVariables")
    private String[] messageVariables;

    @XmlElement(name = "path")
    private String path;

    @XmlElement(name = "timestamp")
    private long timestamp;

    /**
     * To allow deserialisation in jaxb
     */
    public MappedError() {
        // For JSON object deserialisation
    }

    /**
     * JSON creator for Jackson
     *
     * @param errorCode
     * @param messageTemplate
     * @param code
     * @param statusCode
     * @param isPolicyError
     * @param messageVariables
     * @param path
     * @param timestamp
     * @return
     */
    @JsonCreator
    @SuppressWarnings({ "squid:S00107" })
    public MappedError(@JsonProperty("errorCode") final String errorCode,
                       @JsonProperty("messageTemplate") final String messageTemplate,
                       @JsonProperty("code") final String code,
                       @JsonProperty("statusCode") final int statusCode,
                       @JsonProperty("isPolicyError") final boolean isPolicyError,
                       @JsonProperty("messageVariables") final String[] messageVariables,
                       @JsonProperty("path") final String path,
                       @JsonProperty("timestamp") final long timestamp) {
        super();
        this.errorCode = errorCode;
        this.messageTemplate = messageTemplate;
        this.code = code;
        this.statusCode = statusCode;
        this.isPolicyError = isPolicyError;
        this.messageVariables = messageVariables;
        this.path = path;
        this.timestamp = timestamp;
    }

    /**
     * Constructor for exceptions with argument list.
     *
     * @param errorDescription
     *            the {@link ErrorDescription} defining the metadata for the
     *            error condition.
     * @param variables
     *            array of strings defining the message parameters, possibly
     *            null
     */
    public MappedError(final ErrorDescription errorDescription, final String... variables) {
        this(errorDescription, false, variables);
    }

    /**
     * Constructor for exceptions based on
     * {@link com.vennetics.bell.sam.core.exception.ApiException}
     *
     * @param errorDescription
     *            the {@link ErrorDescription} defining the metadata for the
     *            error condition.
     * @param isPolicyError
     *            indicates whether the error is a policy error (vs. service
     *            error)
     * @param variables
     *            array of strings defining the message parameters, possibly
     *            null
     */
    public MappedError(final ErrorDescription errorDescription,
                       final boolean isPolicyError,
                       final String... variables) {
        LOG.debug(">>> MappedException error:{} message:{} variables:{}, policy:{}",
                  errorDescription,
                  variables,
                  isPolicyError);

        messageTemplate = errorDescription.getMessage();
        statusCode = errorDescription.getStatusCode();
        code = errorDescription.getCode();
        errorCode = errorDescription.getType();
        messageVariables = variables != null ? variables : new String[0];
        this.isPolicyError = isPolicyError;
    }

    /**
     * @return the Error Code ENUM e.g. MESSAGE_ALLOWANCE_POLICY_NOT_FOUND
     */
    @JsonProperty("errorCode")
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @return the error message
     */
    @JsonProperty("messageTemplate")
    public String getMessageTemplate() {
        return messageTemplate;
    }

    /**
     * @return the unique error code e.g. JBOX-0001
     */
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    /**
     * @return the HTTP status code e.g. 401
     */
    @JsonProperty("statusCode")
    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus httpStatusCode() {
        return HttpStatus.valueOf(statusCode);
    }

    public String getPath() {
        return path;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String[] getMessageVariables() {
        return messageVariables.clone();
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public MappedError withPath(final String newPath) {
        setPath(newPath);
        return this;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public MappedError withTimestamp(final long newTimestamp) {
        setTimestamp(newTimestamp);
        return this;
    }

    @Override
    public String toString() {
        return "MappedError [statusCode=" + statusCode + ", errorCode=" + errorCode
                        + ", messageTemplate=" + messageTemplate + ", code=" + code + ", policy="
                        + isPolicyError + ", path=" + path + ", timestamp=" + timestamp
                        + ", messageVariables=" + Arrays.toString(messageVariables) + "]";
    }

    public boolean isPolicyError() {
        return isPolicyError;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (code == null ? 0 : code.hashCode());
        result = prime * result + (errorCode == null ? 0 : errorCode.hashCode());
        result = prime * result + (messageTemplate == null ? 0 : messageTemplate.hashCode());
        result = prime * result + (path == null ? 0 : path.hashCode());
        result = prime * result + statusCode;
        result = prime * result + (int) timestamp;
        result = prime * result + Arrays.hashCode(messageVariables);
        result = prime * result + BooleanUtils.toInteger(isPolicyError);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MappedError other = (MappedError) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        if (errorCode == null) {
            if (other.errorCode != null) {
                return false;
            }
        } else if (!errorCode.equals(other.errorCode)) {
            return false;
        }
        if (messageTemplate == null) {
            if (other.messageTemplate != null) {
                return false;
            }
        } else if (!messageTemplate.equals(other.messageTemplate)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }

        if (statusCode != other.statusCode) {
            return false;
        }

        if (timestamp != other.timestamp) {
            return false;
        }

        if (!Arrays.equals(messageVariables, other.messageVariables)) {
            return false;
        }

        return isPolicyError == other.isPolicyError;
    }
}
