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
import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebApplicationDeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);

    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                                          "artifacts" + File.separator + "AS" + File.separator + "war"
                                          + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as", description = "Invoke web application",
          dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApp() throws Exception {
        String webAppURLLocal = webAppURL + "/appServer-valied-deploymant-1.0.0";
        HttpClientUtil client = new HttpClientUtil();
        OMElement omElement = client.get(webAppURLLocal);
        assertEquals(omElement.toString(), "<status>success</status>", "Web app invocation fail");
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
          dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                   "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + "/appServer-valied-deploymant-1.0.0";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");
    }
}
