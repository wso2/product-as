/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.configuration.listeners;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.wso2.appserver.Constants;
import org.wso2.appserver.configuration.server.ServerConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerException;;
import org.wso2.appserver.utils.XMLUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java class which loads the WSO2 specific server level configurations during Application Server startup.
 *
 * @since 6.0.0
 */
public class ServerConfigurationLoader implements LifecycleListener {
    private static final Logger logger = Logger.getLogger(ServerConfigurationLoader.class.getName());
    private static ServerConfiguration serverConfiguration;

    /**
     * Retrieves the WSO2 specific, server level configurations from the configurations file before server startup
     * event.
     * <p>
     * Every WSO2 specific, server level configuration will be defined within this configurations file and will be
     * loaded at this point.
     *
     * @param lifecycleEvent the lifecycle event that has occurred
     */
    @Override
    public void lifecycleEvent(LifecycleEvent lifecycleEvent) {
        if (Lifecycle.BEFORE_START_EVENT.equals(lifecycleEvent.getType())) {
            Object source = lifecycleEvent.getSource();
            if (source instanceof Server) {
                try {
                    buildGlobalConfiguration();
                } catch (ApplicationServerException e) {
                    logger.log(Level.SEVERE, "An error has occurred when retrieving the server level configurations");
                }
            }
        }
    }

    public static ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    private static synchronized void buildGlobalConfiguration() throws ApplicationServerException {

        if (serverConfiguration == null) {
            Path schemaPath = Paths.get(Constants.CATALINA_BASE_PATH, Constants.TOMCAT_CONFIGURATION_DIRECTORY,
                    Constants.WSO2_CONFIGURATION_DIRECTORY, Constants.SERVER_DESCRIPTOR_SCHEMA);
            Path descriptorPath = Paths.get(Constants.CATALINA_BASE_PATH, Constants.TOMCAT_CONFIGURATION_DIRECTORY,
                    Constants.WSO2_CONFIGURATION_DIRECTORY, Constants.SERVER_DESCRIPTOR);
            serverConfiguration = XMLUtils.getUnmarshalledObject(descriptorPath, schemaPath, ServerConfiguration.class);
        }
    }
}