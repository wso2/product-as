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


import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.wso2.appserver.Constants;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerException;
import org.wso2.appserver.utils.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java class which loads WSO2 specific context level configurations for all contexts.
 *
 * @since 6.0.0
 */
public class ContextConfigurationLoader implements LifecycleListener {
    private static final Logger logger = Logger.getLogger(ContextConfigurationLoader.class.getName());
    private static final Map<Context, ContextConfiguration> contextToConfigurationMap = new ConcurrentHashMap<>();

    /**
     * Retrieves the {@code ContextConfiguration} matching the specified context.
     *
     * @param context the context for which the matching {@link ContextConfiguration} is to be returned
     * @return the {@code ContextConfiguration} matching the specified context
     */
    public static Optional<ContextConfiguration> getContextConfiguration(Context context) {
        ContextConfiguration configuration = contextToConfigurationMap.get(context);
        return Optional.ofNullable(configuration);
    }

    /**
     * Processes {@code Context}s before their start event to retrieve a final set of WSO2 specific
     * context level configurations.
     * <p>
     * For the purpose of generating the effective set of configurations, the global and context level webapp
     * descriptor files are read, if available.
     *
     * @param lifecycleEvent the lifecycle event that has occurred
     */
    @Override
    public void lifecycleEvent(LifecycleEvent lifecycleEvent) {
        if (Lifecycle.BEFORE_START_EVENT.equals(lifecycleEvent.getType())) {
            Object source = lifecycleEvent.getSource();
            if (source instanceof Context) {
                Context context = (Context) source;
                try {
                    ContextConfiguration effectiveConfiguration = getEffectiveConfiguration(context);
                    contextToConfigurationMap.put(context, effectiveConfiguration);
                } catch (ApplicationServerException e) {
                    logger.log(Level.SEVERE, "An error occurred when retrieving the effective " +
                            "configuration for the context " + context, e);
                }
            }
        }
    }

    /**
     * Returns the final set of context level configurations for the specified context.
     * <p>
     * For this purpose, the context level configurations defined globally will be merged with context level
     * configurations overridden at the context level (if any).
     * If no configurations are overridden at context level, the global configurations will prevail.
     *
     * @param context the {@link Context} for which the final set of context level configurations are generated
     * @return the final set of context level configurations for the specified {@link Context}
     * @throws ApplicationServerException if the specified {@link Context} is null
     */
    private static ContextConfiguration getEffectiveConfiguration(Context context) throws ApplicationServerException {
        if (context != null) {
            Path schemaPath = Paths.get(Constants.CATALINA_BASE_PATH, Constants.TOMCAT_CONFIGURATION_DIRECTORY,
                    Constants.WSO2_CONFIGURATION_DIRECTORY, Constants.WEBAPP_DESCRIPTOR_SCHEMA);

            Path defaultWebAppDescriptor = Paths.get(Constants.CATALINA_BASE_PATH,
                    Constants.TOMCAT_CONFIGURATION_DIRECTORY, Constants.WSO2_CONFIGURATION_DIRECTORY,
                    Constants.WEBAPP_DESCRIPTOR);

            Path contextWebAppDescriptor = Paths.get(getWebappFilePath(context), Constants.WEBAPP_RESOURCE_FOLDER,
                    Constants.WEBAPP_DESCRIPTOR);

            if (!Files.exists(defaultWebAppDescriptor)) {
                throw new ApplicationServerException("The " + defaultWebAppDescriptor.toString() + " does not exist");
            }

            ContextConfiguration effective = XMLUtils
                    .getUnmarshalledObject(defaultWebAppDescriptor, schemaPath, ContextConfiguration.class);

            if (Files.exists(contextWebAppDescriptor)) {
                ContextConfiguration local = XMLUtils
                        .getUnmarshalledObject(contextWebAppDescriptor, schemaPath, ContextConfiguration.class);
                effective.merge(local);
            }

            return effective;
        } else {
            throw new ApplicationServerException("Context cannot be null");
        }
    }

    // generate the web app file path if exist
    private static String getWebappFilePath(Context context) throws ApplicationServerException {
        String webappFilePath = null;

        //Value of the following variable depends on various conditions. Sometimes you get just the webapp directory
        //name. Sometime you get absolute path the webapp directory or war file.
        try {
            if (context != null) {
                String docBase = context.getDocBase();
                Host host = (Host) context.getParent();
                String appBase = host.getAppBase();
                File canonicalAppBase = new File(appBase);
                if (canonicalAppBase.isAbsolute()) {
                    canonicalAppBase = canonicalAppBase.getCanonicalFile();
                } else {
                    canonicalAppBase = new File(Constants.CATALINA_BASE_PATH, appBase).getCanonicalFile();
                }

                File webappFile = new File(docBase);
                if (webappFile.isAbsolute()) {
                    webappFilePath = webappFile.getCanonicalPath();
                } else {
                    webappFilePath = (new File(canonicalAppBase, docBase)).getPath();
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new ApplicationServerException("Error while generating webapp file path", ex);
        }

        return webappFilePath;
    }
}
