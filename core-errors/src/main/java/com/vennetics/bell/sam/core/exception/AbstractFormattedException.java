package com.vennetics.bell.sam.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Signals a logical request handling failure which will be translated into a a
 * suitable rest response. This is a RuntimeException i.e. unchecked. It is
 * not expected that code will catch this exception, rather it should be
 * translated by the appropriate API proxy.
 */

public abstract class AbstractFormattedException extends RuntimeException {

    private static final long serialVersionUID = 5065966114610563829L;

    @SuppressWarnings({"squid:S1165", "squid:S1312"})  // Logger is not final, to allow for unit tests.
    private static Logger log = LoggerFactory.getLogger(AbstractFormattedException.class);

    private final String messageCode;

    public AbstractFormattedException(final String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
        log.warn(getLogMessage());
    }

    public AbstractFormattedException(final String messageCode, final String message) {
        super(message);
        this.messageCode = messageCode;
        log.warn(getLogMessage());
    }

    public AbstractFormattedException(final Throwable e,
                                      final String messageCode,
                                      final String message) {
        super(message, e);
        this.messageCode = messageCode;
        log.warn(getLogMessage(), e);
    }

    public AbstractFormattedException(final Throwable e, final String messageCode) {
        super(messageCode, e);
        this.messageCode = messageCode;
        log.warn(getLogMessage(), e);
    }

    /**
     * Get the internal message code.
     *
     * @return
     */
    public String getMessageCode() {
        return messageCode;
    }

    public String getFormattedMessage() {
        return getMessage();
    }

    private String getLogMessage() {
        return getMessage() != null ? "[" + messageCode + "]: " + getFormattedMessage()
                        : "Generating error with type [" + messageCode + "]";
    }

    // Implemented only to support unit test.
    public static void setLogger(final Logger logger) {
        log = logger;
    }
}
