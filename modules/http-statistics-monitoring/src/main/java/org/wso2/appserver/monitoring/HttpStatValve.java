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
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.StatsPublisherConfiguration;
import org.wso2.appserver.monitoring.exceptions.ConfigurationException;
import org.wso2.appserver.monitoring.exceptions.EventBuilderException;
import org.wso2.appserver.monitoring.utils.EventBuilder;
import org.wso2.appserver.utils.PathUtils;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;

/**
 * Custom Tomcat valve to Publish server statistics data to Data Analytics Server.
 */
public class HttpStatValve extends ValveBase {

    private static final Log LOG = LogFactory.getLog(HttpStatValve.class);
    private DataPublisher dataPublisher = null;
    private Parser uaParser = null;
    StatsPublisherConfiguration statsPublisherConfiguration;
    Path path;

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();

        LOG.debug("The HttpStatValve initialized.");

        statsPublisherConfiguration = ServerConfigurationLoader.getServerConfiguration().
                getStatsPublisherConfiguration();
        path = PathUtils.getWSO2ConfigurationHome();

        try {
            uaParser = new CachingParser();
        } catch (IOException e) {
            LOG.error("Initializing caching parser object failed:" + e);
            throw new RuntimeException("Initializing caching parser object failed:", e);
        }

        try {
            dataPublisher = getDataPublisher();
        } catch (ConfigurationException e) {
            LOG.error("Initializing DataPublisher failed:", e);
            throw new LifecycleException("Initializing DataPublisher failed", e);
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

        if (filterResponse(response)) {
            Event event = null;
            try {
                event = EventBuilder.buildEvent(statsPublisherConfiguration.getStreamId(), request, response, startTime,
                        responseTime, uaParser);
            } catch (EventBuilderException e) {
                LOG.error("Creating the Event failed: " + e);
            }

            dataPublisher.publish(event);
        }
    }

    /**
     * get file path to the file containing Data Agent configuration and properties.
     * @return the path to the file containing configurations for the Data Agent
     */
    private String getDataAgentConfigPath() {
        Path path = Paths.get(PathUtils.getWSO2ConfigurationHome().toString(),
                EventPublisherConstants.DATA_AGENT_CONF);
        return path.toString();
    }

    /**
     * Instantiate a data publisher to be used to publish data to DAS.
     * @return DataPublisher object initialized with configurations
     * @throws ConfigurationException
     */
    private DataPublisher getDataPublisher() throws ConfigurationException {

        Path path = Paths.get(PathUtils.getWSO2ConfigurationHome().toString(), "Webapp_Statistics_Monitoring",
                EventPublisherConstants.CLIENT_TRUSTSTORE);
        System.setProperty("javax.net.ssl.trustStore", path.toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        DataPublisher dataPublisher;

        try {

            if (statsPublisherConfiguration.getAuthenticationURL() == null) {
                dataPublisher = new DataPublisher(statsPublisherConfiguration.getPublisherURL(),
                        statsPublisherConfiguration.getUsername(),
                        statsPublisherConfiguration.getPassword());
            } else {
                dataPublisher = new DataPublisher(statsPublisherConfiguration.getDataAgentType(),
                        statsPublisherConfiguration.getPublisherURL(),
                        statsPublisherConfiguration.getAuthenticationURL(),
                        statsPublisherConfiguration.getUsername(),
                        statsPublisherConfiguration.getPassword());
            }

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
     * @param response
     * @return
     */
    private boolean filterResponse (Response response) {

        String responseContentType = response.getContentType();
        //if the response content is not null and is of type text/html, allow to publish stats
        if (responseContentType != null && responseContentType.contains("text/html")) {
            return true;
        }
        return false;
    }
}
