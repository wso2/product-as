package org.wso2.appserver.monitoring;

/**
 * The default values used for authentication and configurations of the valve.
 */
public class ConfigurationConstants {

    public static final String USERNAME = "";
    public static final String PASSWORD = "";
    public static final String CONFIG_FILE_FOLDER = "conf/Security";
    public static final String URL = "tcp://127.0.0.1:7611";
    public static final String STREAM_ID = "org.wso2.sample.http.stats:1.0.0";

    /**
     * instantiating is not needed for this class. private constructor to block that.
     */
    private ConfigurationConstants(){

    }
}
