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
package org.wso2.appserver.utils.paths;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.wso2.appserver.utils.AppServerException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class defines an API of file path related utility functions specific to WSO2 Application Server.
 *
 * @since 6.0.0
 */
public class PathUtils {
    /**
     * Prevents instantiating the {@code PathUtils} class.
     */
    private PathUtils() {
    }

    /**
     * Returns a file path representation of the base of Apache Tomcat instances.
     *
     * @return a {@link Path} instance representing the base of Apache Tomcat instances
     * @throws AppServerException if neither CATALINA_BASE nor CATALINA_HOME environmental variable has been set
     */
    public static Path getCatalinaBase() throws AppServerException {
        String envVariable = System.getProperty(PathConstants.CATALINA_BASE);
        if (envVariable != null) {
            return Paths.get(envVariable);
        } else {
            throw new AppServerException("Neither CATALINA_BASE nor CATALINA_HOME environmental variable has been set");
        }
    }

    /**
     * Returns a file path representation of the Apache Tomcat configuration home.
     *
     * @return a {@link Path} instance representing the Apache Tomcat configuration home
     * @throws AppServerException if neither CATALINA_BASE nor CATALINA_HOME environmental variable has been set
     */
    public static Path getCatalinaConfigurationHome() throws AppServerException {
        return Paths.get(getCatalinaBase().toString(), PathConstants.TOMCAT_CONFIGURATION_HOME);
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific configuration home.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific configuration home
     * @throws AppServerException if neither CATALINA_BASE nor CATALINA_HOME environmental variable has been set
     */
    public static Path getWSO2ConfigurationHome() throws AppServerException {
        return Paths.get(getCatalinaConfigurationHome().toString(), PathConstants.WSO2_CONFIGURATION_HOME);
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific global configuration file.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific global configuration
     * file
     * @throws AppServerException if neither CATALINA_BASE nor CATALINA_HOME environmental variable has been set
     */
    public static Path getGlobalWSO2WebAppDescriptor() throws AppServerException {
        return Paths.get(getWSO2ConfigurationHome().toString(), PathConstants.WEBAPP_DESCRIPTOR);
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific global configuration XML
     * schema file.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific global configuration
     * file
     * @throws AppServerException if neither CATALINA_BASE nor CATALINA_HOME environmental variable has been set
     */
    public static Path getWSO2WebAppDescriptorSchema() throws AppServerException {
        return Paths.get(getWSO2ConfigurationHome().toString(), PathConstants.WEBAPP_DESCRIPTOR_SCHEMA);
    }

    /**
     * Returns an absolute file path representation of the webapp descriptor file of the specified context.
     *
     * @param context the webapp of which the local webapp descriptor path is to be returned
     * @return the absolute file path representation of the webapp descriptor file of the specified context
     * @throws AppServerException if the context is null
     */
    public static Path getWSO2WebAppDescriptorForContext(Context context) throws AppServerException {
        if (context != null) {
            Path contextRoot = PathUtils.getContextRoot(context);
            return Paths.get(contextRoot.toString(), PathConstants.WEBAPP_RESOURCE_FOLDER, PathConstants.WEBAPP_DESCRIPTOR);
        } else {
            throw new AppServerException("Context cannot be null");
        }
    }

    /**
     * Returns an absolute file path representation of the webapp context root specified.
     *
     * @param context the webapp of which the context root is to be returned
     * @return the absolute file path representation of the webapp context root specified
     * @throws AppServerException if the context is null
     */
    protected static Path getContextRoot(Context context) throws AppServerException {
        if (context != null) {
            Host host = (Host) context.getParent();
            String appBase = host.getAppBase();
            Path canonicalAppBase = Paths.get(appBase);

            if (!canonicalAppBase.isAbsolute()) {
                canonicalAppBase = Paths.get(getCatalinaBase().toString(), appBase);
            }

            String docBase = context.getDocBase();
            Path webappFilePath = Paths.get(docBase);
            if (!webappFilePath.isAbsolute()) {
                webappFilePath = Paths.get(canonicalAppBase.toString(), docBase);
            }
            return webappFilePath;
        } else {
            throw new AppServerException("Context cannot be null");
        }
    }
}
