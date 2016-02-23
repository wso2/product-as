package org.wso2.appserver.monitoring.exceptions;

/**
 * Thrown when creating an Event with payload data and metadata fails.
 */
public class EventBuilderException extends Exception {

    public EventBuilderException() {
        super();
    }

    public EventBuilderException(String message) {
        super(message);
    }

    public EventBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
