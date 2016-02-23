/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.appserver.monitoring;

/**
 * The default values used for authentication and configurations of the valve.
 */
public class DefaultConfigurationConstants {

    public static final String USERNAME = "";
    public static final String PASSWORD = "";
    public static final String CONFIG_FILE_FOLDER = "conf/wso2/Webapp_Statistics_Monitoring";
    public static final String URL = "tcp://127.0.0.1:7611";
    public static final String STREAM_ID = "org.wso2.http.stats:1.0.0";

    /**
     * instantiating is not needed for this class. private constructor to block that.
     */
    private DefaultConfigurationConstants(){

    }
}

