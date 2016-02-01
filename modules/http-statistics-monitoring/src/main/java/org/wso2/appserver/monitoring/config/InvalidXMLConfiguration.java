package org.wso2.appserver.monitoring.config;

/**
 * Created by nathasha on 1/6/16.
 */
public class InvalidXMLConfiguration extends Exception {

    public InvalidXMLConfiguration() {
        super();
    }

    public InvalidXMLConfiguration(String message) {
        super(message);
    }

    public InvalidXMLConfiguration(String message, Throwable cause) {
        super(message, cause);
    }

}
