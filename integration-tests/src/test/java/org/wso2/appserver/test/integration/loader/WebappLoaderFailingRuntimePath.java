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
 *
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
 * This sample test class demonstrate how to write a test cases which runs on integration test phase.
 * <p>
 * Every class that requires run in integration test phase should have prefix of <strong>IT</strong>.
 *
 * @since 6.0.0
 */
public class WebappLoaderFailingRuntimePath extends TestBase {

    @Test(description = "Checks if the server is no running")
    public void testEnvironmentConfiguration() throws IOException {
        URL requestUrl = new URL(getBaseUrl() + "/jaxrs_basic/services/customerservice/customers/123/");
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCode = connection.getResponseCode();
        Assert.assertNotEquals(responseCode, 200, "Check Server Response Code is not equals to 200");

    }
}
