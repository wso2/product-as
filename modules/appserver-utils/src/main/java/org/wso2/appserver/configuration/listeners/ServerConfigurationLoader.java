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
import org.wso2.appserver.configuration.server.AppServerSecurity;
import org.wso2.appserver.configuration.server.ApplicationServerConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerException;
import org.wso2.appserver.exceptions.ApplicationServerRuntimeException;
import org.wso2.appserver.utils.PathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * A Java class which loads the WSO2 specific server level configurations during Application Server startup.
 *
 * @since 6.0.0
 */
public class ServerConfigurationLoader implements LifecycleListener {
    private static ApplicationServerConfiguration appServerConfiguration;

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
                buildGlobalConfiguration();
            }
        }
    }

    public static ApplicationServerConfiguration getServerConfiguration() {
        return appServerConfiguration;
    }

    private static synchronized void buildGlobalConfiguration() {
        try {
            if (appServerConfiguration == null) {
                Path schemaPath = Paths.get(PathUtils.getAppServerConfigurationBase().toString(),
                        Constants.APP_SERVER_DESCRIPTOR_SCHEMA);
                Path descriptorPath = Paths.
                        get(PathUtils.getAppServerConfigurationBase().toString(), Constants.APP_SERVER_DESCRIPTOR);
                appServerConfiguration = Utils.
                        getUnmarshalledObject(descriptorPath, schemaPath, ApplicationServerConfiguration.class);
                Optional.ofNullable(appServerConfiguration).ifPresent(ApplicationServerConfiguration::resolveVariables);
                setSecuritySystemProperties();
            }
        } catch (ApplicationServerException e) {
            throw new ApplicationServerRuntimeException("An error has occurred when building the global configuration",
                    e);
        }
    }

    /**
     * Sets the system properties associated with Java SSL.
     */
    private static void setSecuritySystemProperties() {
        Optional.ofNullable(appServerConfiguration).ifPresent(configuration -> {
            AppServerSecurity securityConfiguration = configuration.getSecurityConfiguration();

            System.setProperty("javax.net.ssl.keyStore",
                    securityConfiguration.getKeystore().getLocation().replace("\\", "/"));
            System.setProperty("javax.net.ssl.keyStorePassword", securityConfiguration.getKeystore().getPassword());
            System.setProperty("javax.net.ssl.keyStoreType", securityConfiguration.getKeystore().getType());
            System.setProperty("javax.net.ssl.trustStore",
                    securityConfiguration.getTruststore().getLocation().replace("\\", "/"));
            System.setProperty("javax.net.ssl.trustStorePassword", securityConfiguration.getTruststore().getPassword());
            System.setProperty("javax.net.ssl.trustStoreType", securityConfiguration.getTruststore().getType());
        });
    }
}
