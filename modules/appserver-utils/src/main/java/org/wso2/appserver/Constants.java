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
package org.wso2.appserver;

/**
 * A Java class which defines the constants associated with WSO2 Application Server appserver-utils module.
 *
 * @since 6.0.0
 */
public final class Constants {
    /**
     * XML namespace constants
     */

    /**
     * Namespace for wso2as.xml file XML content
     */
    public static final String APP_SERVER_CONFIGURATION_NAMESPACE = "http://wso2.org/2016/wso2as";

    /**
     * Namespace for wso2as-web.xml file XML content
     */
    public static final String WEBAPP_DESCRIPTOR_NAMESPACE = "http://wso2.org/2016/wso2as-web";

    /**
     * File name constants
     */

    /**
     * WSO2 Application Server descriptor file name
     */
    public static final String APP_SERVER_DESCRIPTOR = "wso2as.xml";
    /**
     * WSO2 Application Server descriptor XML schema file name
     */
    public static final String APP_SERVER_DESCRIPTOR_SCHEMA = "wso2as.xsd";
    /**
     * WSO2 Application Server context level descriptor file name
     */
    public static final String WEBAPP_DESCRIPTOR = "wso2as-web.xml";
    /**
     * WSO2 Application Server context level descriptor schema file name
     */
    public static final String WEBAPP_DESCRIPTOR_SCHEMA = "wso2as-web.xsd";

    /**
     * Folder identifier constants
     */

    /**
     * Apache Tomcat configuration base directory identifier
     */
    public static final String TOMCAT_CONFIGURATION_DIRECTORY = "conf";
    /**
     * WSO2 Application Server configuration base directory identifier
     */
    public static final String APP_SERVER_CONFIGURATION_DIRECTORY = "wso2";
    /**
     * Web application specific resource folder identifier
     */
    public static final String WEBAPP_RESOURCE_FOLDER = "WEB-INF";

    /**
     * Prevents instantiating this class.
     */
    private Constants() {
    }
}
