package org.wso2.appserver.monitoring.exceptions;

/**
 * General exception class for the Statistics Publisher module
 */
public class StatPublisherException extends Exception {

    /**
     * Exception to be thrown when an error occurs in the statistics publisher module
     * @param message the detail message
     * @param cause the cause of exception
     */
    public StatPublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}
