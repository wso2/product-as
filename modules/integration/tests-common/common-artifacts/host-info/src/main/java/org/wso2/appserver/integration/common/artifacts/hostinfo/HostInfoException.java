package org.wso2.appserver.integration.common.artifacts.hostinfo;

/**
 * To handle the Exceptions while getting the host info .
 */
public class HostInfoException extends Exception{

    public HostInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
