package org.wso2.appserver.monitoring.collector.http;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.monitoring.config.ConfigurationException;
import org.wso2.appserver.monitoring.config.InvalidXMLConfiguration;
import org.wso2.appserver.monitoring.config.StreamConfig;
import org.wso2.appserver.monitoring.publisher.MonitoringPublisherException;
import org.wso2.appserver.monitoring.publisher.http.EventBuilder;
import org.wso2.appserver.monitoring.publisher.http.WebappMonitoringEvent;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;


/**
 * Custom Tomcat valve to Publish server statistics data to Data Analytics Server.
 */
public class HttpStatValve extends ValveBase {

    private static final Log LOG = LogFactory.getLog(HttpStatValve.class);
    protected String username = "admin";
    protected String password = "admin";
    protected String host = "127.0.0.1";
    protected String configFileFolder = "conf/Security";
    public DataPublisher dataPublisher = null;
    public String httpStream;
    public String streamVersion;
    String streamId;
    public String parentDir;
    private static final int defaultThriftPort = 7611;
    private static final int defaultBinaryPort = 9611;

    public HttpStatValve() throws InvalidXMLConfiguration {

        LOG.debug("The HttpStatValve initialized.");

        System.setProperty("javax.net.ssl.trustStore", parentDir + File.separator + getConfigFileFolder()
                + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        File userDir = new File(System.getProperty("catalina.home"));
        parentDir = userDir.getAbsolutePath();

        StreamConfig streamconfig = new StreamConfig();
        httpStream = streamconfig.getStreamName();
        streamVersion = streamconfig.getStreamVersion();
        streamId = DataBridgeCommonsUtils.generateStreamId(httpStream, streamVersion);

        try {
            if (dataPublisher == null) {
                dataPublisher = getDataPublisher();
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed at configurations: " + e);
        }
    }

    @Override
    public void invoke(Request request, Response response)  {

        Long startTime = System.currentTimeMillis();

        try {
            getNext().invoke(request, response);
        } catch (IOException | ServletException e) {
            LOG.error("Invoke failed:" + e);
        }
        long responseTime = System.currentTimeMillis() - startTime;

        EventBuilder eventbuilder = new EventBuilder();
        WebappMonitoringEvent webappMonitoringEvent = eventbuilder.setStatData(request, response, responseTime);
        webappMonitoringEvent.setTimestamp(startTime);

        if (LOG.isDebugEnabled()) {
            LOG.debug("publishing the HTTP Stat : " + webappMonitoringEvent);
        }

        try {
            Event event = eventbuilder.prepareEvent(streamId, webappMonitoringEvent);
            dataPublisher.publish(event);

        } catch (MonitoringPublisherException e) {
            LOG.error("Publishing failed:" + e);
        }

    }

    public  String getDataAgentConfigPath() {
        File filePath = new File(parentDir + File.separator + getConfigFileFolder());
        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";

    }

    private static String  getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0 || result.equals("")) {
            result = def;
        }
        return result;
    }

    private DataPublisher getDataPublisher() throws ConfigurationException {

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        String type = getProperty("type", "Thrift");

        int receiverPort = defaultThriftPort;
        if (type.equals("Binary")) {
            receiverPort = defaultBinaryPort;
        }
        String url = getProperty("url", "tcp://" + getHost() + ":" + receiverPort);
        DataPublisher dataPublisher;
        try {
            dataPublisher = new DataPublisher(url, getUsername(), getPassword());
        } catch (DataEndpointAgentConfigurationException | DataEndpointException | DataEndpointConfigurationException |
                DataEndpointAuthenticationException | TransportException e) {
            throw new ConfigurationException("Configuring Data Endpoint Agent failed", e);
        }
        return dataPublisher;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setConfigFileFolder(String configFileFolder) {
        this.configFileFolder = configFileFolder;
    }

    public String getConfigFileFolder() {
        return configFileFolder;
    }

}
