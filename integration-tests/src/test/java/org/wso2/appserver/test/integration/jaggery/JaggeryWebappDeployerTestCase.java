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
package org.wso2.appserver.test.integration.jaggery;

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
 * This test case checks the jaggery runtime support in AS.
 *
 * @since 6.0.0
 */
public class JaggeryWebappDeployerTestCase extends TestBase {
    @Test(description = "tests whether the jaggery webapp can be invoked")
    public void testEnvironmentConfiguration() throws IOException {
        //send an initial get request
        URL requestUrlGet = new URL(getBaseUrl() + "/coffeeshop/orders/");
        HttpURLConnection connectionGet = (HttpURLConnection) requestUrlGet.openConnection();
        connectionGet.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCodeGet = connectionGet.getResponseCode();
        Assert.assertEquals(responseCodeGet, 200, "Server Response Code");
        if (responseCodeGet == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connectionGet.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            Assert.assertTrue(sb.toString().contains("Sorry, Orders are not exsit"), "Response from coffeeshop is "
                    + "wrong");
        }

        String cookie = (connectionGet.getHeaderFields().get("Set-Cookie").get(0));

        //send post request to place an order
        URL requestUrlPost = new URL(getBaseUrl() + "/coffeeshop/orders?order=Espresso");
        HttpURLConnection connectionPost = (HttpURLConnection) requestUrlPost.openConnection();
        connectionPost.setRequestMethod(TestConstants.HTTP_POST_METHOD);
        connectionPost.addRequestProperty("Cookie", cookie);

        int responseCodePost = connectionPost.getResponseCode();
        Assert.assertEquals(responseCodePost, 200, "Server Response Code");
        if (responseCodePost == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connectionPost.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String expectedResponse = "\"ORDER_ID\" : 0.0, \"ADDITION\" : \"\", \"DRINK\" : \"Espresso\", \"COST\" : "
                    + "\"$5\", \"STATUS\" : \"In Progress\", \"PAY\" : false";
            Assert.assertTrue(sb.toString().contains(expectedResponse), "Response from coffeeshop is "
                    + "wrong");
        }

        //send get request again to check for placed order
        URL requestUrlGet2 = new URL(getBaseUrl() + "/coffeeshop/orders/");
        HttpURLConnection connectionGet2 = (HttpURLConnection) requestUrlGet2.openConnection();
        connectionGet2.setRequestMethod(TestConstants.HTTP_GET_METHOD);
        connectionGet2.addRequestProperty("Cookie", cookie);

        int responseCodeGet2 = connectionGet2.getResponseCode();
        Assert.assertEquals(responseCodeGet2, 200, "Server Response Code");
        if (responseCodeGet2 == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connectionGet2.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String expectedResponse = "\"ORDER_ID\" : 0.0, \"ADDITION\" : \"\", \"DRINK\" : \"Espresso\", \"COST\" : "
                    + "\"$5\", \"STATUS\" : \"In Progress\", \"PAY\" : false";
            Assert.assertTrue(sb.toString().contains(expectedResponse), "Response from coffeeshop is "
                    + "wrong");
        }

    }
}
