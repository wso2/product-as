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
package org.wso2.appserver.test.integration.javaee.cdi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CdiScopeTestCase extends TestBase {

    private static final Log log = LogFactory.getLog(CdiScopeTestCase.class);
    private static final String webAppLocalURL = "/cdi-scope-" + System.getProperty("appserver.version");

    @Test(description = "test cdi scopes, post construct & pre destroy with servlet")
    public void testCdiServlet() throws IOException {

        URL requestUrlGet = new URL(getBaseUrl() + webAppLocalURL);
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
            Assert.assertTrue(sb.toString().contains("Receptionist: Hi, this is the first time I meet youLift "
                            + "Operator: Hi, this is the first time I meet you"),
                    "Response doesn't contain the expected message");
        }

        URL requestUrlGet2 = new URL(getBaseUrl() + webAppLocalURL);
        HttpURLConnection connectionGet2 = (HttpURLConnection) requestUrlGet2.openConnection();
        connectionGet2.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCodeGet2 = connectionGet2.getResponseCode();
        Assert.assertEquals(responseCodeGet2, 200, "Server Response Code");
        if (responseCodeGet2 == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connectionGet2.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            Assert.assertTrue(sb.toString().contains(
                    "Receptionist: Hi, this is the first time I meet youLift Operator: Hi, I met you for 1 time(s)"),
                    "Response doesn't contain the expected message");
        }
    }
}
