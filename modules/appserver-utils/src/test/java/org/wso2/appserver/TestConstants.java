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
 * This class defines constants used within the unit-tests of Application Server Utils module.
 *
 * @since 6.0.0
 */
public class TestConstants {
    protected static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    protected static final String CATALINA_BASE = "tomcat-base";
    protected static final String SAMPLE_CONFIGURATION_FILE = "sample.xml";
    protected static final String SAMPLE_WSO2_SERVER_DESCRIPTOR = "sample-wso2as.xml";
    protected static final String SAMPLE_WSO2_SERVER_DESCRIPTOR_SCHEMA = "sample-wso2as.xsd";

    /**
     * Prevents instantiating this class.
     */
    private TestConstants() {
    }
}
