/*
* Copyright 2004,2013 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.appserver.test.integration.javaee.jpa;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JpaServletCdiTestCase extends TestBase {
    private static final String webAppLocalURL = "/jpa-order-processor-" + System.getProperty("appserver.version");

    @Test(description = "test jpa and jax-ws")
    public void testJpaServletCdi() throws Exception {
        URL requestUrlGet = new URL(
                getBaseUrl() + webAppLocalURL + "/order?item=Item0001&quantity=100&placeOrder=Place+Order");
        HttpURLConnection connectionGet = (HttpURLConnection) requestUrlGet.openConnection();
        connectionGet.setRequestMethod(TestConstants.HTTP_POST_METHOD);
        int responseCodeGet = connectionGet.getResponseCode();
        Assert.assertEquals(responseCodeGet, 200, "Server Response Code");
        if (responseCodeGet == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connectionGet.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            Assert.assertTrue(sb.toString().contains(
                    "        <tr>        <td>1        </td>        <td>Item0001        </td>        <td>100        </td>"),
                    "Response doesn't contain expected data");
        }
    }
}
