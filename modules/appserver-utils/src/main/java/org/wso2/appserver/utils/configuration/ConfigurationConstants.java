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
package org.wso2.appserver.utils.configuration;

/**
 * A Java class which defines constants associated with WSO2 Application Server configurations.
 *
 * @since 6.0.0
 */
public class ConfigurationConstants {
    //  Namespace for wso2as.xml file XML content
    public static final String SERVER_CONFIGURATION_NAMESPACE = "http://wso2.org/2016/wso2as-server";
    //  Namespace for wso2as-web.xml file XML content
    public static final String WEBAPP_DESCRIPTOR_NAMESPACE = "http://wso2.org/2016/wso2as-web";

    /**
     * Prevents instantiating this class.
     */
    private ConfigurationConstants() {
    }
}
