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
package org.wso2.appserver.integration.tests.webapp.spring;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
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
        super.init();
        webAppDeploymentDir = System.getProperty(ServerConstants.CARBON_HOME) + File.separator
                + "repository" + File.separator + "deployment" + File.separator + "server"
                + File.separator + "webapps" + File.separator;
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Deploying web application used spring by copying war" +
            " file to deployment directory")
    public void testWebApplicationHotDeployment() throws Exception {
        FileManager.copyJarFile(new File(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + "spring" + File.separator + webAppFileName)
        ,webAppDeploymentDir);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Invoke web application used spring",
            dependsOnMethods = "testWebApplicationHotDeployment")
    public void testInvokeWebApp() throws Exception {
        String webAppURLLocal = webAppURL + "/" + webAppName + "/spring/intro";
        HttpResponse response = getPage(webAppURLLocal);
        assertTrue(response.getData().contains("Welcome to Spring Travel")
                , "Web app invocation fail. Expected text not found");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "UnDeploying web application used spring by deleting war" +
            " file from deployment directory",
            dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        Assert.assertTrue(FileManager.deleteFile(webAppDeploymentDir + webAppFileName));
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
        String webAppURLLocal =  webAppURL + "/" + webAppName + "/spring/intro";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
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
