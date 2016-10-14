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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.test.integration.statisticspublishing;

/**
 * Constants related to HTTP statistics publishing integration test.
 */
public class Constants {

    // Thrift server host.
    public static final String HOST = "127.0.0.1";
    public static final String THRIFT_PORT = "thrift.port";
    public static final String THRIFT_SSL_PORT = "thrift.ssl.port";

    // Thrift server ports.
    public static final int DEFAULT_THRIFT_PORT = 7711;
    public static final int DEFAULT_THRIFT_SSL_PORT = 7811;
    public static final int ORIGINAL_THRIFT_PORT = 7611;
    public static final int ORIGINAL_THRIFT_SSL_PORT = 7711;
    public static final int PORT_SCAN_MIN = 7800;
    public static final int PORT_SCAN_MAX = 7900;
}
