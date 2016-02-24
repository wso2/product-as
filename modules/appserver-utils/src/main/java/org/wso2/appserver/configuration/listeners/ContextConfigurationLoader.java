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
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.wso2.appserver.utils.XMLUtils;
import org.wso2.appserver.utils.PathUtils;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.exceptions.AppServerException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java class which loads WSO2 specific context level configurations for all contexts.
 *
 * @since 6.0.0
 */
public class ContextConfigurationLoader implements LifecycleListener {
    private static final Logger logger = Logger.getLogger(ContextConfigurationLoader.class.getName());
    private static final Map<Context, ContextConfiguration> contextToConfigurationMap = new HashMap<>();

    /**
     * Retrieves the {@code ContextConfiguration} matching the specified context.
     *
     * @param context the context for which the matching {@link ContextConfiguration} is to be returned
     * @return the {@code ContextConfiguration} matching the specified context
     */
    public static Optional<ContextConfiguration> retrieveContextConfiguration(Context context) {
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
                    ContextConfiguration effectiveConfiguration = retrieveEffectiveConfiguration(context);
                    contextToConfigurationMap.put(context, effectiveConfiguration);
                } catch (AppServerException e) {
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
     * @throws AppServerException if the specified {@link Context} is null
     */
    private static ContextConfiguration retrieveEffectiveConfiguration(Context context) throws AppServerException {
        if (context != null) {
            Path globalWebAppDescriptor = PathUtils.getGlobalWSO2WebAppDescriptor();
            Path contextWebAppDescriptor = PathUtils.getWSO2WebAppDescriptorForContext(context);
            Optional<Path> schema = Optional.of(PathUtils.getWSO2WebAppDescriptorSchema());

            if ((Files.exists(globalWebAppDescriptor)) && (Files.exists(contextWebAppDescriptor))) {
                ContextConfiguration global = XMLUtils.
                        getUnmarshalledObject(globalWebAppDescriptor, schema, ContextConfiguration.class);
                ContextConfiguration local = XMLUtils.
                        getUnmarshalledObject(contextWebAppDescriptor, schema, ContextConfiguration.class);
                return ListenerUtils.merge(global, local);
            } else if (Files.exists(globalWebAppDescriptor)) {
                ContextConfiguration global = XMLUtils.
                        getUnmarshalledObject(globalWebAppDescriptor, schema, ContextConfiguration.class);
                return ListenerUtils.merge(global, null);
            } else {
                throw new AppServerException("The " + globalWebAppDescriptor.toString() + " does not exist");
            }
        } else {
            throw new AppServerException("Context cannot be null");
        }
    }
}
