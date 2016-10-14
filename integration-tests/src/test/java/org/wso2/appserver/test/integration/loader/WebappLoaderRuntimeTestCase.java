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
package org.wso2.appserver.test.integration.loader;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This test case checkes whether the CXF runtime is properly engaged in the default distribution.
 *
 * @since 6.0.0
 */
public class WebappLoaderRuntimeTestCase extends TestBase {
    @Test(description = "tests whether the jaxrs_basic webapp can be invoked")
    public void testEnvironmentConfiguration() throws IOException {
        URL requestUrl = new URL(getBaseUrl() + "/jaxrs_basic-" + System.getProperty("appserver.version")
                + "/services/customerservice/customers/123/");
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCode = connection.getResponseCode();
        Assert.assertEquals(responseCode, 200, "Server Response Code");
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            Assert.assertTrue(sb.toString().contains("John"), "Response from jaxrs_basic is wrong, CXF runtime is " +
                    "not engaged");
        }
    }
}
