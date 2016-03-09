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

import org.apache.catalina.Globals;
import org.wso2.appserver.Constants;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class defines an API of file path related utility functions specific to WSO2 Application Server.
 *
 * @since 6.0.0
 */
public final class PathUtils {
    private static final Path PATH_CATALINA_BASE;
    private static final Path PATH_CATALINA_CONFIG_BASE;
    private static final Path PATH_APP_SERVER_CONFIG_BASE;

    static {
        String catalinaBase = System.getProperty(Globals.CATALINA_BASE_PROP);
        PATH_CATALINA_BASE = Paths.get(catalinaBase);
        PATH_CATALINA_CONFIG_BASE = Paths.get(catalinaBase, Constants.TOMCAT_CONFIGURATION_DIRECTORY);
        PATH_APP_SERVER_CONFIG_BASE = Paths.get(catalinaBase, Constants.TOMCAT_CONFIGURATION_DIRECTORY,
                Constants.APP_SERVER_CONFIGURATION_DIRECTORY);
    }

    /**
     * Prevents instantiating this class.
     */
    private PathUtils() {
    }

    /**
     * Returns a file path representation of the Apache Tomcat instance specified by the CATALINA_BASE
     * environment variable.
     *
     * @return a {@link Path} instance representing the Apache Tomcat instance specified by the CATALINA_BASE
     * environment variable
     */
    public static Path getCatalinaBase() {
        return PATH_CATALINA_BASE;
    }

    /**
     * Returns a file path representation of the Apache Tomcat configuration base.
     *
     * @return a {@link Path} instance representing the Apache Tomcat configuration base
     */
    public static Path getCatalinaConfigurationBase() {
        return PATH_CATALINA_CONFIG_BASE;
    }

    /**
     * Returns a file path representation of the Application Server's WSO2-specific configuration base.
     *
     * @return a {@link Path} instance representing the WSO2 Application Server's WSO2-specific configuration base
     */
    public static Path getAppServerConfigurationBase() {
        return PATH_APP_SERVER_CONFIG_BASE;
    }
}
