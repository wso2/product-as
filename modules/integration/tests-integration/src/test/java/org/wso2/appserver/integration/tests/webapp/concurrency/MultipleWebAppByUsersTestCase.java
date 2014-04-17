/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.appserver.integration.tests.webapp.concurrency;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.TestExceptionHandler;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class MultipleWebAppByUsersTestCase extends ASIntegrationTest {

    public static String webAppFileName1 = "Calendar";
    public static String webAppFileName2 = "myServletWAR";
    public static String webAppFileName3 = "sample";
    public static String filePath1;
    public static String filePath2;
    public static String filePath3;
    WebAppWorker worker1;
    WebAppWorker worker2;
    WebAppWorker worker3;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        filePath1 = FrameworkPathUtil.getSystemResourceLocation() +
                    "artifacts" + File.separator + "AS" + File.separator + "war"
                    + File.separator + "Calendar.war";

        filePath2 = FrameworkPathUtil.getSystemResourceLocation() +
                    "artifacts" + File.separator + "AS" + File.separator + "war"
                    + File.separator + "myServletWAR.war";

        filePath3 = FrameworkPathUtil.getSystemResourceLocation() +
                    "artifacts" + File.separator + "AS" + File.separator + "war"
                    + File.separator + "sample.war";
    }

    @Test(groups = "wso2.as", description = "Deploying web application using multiple threads")
    public void testWebApplicationDeployment() throws Exception {

        super.init("superTenant", "userKey1");//build the environment by user2
        worker1 = new WebAppWorker(sessionCookie, backendURL, filePath1);
        super.init("superTenant", "userKey2");//build the environment by user3
        worker2 = new WebAppWorker(sessionCookie, backendURL, filePath2);
        super.init("superTenant", "userKey3");//build the environment by user4
        worker3 = new WebAppWorker(sessionCookie, backendURL, filePath3);

        TestExceptionHandler exHandler = new TestExceptionHandler();

        Thread t1 = new Thread(worker1);
        t1.start();

        Thread t2 = new Thread(worker2);
        t2.start();

        Thread t3 = new Thread(worker3);
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException ignored) {
        }

        if (exHandler.throwable != null) {
            exHandler.throwable.printStackTrace();
            exHandler.throwable.getMessage();
        }

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppFileName1),
                   "Webapp has not deployed");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppFileName2),
                   "Webapp has not deployed");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppFileName3),
                   "Webapp has not deployed");

    }

    @Test(groups = "wso2.as", description = "multiple webapp uploader test case - invoke webapps",
          dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebapps() throws IOException {
        String webAppURL1 = webAppURL + "/" + webAppFileName1 + "/Calendar.html";
        HttpResponse response1 = HttpRequestUtil.sendGetRequest(webAppURL1, null);
        assertTrue(response1.getData().contains("<h1>GWT Calendar</h1>"), "Webapp invocation fail");

        String webAppURL2 = webAppURL + "/" + webAppFileName2 + "/hello";
        HttpResponse response2 = HttpRequestUtil.sendGetRequest(webAppURL2, null);
        assertTrue(response2.getData().contains("HelloServlet in myServletWAR!"),
                   "Webapp invocation fail");

        String webAppURL3 = webAppURL + "/" + webAppFileName3 + "/hello.jsp";
        HttpResponse response3 = HttpRequestUtil.sendGetRequest(webAppURL3, null);
        assertTrue(response3.getData().contains("Sample Application JSP Page"),
                   "Webapp invocation fail");
    }

    @AfterClass(enabled = true)
    public void testCleanup() throws Exception {
        worker1.deleteWebApp();
        worker2.deleteWebApp();
        worker3.deleteWebApp();

        super.init("superTenant", "userKey1");//build the environment by user2
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppFileName1),
                   "Webapp has not deployed");

        super.init("superTenant", "userKey2");//build the environment by user2
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppFileName2),
                   "Webapp has not deployed");

        super.init("superTenant", "userKey3");//build the environment by user2
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppFileName2),
                   "Webapp has not deployed");
    }
}
