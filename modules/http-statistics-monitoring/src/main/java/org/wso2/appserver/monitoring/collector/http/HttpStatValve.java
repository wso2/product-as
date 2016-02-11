package org.wso2.appserver.monitoring.collector.http;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.monitoring.config.ConfigurationException;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;

/**
 * Custom Tomcat valve to Publish server statistics data to Data Analytics Server.
 */
public class HttpStatValve extends ValveBase {

    private static final Log LOG = LogFactory.getLog(HttpStatValve.class);
    protected String username = "admin";
    protected String password = "admin";
    protected String configFileFolder = "conf/Security";
    protected String url = "tcp://127.0.0.1:7611";
    protected String streamId = "org.wso2.sample.http.stats:1.0.0";
    private DataPublisher dataPublisher = null;
    private String appServerHome;

    public HttpStatValve()  {

        LOG.debug("The HttpStatValve initialized.");
        File userDir = new File(System.getProperty("catalina.home"));
        appServerHome = userDir.getAbsolutePath();

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
        WebappMonitoringEvent webappMonitoringEvent = eventbuilder.setStatData(request, response,
                startTime, responseTime);

        if (LOG.isDebugEnabled()) {
            LOG.debug("publishing the HTTP Stat : " + webappMonitoringEvent);
        }
        try {
            Event event = eventbuilder.prepareEvent(getStreamId(), webappMonitoringEvent);
            dataPublisher.publish(event);
        } catch (MonitoringPublisherException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return file path to the file containing Data Agent configuration and properties
     */
    public String getDataAgentConfigPath() {
        Path path = Paths.get(appServerHome, getConfigFileFolder(), "data-agent-conf.xml");
        return path.toString();
    }

    //instantiating a data publisher to be used to publish data to DAS
    private DataPublisher getDataPublisher() throws ConfigurationException {

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        String url = getUrl();
        DataPublisher dataPublisher;
        try {
            dataPublisher = new DataPublisher(url, getUsername(), getPassword());
        } catch (DataEndpointAgentConfigurationException | DataEndpointException | DataEndpointConfigurationException |
                DataEndpointAuthenticationException | TransportException e) {
            throw new ConfigurationException("Configuring Data Endpoint Agent failed", e);
        }
        return dataPublisher;
    }

    /**
     *
     * @param password login password for DAS
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return login password for DAS
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param username login username for DAS
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return login username for DAS
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param configFileFolder relative path to the folder containing transport configuration files
     */
    public void setConfigFileFolder(String configFileFolder) {
        this.configFileFolder = configFileFolder;
    }

    /**
     *
     * @return relative path to the folder containing transport configuration files
     */
    public String getConfigFileFolder() {
        return configFileFolder;
    }

    /**
     *
     * @param url DAS url
     */
    public void setUrl(String url) {
        this.url = url; }

    /**
     *
     * @return DAS url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param streamId Unique ID of the Event Stream from which data is published to DAS
     */
    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    /**
     *
     * @return Unique ID of the Event Stream from which data is published to DAS
     */
    public String getStreamId() {
        return streamId;
    }
}
