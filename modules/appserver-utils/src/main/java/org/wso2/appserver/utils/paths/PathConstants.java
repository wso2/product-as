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

/**
 * This class defines the constants used within {@code PathUtils}.
 *
 * @since 6.0.0
 */
public class PathConstants {
    //  Environmental variable property name constant
    public static final String CATALINA_BASE = "catalina.base";

    //  File path related constants
    public static final String WEBAPP_DESCRIPTOR = "wso2as-web.xml";
    public static final String WEBAPP_DESCRIPTOR_SCHEMA = "wso2as-web.xsd";
    protected static final String TOMCAT_CONFIGURATION_HOME = "conf";
    protected static final String WSO2_CONFIGURATION_HOME = "wso2";
    protected static final String WEBAPP_RESOURCE_FOLDER = "WEB-INF";

    /**
     * Prevents instantiating the PathConstants class.
     */
    private PathConstants() {
    }
}
