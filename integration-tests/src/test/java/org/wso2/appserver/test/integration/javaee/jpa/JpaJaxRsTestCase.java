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
package org.wso2.appserver.test.integration.javaee.jpa;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JpaJaxRsTestCase extends TestBase {
    private static final String webAppLocalURL = "/jpa-student-register-" + System.getProperty("appserver.version");

    @Test(description = "test jpa and jax-rs")
    public void testJpaRsGet() throws Exception {

        String getAll = "/student/getall";
        URL requestUrlGet = new URL(getBaseUrl() + webAppLocalURL + getAll);
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
            Assert.assertTrue(sb.toString().contains("{\"students\":[{\"name\":\"John\",\"index\":100}]}"),
                    "Response is invalid.");
        }

    }
}
