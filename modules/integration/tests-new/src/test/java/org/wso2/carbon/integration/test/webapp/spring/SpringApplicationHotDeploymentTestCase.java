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
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.core.annotations.ExecutionMode;
import org.wso2.carbon.automation.core.annotations.SetEnvironment;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.fileutils.FileManager;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * Deploying the  web application used spring by dropping the war file to the repository/deployment/server/webapp
 * folder & invoke once deployed successfully
 */
public class SpringApplicationHotDeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "booking-faces.war";
    private final String webAppName = "booking-faces";
    private String webAppDeploymentDir;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        if (ExecutionMode.tenant.name().equalsIgnoreCase(FrameworkFactory.getFrameworkProperties(
                ProductConstant.APP_SERVER_NAME).getEnvironmentSettings().executionMode())) {
            throw new Exception("This test is not Implemented for tenants");
            //todo - build the tenant web app deployment path
            /*webAppDeploymentDir = System.getProperty(ServerConstants.CARBON_HOME) + File.separator
                                  + "repository" + File.separator + "deployment" + File.separator + "server"
                                  + File.separator + "webapps" + File.separator;*/
        } else {
            webAppDeploymentDir = System.getProperty(ServerConstants.CARBON_HOME) + File.separator
                                  + "repository" + File.separator + "deployment" + File.separator + "server"
                                  + File.separator + "webapps" + File.separator;
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.integration_all})
    @Test(groups = "wso2.as", description = "Deploying web application used spring by copying war" +
                                            " file to deployment directory")
    public void testWebApplicationHotDeployment() throws Exception {
        FileManager.copyJarFile(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                                         "artifacts" + File.separator + "AS" + File.separator + "war"
                                         + File.separator + "spring" + File.separator + webAppFileName)
                , webAppDeploymentDir);

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName)
                , "Web Application Deployment failed");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.integration_all})
    @Test(groups = "wso2.as", description = "Invoke web application used spring",
          dependsOnMethods = "testWebApplicationHotDeployment")
    public void testInvokeWebApp() throws Exception {
        String webAppURL = asServer.getWebAppURL() + "/" + webAppName + "/spring/intro";
        HttpResponse response = getPage(webAppURL);
        assertTrue(response.getData().contains("Welcome to Spring Travel")
                , "Web app invocation fail. Expected text not found");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.integration_all})
    @Test(groups = "wso2.as", description = "UnDeploying web application used spring by deleting war" +
                                            " file from deployment directory",
          dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        Assert.assertTrue(FileManager.deleteFile(webAppDeploymentDir + webAppFileName));
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName),
                   "Web Application unDeployment failed");

        String webAppURL = asServer.getWebAppURL() + "/" + webAppName + "/spring/intro";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");
    }

    @AfterClass(alwaysRun = true, description = "Delete Artifact if it is remained due to test failure")
    public void destroy() {
        if (new File(webAppDeploymentDir + webAppFileName).exists()) {
            FileManager.deleteFile(webAppDeploymentDir + webAppFileName);
        }
    }

    private HttpResponse getPage(String webAppUrl) throws IOException {
        return HttpRequestUtil.sendGetRequest(webAppUrl, null);
    }
}
