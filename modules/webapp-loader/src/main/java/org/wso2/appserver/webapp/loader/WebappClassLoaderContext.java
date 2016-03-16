/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.appserver.webapp.loader;

import org.apache.catalina.Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.configuration.context.ClassLoaderConfiguration;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.ClassLoaderEnvironments;
import org.wso2.appserver.exceptions.ApplicationServerRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Build and stores the class loading context of a webapp which defines in the classloader configurations.
 *
 * @since 6.0.0
 */
public class WebappClassLoaderContext {

    private static Map<String, List<String>> definedEnvironments = new ConcurrentHashMap<>();
    private static final Log log = LogFactory.getLog(AppServerWebappLoader.class);

    private Map<String, List<String>> selectedEnvironments = new ConcurrentHashMap<>();

    static {
        List<ClassLoaderEnvironments.Environment> environments = ServerConfigurationLoader.getServerConfiguration()
                .getClassLoaderEnvironments().getEnvironments().getEnvironments();

        // populate the classpath defines in each environment
        environments.forEach((environment) -> {
            if (!definedEnvironments.containsKey(environment.getName())) {
                List<String> repositories = new ArrayList<>();
                try {
                    repositories.addAll(generateClasspath(environment.getClasspath()));
                    definedEnvironments.put(environment.getName(), repositories);
                } catch (FileNotFoundException ex) {
                    String message = "Environment configuration of: " + environment.getName() + " is wrong.";
                    log.error(message, ex);
                    throw new ApplicationServerRuntimeException(message, ex);
                }
            } else {
                log.warn("Duplicated environment: " + environment.getName()
                        + ", skipping classpath: " + environment.getClasspath());
            }
        });
    }

    /**
     * Construct web application specific classloader behaviour
     *
     * @param context the context of the web application
     */
    public WebappClassLoaderContext(Context context) {

        ContextConfigurationLoader.getContextConfiguration(context)
                .ifPresent(configuration -> {
                    ClassLoaderConfiguration classLoaderConfiguration = configuration.getClassLoaderConfiguration();
                    List<String> environmentNames = Arrays
                            .asList(classLoaderConfiguration.getEnvironments().split("\\s*,\\s*"));

                    environmentNames.forEach(environmentName -> {
                        if (definedEnvironments.containsKey(environmentName)) {
                            selectedEnvironments.put(environmentName, definedEnvironments.get(environmentName));
                        } else {
                            String message = "Undefined environment: " + environmentName + " in " + context.getPath();
                            log.warn(message);
                        }
                    });
                });
    }

    /**
     * Get the all jar library urls specified by the each environment for this web application
     *
     * @return A list containing the jar library urls as strings
     */
    public List<String> getProvidedRepositories() {
        List<String> repositories = new ArrayList<>();
        selectedEnvironments.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .forEach(repositories::addAll);
        return repositories;
    }

    // returns a list of jar url's for the given path
    private static List<String> generateClasspath(String classPath) throws FileNotFoundException {

        List<String> urlStr = new ArrayList<>();
        String realClassPath = StrSubstitutor.replaceSystemProperties(classPath);
        File classPathUrl = new File(realClassPath);

        if (!classPathUrl.exists()) {
            throw new FileNotFoundException("The classpath: " + realClassPath + " does not exist.");
        }

        if (classPathUrl.isFile()) {
            urlStr.add(classPathUrl.toURI().toString());
            return urlStr;
        }

        FileUtils.listFiles(classPathUrl, new String[]{"jar"}, false)
                .forEach((file) -> urlStr.add(file.toURI().toString()));

        return urlStr;
    }


}
