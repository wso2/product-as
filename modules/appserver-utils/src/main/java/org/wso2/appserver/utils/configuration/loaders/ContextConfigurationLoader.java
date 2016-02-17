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
package org.wso2.appserver.utils.configuration.loaders;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.wso2.appserver.utils.AppServerException;
import org.wso2.appserver.utils.GenericUtils;
import org.wso2.appserver.utils.configuration.model.Configuration;
import org.wso2.appserver.utils.paths.PathUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * A Java class which manages the context level WSO2 specific configurations for all contexts.
 *
 * @since 6.0.0
 */
public class ContextConfigurationLoader implements LifecycleListener {
    private static final Logger logger = Logger.getLogger(ContextConfigurationLoader.class.getName());
    private static final Map<Context, Configuration> contextToConfigurationMap = new HashMap<>();

    /**
     * Processes {@code Context}s before their start event to retrieve an final set of WSO2 specific
     * configurations.
     * <p>
     * For the purpose of generating the effective set of configurations, the global and context-level webapp
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
                    Configuration effectiveConfiguration = retrieveEffectiveConfiguration(context);
                    contextToConfigurationMap.put(context, effectiveConfiguration);
                } catch (AppServerException e) {
                    logger.log(Level.SEVERE,
                            "An error occurred when retrieving the effective configuration for the context", e);
                }
            }
        }
    }

    /**
     * Retrieves the {@code Configuration} matching the specified context.
     *
     * @param context the context of which the matching {@link Configuration} is to be returned
     * @return the {@code Configuration} matching the specified context
     */
    public static Optional<Configuration> retrieveContextConfiguration(Context context) {
        Configuration configuration = contextToConfigurationMap.get(context);
        return Optional.ofNullable(configuration);
    }

    /**
     * Returns a {@code Configuration} object which merges the globally and locally declared webapp descriptor
     * configurations.
     *
     * @param context the webapp to be processed
     * @return a {@link Configuration} object which merges the globally and locally declared webapp descriptor
     * configurations
     * @throws AppServerException if an error occurs when retrieving the final configurations
     */
    private static Configuration retrieveEffectiveConfiguration(Context context) throws AppServerException {
        if (context != null) {
            //  Retrieves the global configuration holder for global webapp descriptor
            Configuration globalConfiguration = GlobalConfigurationLoader.getGlobalConfiguration();

            //  Obtains the file path representation for the currently processed context
            Path contextWebAppDescriptor = PathUtils.getWSO2WebAppDescriptorForContext(context);
            Unmarshaller unmarshaller = GenericUtils.
                    getXMLUnmarshaller(Optional.of(PathUtils.getWSO2WebAppDescriptorSchema()), Configuration.class);
            Configuration contextConfiguration;
            try {
                if (Files.exists(contextWebAppDescriptor)) {
                    contextConfiguration = (Configuration) unmarshaller.unmarshal(contextWebAppDescriptor.toFile());
                } else {
                    contextConfiguration = null;
                }
            } catch (JAXBException e) {
                throw new AppServerException("An error has occurred during unmarshalling XML data", e);
            }

            //  Return the effective configuration holder processed using the global configurations and currently
            //  processed context configurations
            return PriorityUtils.merge(globalConfiguration, Optional.ofNullable(contextConfiguration));
        } else {
            throw new AppServerException("Context cannot be null");
        }
    }
}
