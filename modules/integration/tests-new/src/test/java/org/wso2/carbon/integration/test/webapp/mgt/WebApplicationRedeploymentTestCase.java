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
package org.wso2.carbon.integration.test.webapp.mgt;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;

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
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void testWebApplicationDeploymentAndAccessibility() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        webAppAdminClient.warFileUplaoder(ProductConstant.getResourceLocations(ProductConstant.APP_SERVER_NAME) + File.separator + "war"
                                          + File.separator + webAppFileName);

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName)
                , "Web Application Deployment failed");
        String webAppURL = asServer.getWebAppURL() + "/" + webAppPath;
        assertEquals(getPage(webAppURL).getData().trim(), "<response>Original Content</response>", "Web app invocation fail");
    }

    @Test(groups = "wso2.as", description = "Redeployment of web application")
    public void testWebApplicationRedeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(ProductConstant.getResourceLocations(ProductConstant.APP_SERVER_NAME)
                                          + File.separator + "war" + File.separator + "duplicateWar"
                                          + File.separator + webAppFileName);
        //wait for application to be redeployed
        Thread.sleep(30000);
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName)
                , "Web Application Redeployment failed. Application not deployed");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started", "State mismatched after redeploying web application");
        String webAppURL = asServer.getWebAppURL() + "/" + webAppPath;
        assertEquals(getPage(webAppURL).getData().trim(), "<response>Original Content changed</response>"
                , "Changes done to web app not reflected after redeployment");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName);
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName),
                   "Web Application unDeployment failed");

        String webAppURL = asServer.getWebAppURL() + "/" + webAppPath;
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");
    }

    private HttpResponse getPage(String webAppUrl) throws IOException {
        return HttpRequestUtil.sendGetRequest(webAppUrl, null);
    }
}
