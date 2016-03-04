/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.appserver.monitoring;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.monitoring.exceptions.ConfigurationException;
import org.wso2.appserver.monitoring.exceptions.EventBuilderException;
import org.wso2.appserver.monitoring.utils.EventBuilder;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import ua_parser.CachingParser;
import ua_parser.Parser;

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
    private String username = DefaultConfigurationConstants.USERNAME;
    private String password = DefaultConfigurationConstants.PASSWORD;
    private String configFileFolder = DefaultConfigurationConstants.CONFIG_FILE_FOLDER;
    private String type = DefaultConfigurationConstants.TYPE;
    private String publisherUrl = DefaultConfigurationConstants.PUBLISHER_URL;
    private String authenticationUrl = DefaultConfigurationConstants.AUTHENTICATION_URL;
    private String streamId = DefaultConfigurationConstants.STREAM_ID;
    private DataPublisher dataPublisher = null;
    private String appServerHome;
    private Parser uaParser = null;

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();

        LOG.debug("The HttpStatValve initialized.");
        File userDir = new File(System.getProperty("catalina.base"));
        appServerHome = userDir.getAbsolutePath();

        try {
            uaParser = new CachingParser();
        } catch (IOException e) {
            LOG.error("Creating caching parser object failed:" + e);
            throw new RuntimeException("Creating caching parser object failed:", e);
        }

        try {
            dataPublisher = getDataPublisher();
        } catch (ConfigurationException e) {
            LOG.error("Creating DataPublisher failed:", e);
            throw new LifecycleException("Creating DataPublisher failed", e);
        }
//
//        //why is this necessary
//        if (dataPublisher == null) {
//            throw new LifecycleException("DataPublisher was not created.");
//        }
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {

        Long startTime = System.currentTimeMillis();
        getNext().invoke(request, response);
        long responseTime = System.currentTimeMillis() - startTime;

        Event event = null;
        try {
            event = EventBuilder.buildEvent(getStreamId(), request, response, startTime, responseTime, uaParser);
        } catch (EventBuilderException e) {
            LOG.error("Creating the Event failed: " + e);
        }

        dataPublisher.publish(event);
    }

    //get file path to the file containing Data Agent configuration and properties
    private String getDataAgentConfigPath() {
        Path path = Paths.get(appServerHome, getConfigFileFolder(), DefaultConfigurationConstants.DATA_AGENT_CONF);
        return path.toString();
    }

    //instantiate a data publisher to be used to publish data to DAS
    private DataPublisher getDataPublisher() throws ConfigurationException {

        Path path = Paths.get(appServerHome, getConfigFileFolder(), DefaultConfigurationConstants.CLIENT_TRUSTSTORE);
        System.setProperty("javax.net.ssl.trustStore", path.toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        DataPublisher dataPublisher;

        try {
            dataPublisher = new DataPublisher(getType(), getPublisherUrl(), getAuthenticationUrl(),
                    getUsername(), getPassword());
        } catch (DataEndpointAgentConfigurationException e) {
            LOG.error("Data Endpoint Agent configuration failed: " + e);
            throw new ConfigurationException("Data Endpoint Agent configuration failed: ", e);
        } catch (DataEndpointException e) {
            LOG.error("Communication with Data Endpoint failed: " + e);
            throw new ConfigurationException("Communication with Data Endpoint failed: ", e);
        } catch (DataEndpointConfigurationException e) {
            LOG.error("Parsing Data Endpoint configurations failed: " + e);
            throw new ConfigurationException("Parsing Data Endpoint configurations failed: ", e);
        } catch (DataEndpointAuthenticationException e) {
            LOG.error("Connection to Data Endpoint failed during authentication: " + e);
            throw new ConfigurationException("Connection to Data Endpoint failed during authentication: ", e);
        } catch (TransportException e) {
            LOG.error("Connection failed: " + e);
            throw new ConfigurationException("Connection failed: ", e);
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
     * @param type Data Agent type for publishing
     */
    public void setType(String type) {
        this.type = type; }

    /**
     *
     * @return Data Agent type for publishing
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param publisherUrl DAS url for publishing data
     */
    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl; }

    /**
     *
     * @return DAS url for publishing data
     */
    public String getPublisherUrl() {
        return publisherUrl;
    }

    /**
     *
     * @param authenticationUrl DAS url for authentication
     */
    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl; }

    /**
     *
     * @return DAS url for authentication
     */
    public String getAuthenticationUrl() {
        return authenticationUrl;
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
