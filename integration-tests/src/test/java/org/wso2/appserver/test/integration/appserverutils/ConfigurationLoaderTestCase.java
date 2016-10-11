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
package org.wso2.appserver.test.integration.appserverutils;

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
    private static final String configurationsTestServletMapping = "/configurations";
    private static final String systemPropertyTestServletMapping = "/system-property";

    @Test(description = "Tests the server level descriptor content loading using a sample valve")
    public void testServerConfigurationLoading() throws IOException {
        validateConfigurations(configurationsTestServletMapping, "isServerConfigurationUniform");
    }

    @Test(description = "Tests the context level descriptor content loading using a sample valve, for a sample context")
    public void testContextConfigurationLoading() throws IOException {
        validateConfigurations(configurationsTestServletMapping, "isContextConfigurationUniform");
    }

    @Test(description = "Tests the availability of keystore file")
    public void testKeystoreFileAvailability() throws IOException {
        validateConfigurations(systemPropertyTestServletMapping, "keyStoreFileAvailable");
    }

    @Test(description = "Tests the availability of trust-store file")
    public void testTrustStoreFileAvailability() throws IOException {
        validateConfigurations(systemPropertyTestServletMapping, "trustStoreFileAvailable");
    }

    @Test(description = "Tests whether the 'javax.net.ssl.keyStore' system property is correctly set")
    public void testKeystoreLocationSystemProperty() throws IOException {
        String expectedKeystoreRelativePath = "/conf/wso2/wso2carbon.jks";
        testSystemProperty("javax.net.ssl.keyStore", (getAppserverHome() + expectedKeystoreRelativePath)
                .replaceAll("\\\\", "/"));
    }

    @Test(description = "Tests whether the 'javax.net.ssl.keyStorePassword' system property is correctly set")
    public void testKeystorePasswordSystemProperty() throws IOException {
        testSystemProperty("javax.net.ssl.keyStorePassword", "wso2carbon");
    }

    @Test(description = "Tests whether the 'javax.net.ssl.keyStoreType' system property is correctly set")
    public void testKeystoreTypeSystemProperty() throws IOException {
        testSystemProperty("javax.net.ssl.keyStoreType", "JKS");
    }

    @Test(description = "Tests whether the 'javax.net.ssl.trustStore' system property is correctly set")
    public void testTrustStoreLocationSystemProperty() throws IOException {
        String expectedTrustStoreRelativePath = "/conf/wso2/client-truststore.jks";
        testSystemProperty("javax.net.ssl.trustStore", (getAppserverHome() + expectedTrustStoreRelativePath)
                .replaceAll("\\\\", "/"));
    }

    @Test(description = "Tests whether the 'javax.net.ssl.trustStorePassword' system property is correctly set")
    public void testTrustStorePasswordSystemProperty() throws IOException {
        testSystemProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    @Test(description = "Tests whether the 'javax.net.ssl.trustStoreType' system property is correctly set")
    public void testTrustStoreTypeSystemProperty() throws IOException {
        testSystemProperty("javax.net.ssl.trustStoreType", "JKS");
    }

    private void validateConfigurations(String servletMapping, String returnHeaderName) throws IOException {
        HttpURLConnection connection = getConnection(servletMapping);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> fields = headerFields.get(returnHeaderName);
            if (fields != null) {
                Assert.assertTrue(Boolean.parseBoolean(fields.get(0)));
            } else {
                Assert.fail();
            }
        } else {
            Assert.fail();
        }
    }

    private void testSystemProperty(String systemPropertyKey, String expectedValue) throws IOException {
        HttpURLConnection connection = getConnection(systemPropertyTestServletMapping);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> fields = headerFields.get(systemPropertyKey);
            if (fields != null) {
                Assert.assertTrue(fields.get(0).equals(expectedValue));
            } else {
                Assert.fail();
            }
        } else {
            Assert.fail();
        }
    }

    private HttpURLConnection getConnection(String servletMapping) throws IOException {
        URL requestUrl = new URL(getBaseUrl() + "/configuration-loader-" + System.getProperty("appserver.version") +
                servletMapping);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        return connection;
    }
}
