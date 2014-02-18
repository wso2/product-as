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
package org.wso2.carbon.integration.test.webapp.spring;

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

import static org.testng.Assert.assertTrue;

/**
 * Spring web application deployment, redeployment, reloading, stopping and starting testing
 */
public class SpringApplicationDeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "booking-faces.war";
    private final String webAppName = "booking-faces";
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

    }

    @Test(groups = "wso2.as", description = "Deploying web application used spring")
    public void testSpringWebApplicationDeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                                          "artifacts" + File.separator + "AS" + File.separator + "war"
                                          + File.separator + "spring" + File.separator + webAppFileName);

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName)
                , "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as", description = "Invoke web application used spring",
          dependsOnMethods = "testSpringWebApplicationDeployment")
    public void testInvokeWebApp() throws Exception {
        getAndVerifyApplicationPage();
    }

    @Test(groups = "wso2.as", description = "reload web application used spring", dependsOnMethods = "testInvokeWebApp")
    public void testWebApplicationReloading() throws Exception {
        webAppAdminClient.reloadWebApp(webAppFileName);
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started", "State mismatched after reloading web application");
        getAndVerifyApplicationPage();
    }

    @Test(groups = "wso2.as", description = "Stop web application", dependsOnMethods = "testWebApplicationReloading")
    public void testWebApplicationStop() throws Exception {
        assertTrue(webAppAdminClient.stopWebApp(webAppFileName), "failed to stop web application");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Stopped", "Stop State mismatched");
        String webAppURL = asServer.getWebAppURL() + "/" + webAppName;
        Assert.assertEquals(HttpRequestUtil.sendGetRequest(webAppURL, null).getResponseCode(), 302, "Response code mismatch. Client request " +
                                                                                                    "got a response even after web app is stopped");
    }

    @Test(groups = "wso2.as", description = "Stop web application", dependsOnMethods = "testWebApplicationStop")
    public void testWebApplicationStart() throws Exception {
        assertTrue(webAppAdminClient.startWebApp(webAppFileName), "failed to start wen application");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started", "Start State mismatched");
        getAndVerifyApplicationPage();
    }

    @Test(groups = "wso2.as", description = "Redeployment of web application used spring"
            , dependsOnMethods = "testWebApplicationStart")
    public void testWebApplicationRedeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                                          "artifacts" + File.separator + "AS" + File.separator + "war"
                                          + File.separator + "spring" + File.separator + webAppFileName);
        //wait for application to be redeployed
        Thread.sleep(45000);
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName)
                , "Web Application Redeployment failed. Application not deployed");
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started"
                , "State mismatched after redeploying web application");
        getAndVerifyApplicationPage();
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
          dependsOnMethods = "testWebApplicationRedeployment")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName);
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName),
                   "Web Application unDeployment failed");

        String webAppURL = asServer.getWebAppURL() + "/" + webAppName + "/spring/intro";
        HttpRequestUtil client = new HttpRequestUtil();
        HttpResponse response = client.sendGetRequest(webAppURL, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if (webAppAdminClient.getWebApplist(webAppName).contains(webAppName)) {
            webAppAdminClient.deleteWebAppFile(webAppFileName);
        }
    }

    private void getAndVerifyApplicationPage() throws IOException {
        String webAppURL = asServer.getWebAppURL() + "/" + webAppName + "/spring/intro";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        assertTrue(response.getData().contains("Welcome to Spring Travel")
                , "Web app invocation fail. Expected text not found");
    }
}
