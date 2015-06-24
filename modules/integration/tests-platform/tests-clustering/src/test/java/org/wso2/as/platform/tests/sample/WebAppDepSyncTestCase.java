/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.as.platform.tests.sample;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASPlatformBaseTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * In this class we upload web app to manager node, and access the same web app in worker node to check depsync working properly
 * The web app returns the IP address of the host machine
 */

public class WebAppDepSyncTestCase extends ASPlatformBaseTest {

    private final String webAppFileName = "wso2appserver-samples-hello-webapp-5.3.0-SNAPSHOT.war";
    private final String webAppName = "wso2appserver-samples-hello-webapp-5.3.0-SNAPSHOT";
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;
    private AutomationContext managerNode;
    private AutomationContext workerNode;
    private String managerSessionCookie;
    private String webAppURLWorkerNode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        managerNode = getAutomationContextWithKey("appServerInstance0001");
        workerNode = getAutomationContextWithKey("appserver.carbon-test.org");
        managerSessionCookie = login(managerNode);
        webAppAdminClient = new WebAppAdminClient(managerNode.getContextUrls().getBackEndUrl(),managerSessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void  clean() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, workerNode.getWorkerInstanceName());
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                        managerNode.getContextUrls().getBackEndUrl(), managerSessionCookie, webAppName),
                "Web Application unDeployment failed");
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps"
                + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                managerNode.getContextUrls().getBackEndUrl(), managerSessionCookie, webAppName)
                , "Web Application Deployment failed");

        Thread.sleep(30000);

    }

    @Test(groups = "wso2.as", description = "Invoke web application",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApp() throws Exception {
        webAppURLWorkerNode = workerNode.getContextUrls().getWebAppURL();

        String webAppURLLocal = webAppURLWorkerNode + "/" + webAppName + "/";
        log.info("Web app URL = " + webAppURLLocal);

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);

        log.info("Response from web app : " + response.getData() + "\n");

        assertTrue(response.getData().contains(workerNode.getDefaultInstance().getHosts().get("default")), "Invalid response");
    }


}
