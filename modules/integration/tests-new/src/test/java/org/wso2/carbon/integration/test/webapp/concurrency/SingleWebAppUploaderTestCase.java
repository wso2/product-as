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

package org.wso2.carbon.integration.test.webapp.concurrency;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.automation.utils.webapp.TestExceptionHandler;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class SingleWebAppUploaderTestCase extends ASIntegrationTest {

    public static String webAppFileName = "SimpleServlet";
    public static String filePath;
    WebAppWorker worker1;
    WebAppWorker worker2;
    WebAppWorker worker3;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        filePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                   "artifacts" + File.separator + "AS" + File.separator + "war"
                   + File.separator + "SimpleServlet.war";

    }

    @Test(groups = "wso2.as", description = "Deploying web application using multiple threads")
    public void testWebApplicationDeployment() throws Exception {
        worker1 = new WebAppWorker(asServer.getSessionCookie(), asServer.getBackEndUrl(), filePath);
        worker2 = new WebAppWorker(asServer.getSessionCookie(), asServer.getBackEndUrl(), filePath);
        worker3 = new WebAppWorker(asServer.getSessionCookie(), asServer.getBackEndUrl(), filePath);

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

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppFileName),
                   "Webapp has not deployed");

    }

    @Test(groups = "wso2.as", description = "multiple webapp uploader test case - invoke webapps",
          dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebapps() throws IOException {
        String webAppURL1 = asServer.getWebAppURL() + "/" + webAppFileName + "/simple-servlet";
        HttpResponse response1 = HttpRequestUtil.sendGetRequest(webAppURL1, null);
        assertTrue(response1.getData().contains("Hello, World"));
    }

    @AfterClass(enabled = true)
    public void testCleanup() throws Exception {
        worker1.deleteWebApp();
        worker2.deleteWebApp();
        worker3.deleteWebApp();

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppFileName),
                   "Webapp has not deployed");
    }
}
