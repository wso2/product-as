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
import org.wso2.appserver.Constants;
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerException;
import org.wso2.appserver.exceptions.ApplicationServerRuntimeException;
import org.wso2.appserver.utils.PathUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Java class which loads WSO2 specific context level configurations for all contexts.
 *
 * @since 6.0.0
 */
public class ContextConfigurationLoader implements LifecycleListener {
    private static final Map<Context, AppServerWebAppConfiguration> contextToConfigurationMap =
            new ConcurrentHashMap<>();

    /**
     * Retrieves the {@code AppServerWebAppConfiguration} matching the specified context.
     *
     * @param context the context for which the matching {@link AppServerWebAppConfiguration} is to be returned
     * @return the {@code AppServerWebAppConfiguration} matching the specified context
     */
    public static Optional<AppServerWebAppConfiguration> getContextConfiguration(Context context) {
        AppServerWebAppConfiguration configuration = contextToConfigurationMap.get(context);
        return Optional.ofNullable(configuration);
    }

    /**
     * Processes {@code Context}s at "configure_start" event to retrieve a final set of WSO2 specific
     * context level configurations.
     * <p>
     * For the purpose of generating the effective set of configurations, the global and context level webapp
     * descriptor files are read, if available.
     *
     * @param lifecycleEvent the lifecycle event that has occurred
     */
    @Override
    public void lifecycleEvent(LifecycleEvent lifecycleEvent) {
        Object source;
        if (Lifecycle.BEFORE_START_EVENT.equals(lifecycleEvent.getType())) {
            source = lifecycleEvent.getSource();
            if (source instanceof Context) {
                Context context = (Context) source;
                AppServerWebAppConfiguration effectiveConfiguration = getEffectiveConfiguration(context);
                contextToConfigurationMap.put(context, effectiveConfiguration);
            }
        } else if (Lifecycle.AFTER_STOP_EVENT.equals(lifecycleEvent.getType())) {
            source = lifecycleEvent.getSource();
            if (source instanceof Context) {
                Context context = (Context) source;
                contextToConfigurationMap.remove(context);
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
     */
    private static AppServerWebAppConfiguration getEffectiveConfiguration(Context context) {
        if (context != null) {
            Path schemaPath = Paths.
                    get(PathUtils.getAppServerConfigurationBase().toString(), Constants.WEBAPP_DESCRIPTOR_SCHEMA);
            Path defaultWebAppDescriptor = Paths.
                    get(PathUtils.getAppServerConfigurationBase().toString(), Constants.WEBAPP_DESCRIPTOR);
            AppServerWebAppConfiguration effective;
            try {
                Path localWebAppDescriptor = Paths.get(PathUtils.getWebAppPath(context).toString(),
                        Constants.WEB_CONTAINER_RESOURCE_FOLDER, Constants.WEBAPP_DESCRIPTOR);
                if (!Files.exists(defaultWebAppDescriptor)) {
                    throw new ApplicationServerRuntimeException(
                            "The " + defaultWebAppDescriptor.toString() + " does not exist");
                }
                effective = Utils.
                        getUnmarshalledObject(defaultWebAppDescriptor, schemaPath, AppServerWebAppConfiguration.class);
                if (Files.exists(localWebAppDescriptor)) {
                    AppServerWebAppConfiguration local = Utils.getUnmarshalledObject(localWebAppDescriptor, schemaPath,
                            AppServerWebAppConfiguration.class);
                    effective.merge(local);
                }
            } catch (ApplicationServerException e) {
                throw new ApplicationServerRuntimeException("Error when loading the context level configuration", e);
            }
            return effective;
        } else {
            throw new ApplicationServerRuntimeException("Context cannot be null");
        }
    }
}
