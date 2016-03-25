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
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This test class is test the web application redeployment. once web application is deployed , Some modifications is made
 * and deploy again. Verify whether the web application gets deployed correctly. Once deployed, access the web application
 * and see whether changes done are reflected
 */

public class WebApplicationRedeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "appServer-webapp-content-changing.war";
    private final String webAppName = "appServer-webapp-content-changing";
    private final String webAppPath = webAppName + "/SimpleServlet";
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void testWebApplicationDeploymentAndAccessibility() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
        String webAppURLLocal = webAppURL + "/" + webAppPath;
        assertEquals(getPage(webAppURLLocal).getData().trim(), "<response>Original Content</response>", "Web app invocation fail");
    }

    @Test(groups = "wso2.as", description = "Redeployment of web application")
    public void testWebApplicationRedeployment() throws Exception {
        webAppAdminClient.uploadWarFile( FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "AS"   + File.separator + "war" + File.separator + "duplicateWar"
                                          + File.separator + webAppFileName);
        //wait for application to be redeployed
        Thread.sleep(30000);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Redeployment failed. Application not deployed");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started", "State mismatched after redeploying web application");
        String webAppURLLocal =  webAppURL + "/" + webAppPath;
        assertEquals(getPage(webAppURLLocal).getData().trim(), "<response>Original Content changed</response>"
                , "Changes done to web app not reflected after redeployment");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                   "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + "/" + webAppPath;
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");
    }

    private HttpResponse getPage(String webAppUrl) throws IOException {
        return ASHttpRequestUtil.sendGetRequest(webAppUrl, null);
    }
}
