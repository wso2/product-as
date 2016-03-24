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
 */
package org.wso2.appserver.test.integration.utils;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * This Java class defines integration test cases for WSO2 Application Server specific configuration loading.
 *
 * @since 6.0.0
 */
public class ConfigurationLoaderTestCase extends TestBase {
    private static final String sampleContextRoot = "/configuration-loader";
    private static final String serverConfigurationTestResultAttrName = "isServerConfigurationUniform";
    private static final String contextConfigurationTestResultAttrName = "isContextConfigurationUniform";

    @Test(description = "Tests the server level descriptor content loading using a sample valve")
    public void testServerConfigurationLoading() throws IOException {
        URL requestUrl = new URL(getBaseUrl() + sampleContextRoot);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> fields = headerFields.get(serverConfigurationTestResultAttrName);
            if (fields != null) {
                Assert.assertTrue(Boolean.parseBoolean(fields.get(0)));
            } else {
                Assert.fail();
            }
        } else {
            Assert.fail();
        }
    }

    @Test(description = "Tests the context level descriptor content loading using a sample valve, for a sample context")
    public void testContextConfigurationLoading() throws IOException {
        URL requestUrl = new URL(getBaseUrl() + sampleContextRoot);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> fields = headerFields.get(contextConfigurationTestResultAttrName);
            if (fields != null) {
                Assert.assertTrue(Boolean.parseBoolean(fields.get(0)));
            } else {
                Assert.fail();
            }
        } else {
            Assert.fail();
        }
    }
}
