package org.wso2.appserver.apieverywhere.exceptions;

/**
 * Custom run time exception class for the api-everywhere-as module.
 *
 * @since 6.0.0
 */
public class APIEverywhereException extends RuntimeException {

    /**
     * Exception to be thrown when an error occurs in the api-everywhere-as module.
     *
     * @param message the detail message
     * @param cause   the cause of exception
     */
    public APIEverywhereException(String message, Throwable cause) {
        super(message, cause);
    }
}
