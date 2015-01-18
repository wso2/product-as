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
package org.wso2.appserver.integration.tests.webapp.mgt;

import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Stopping the web application , access it and verifying correct error handling
 * Starting the web application, access it to see whether the requests are being served as before
 */
public class WebApplicationStateChangingTestCase extends ASIntegrationTest {
    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void testWebApplicationDeploymentAndAccessibility() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                                          "artifacts" + File.separator + "AS" + File.separator + "war"
                                          + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
        String webAppURLLocal = webAppURL + "/" + webAppName;
        assertEquals(getPage(/*webAppURL*/webAppURLLocal).getData(), "<status>success</status>", "Web app invocation fail");
    }

    @Test(groups = "wso2.as", description = "Stop web application")
    public void testWebApplicationStop() throws Exception {
        assertTrue(webAppAdminClient.stopWebApp(webAppFileName, hostName), "failed to stop web application");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Stopped", "Stop State mismatched");
        String webAppURLLocal = webAppURL + "/" + webAppName;
        Assert.assertEquals(getPage(webAppURLLocal).getResponseCode(), 302, "Response code mismatch. Client request " +
                                                                       "got a response even after web app is stopped");
    }

    @Test(groups = "wso2.as", description = "Stop web application", dependsOnMethods = "testWebApplicationStop")
    public void testWebApplicationStart() throws Exception {
        assertTrue(webAppAdminClient.startWebApp(webAppFileName, hostName), "failed to start wen application");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started", "Start State mismatched");
        String webAppURLLocal = webAppURL + "/" + webAppName;
        assertEquals(getPage(webAppURLLocal).getData(), "<status>success</status>", "Web app invocation fail");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                 backendURL, sessionCookie, webAppName),
                   "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + "/appServer-valied-deploymant-1.0.0";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");
    }

    private HttpResponse getPage(String webAppUrl) throws IOException {
        return HttpRequestUtil.sendGetRequest(webAppUrl, null);
    }
}
