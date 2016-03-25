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
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.StatsPublisherConfiguration;
import org.wso2.appserver.monitoring.exceptions.StatPublisherException;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.servlet.ServletException;

/**
 * Custom Tomcat valve to Publish server statistics data to Data Analytics Server.
 */
public class HttpStatValve extends ValveBase {

    private static final Log LOG = LogFactory.getLog(HttpStatValve.class);
    private DataPublisher dataPublisher = null;
    StatsPublisherConfiguration statsPublisherConfiguration;

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();

        LOG.debug("The HttpStatValve initialized.");

        setTrustStorePath();
        statsPublisherConfiguration = ServerConfigurationLoader.getServerConfiguration().
                getStatsPublisherConfiguration();

        try {
            dataPublisher = getDataPublisher();
        } catch (StatPublisherException e) {
            LOG.error("Initializing DataPublisher failed:", e);
            throw new LifecycleException("Initializing DataPublisher failed: " + e);
        }

    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {

        Long startTime = System.currentTimeMillis();
        getNext().invoke(request, response);
        long responseTime = System.currentTimeMillis() - startTime;

        if (filterResponse(response)) {
            Event event;
            try {
                event = EventBuilder.buildEvent(statsPublisherConfiguration.getStreamId(), request, response, startTime,
                        responseTime);
            } catch (StatPublisherException e) {
                LOG.error("Creating the Event failed: " + e);
                throw new IOException("Creating the Event failed: " + e);
            }

            dataPublisher.publish(event);
        }
    }

    /**
     * get file path to the file containing Data Agent configuration and properties.
     * @return the path to the file containing configurations for the Data Agent
     */
    private String getDataAgentConfigPath() {
        Path path = Paths.get(PathUtils.getAppServerConfigurationBase().toString(),
                EventPublisherConstants.DATA_AGENT_CONF);
        return path.toString();
    }

    /**
     * Instantiate a data publisher to be used to publish data to DAS.
     * @return DataPublisher object initialized with configurations
     * @throws StatPublisherException
     */
    private DataPublisher getDataPublisher() throws StatPublisherException {

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        DataPublisher dataPublisher;

        try {

            if (!Optional.ofNullable(statsPublisherConfiguration.getAuthenticationURL()).isPresent()) {
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
            throw new StatPublisherException("Data Endpoint Agent configuration failed: ", e);
        } catch (DataEndpointException e) {
            LOG.error("Communication with Data Endpoint failed: " + e);
            throw new StatPublisherException("Communication with Data Endpoint failed: ", e);
        } catch (DataEndpointConfigurationException e) {
            LOG.error("Parsing Data Endpoint configurations failed: " + e);
            throw new StatPublisherException("Parsing Data Endpoint configurations failed: ", e);
        } catch (DataEndpointAuthenticationException e) {
            LOG.error("Connection to Data Endpoint failed during authentication: " + e);
            throw new StatPublisherException("Connection to Data Endpoint failed during authentication: ", e);
        } catch (TransportException e) {
            LOG.error("Connection failed: " + e);
            throw new StatPublisherException("Connection failed: ", e);
        }

        return dataPublisher;
    }

    /**
     * Filter to process only requests of text/html type.
     * @param response The Response object of client
     * @return true if request is of text/html type and false if not
     */
    private boolean filterResponse (Response response) {

        String responseContentType = response.getContentType();
        //if the response content is not null and is of type text/html, allow to publish stats
        return responseContentType != null && responseContentType.contains("text/html");
    }

    /**
     * Setting the System property for the trust store.
     */
    private void setTrustStorePath() {
        String pathToBeReplaced = System.getProperty("javax.net.ssl.trustStore");
        String realTrustStorePath = StrSubstitutor.replaceSystemProperties(pathToBeReplaced);
        System.setProperty("javax.net.ssl.trustStore", realTrustStorePath);
    }
}
