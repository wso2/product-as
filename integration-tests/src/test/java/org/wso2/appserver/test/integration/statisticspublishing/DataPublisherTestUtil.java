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
 */
package org.wso2.appserver.test.integration.statisticspublishing;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Util methods related to HTTP statistics publishing integration test.
 */
public class DataPublisherTestUtil {
    public static final String LOCAL_HOST = "localhost";

    /**
     * Sets the trust store parameters.
     */
    public static void setTrustStoreParams() {
        Path trustStorePath = Paths.get("src", "test", "resources", "client-truststore.jks").toAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", trustStorePath.toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    /**
     * Sets the key store parameters.
     */
    public static void setKeyStoreParams() {
        Path keyStorePath = Paths.get("src", "test", "resources", "wso2carbon.jks").toAbsolutePath();
        System.setProperty("Security.KeyStore.Location", keyStorePath.toString());
        System.setProperty("Security.KeyStore.Password", "wso2carbon");
    }

    /**
     * Returns the absolute path of data-bridge-config.xml.
     *
     * @return absolute path of data-bridge-config.xml
     */
    public static String getDataBridgeConfigPath() {
        Path dataBridgeConfigPath = Paths.get("src", "test", "resources", "data-bridge-config.xml").toAbsolutePath();
        return dataBridgeConfigPath.toString();
    }

    /**
     * Returns the absolute path of the org.wso2.http.stats_1.0.0.json
     *
     * @return absolute path of the org.wso2.http.stats_1.0.0.json
     */
    public static String getStreamDefinitionPath() {
        Path streamDefinitionPath = Paths.get("src", "test", "resources", "org.wso2.http.stats_1.0.0.json")
                .toAbsolutePath();
        return streamDefinitionPath.toString();
    }

}
