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
package org.wso2.appserver.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class defines an API of file path related utility functions specific to WSO2 Application Server.
 *
 * @since 6.0.0
 */
public class PathUtils {
    /**
     * Returns a file path representation of the base of Apache Tomcat instances.
     *
     * @return a {@link Path} instance representing the base of Apache Tomcat instances
     * @throws AppServerException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getCatalinaBase() throws AppServerException {
        String envVariable = System.getProperty(Constants.CATALINA_BASE);
        if (envVariable != null) {
            return Paths.get(envVariable);
        } else {
            throw new AppServerException("CATALINA_BASE environmental variable has not been set");
        }
    }

    /**
     * Returns a file path representation of the Apache Tomcat configuration home.
     *
     * @return a {@link Path} instance representing the Apache Tomcat configuration home
     * @throws AppServerException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getCatalinaConfigurationHome() throws AppServerException {
        return Paths.get(getCatalinaBase().toString(), Constants.TOMCAT_CONFIGURATION_FOLDER_NAME);
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific configuration home.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific configuration home
     * @throws AppServerException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getWSO2ConfigurationHome() throws AppServerException {
        return Paths.get(getCatalinaConfigurationHome().toString(), Constants.WSO2_CONFIGURATION_FOLDER_NAME);
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific global configuration file.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific global configuration
     * file
     * @throws AppServerException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getWSO2GlobalConfigurationFile() throws AppServerException {
        return Paths.get(getWSO2ConfigurationHome().toString(), Constants.WSO2AS_CONFIG_FILE_NAME);
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific global configuration XML
     * schema file.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific global configuration
     * file
     * @throws AppServerException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getWSO2GlobalConfigurationSchemaFile() throws AppServerException {
        return Paths.get(getWSO2ConfigurationHome().toString(), Constants.WSO2_CONFIG_XML_SCHEMA_FILE_NAME);
    }
}
