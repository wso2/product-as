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
package org.wso2.appserver.test.integration.javaee;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.testng.Assert.assertTrue;

public class JndiDumpTestCase extends TestBase {
    private static final String webAppLocalURL = "/ejb-examples";

    @Test(description = "test jndi dump")
    public void annotatedServletTest() throws Exception {
        String annotatedServletUrl = getBaseUrl() + webAppLocalURL + "/annotated";

        URL requestUrlGet = new URL(annotatedServletUrl);
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

            String result = sb.toString();

            //local bean ejb
            assertTrue(result.contains("@EJB=AnnotatedEJB[name=foo]"),
                    "Response doesn't contain @EJB=AnnotatedEJB[name=foo]");
            assertTrue(result.contains("@EJB.getName()=foo"),
                    "Response doesn't contain @EJB.getName()=foo");
            assertTrue(result.contains("@EJB.getDs()=org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource"),
                    "Response doesn't contain @EJB.getDs()=org.apache.openejb."
                            + "resource.jdbc.managed.local.ManagedDataSource");
            assertTrue(result.contains("JNDI=AnnotatedEJB[name=foo]"),
                    "Response doesn't contain JNDI=AnnotatedEJB[name=foo]");

            // local ejb
            assertTrue(
                    result.contains(
                            "@EJB=proxy=org.superbiz.servlet.AnnotatedEJBLocal;deployment=AnnotatedEJB;pk=null"));
            assertTrue(
                    result.contains(
                            "JNDI=proxy=org.superbiz.servlet.AnnotatedEJBLocal;deployment=AnnotatedEJB;pk=null"));

            //remote ejb
            assertTrue(
                    result.contains(
                            "@EJB=proxy=org.superbiz.servlet.AnnotatedEJBRemote;deployment=AnnotatedEJB;pk=null"));
            assertTrue(
                    result.contains(
                            "JNDI=proxy=org.superbiz.servlet.AnnotatedEJBRemote;deployment=AnnotatedEJB;pk=null"));

            //datasource
            assertTrue(result.contains("@Resource=org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource"));
            assertTrue(result.contains("JNDI=org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource"));

        }
    }
}
