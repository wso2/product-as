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

import org.apache.axis2.AxisFault;
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
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * In this class we upload web app to manager node, and access the same web app in worker node to check depsync working properly
 * The web app returns the IP address of the host machine
 */

public class WebAppDepSyncTestCase extends ASPlatformBaseTest {

    private final String webAppFileName = "wso2appserver-samples-hello-webapp-5.3.0-SNAPSHOT.war";
    private final String webAppName = "wso2appserver-samples-hello-webapp-5.3.0-SNAPSHOT";
    private WebAppAdminClient webAppAdminClient;
    private AutomationContext managerNode;
    private AutomationContext workerNode1;
    private AutomationContext workerNode2;
    private AutomationContext lbNode;
    private String managerSessionCookie;
    private String webAppURLWorkerNode;

    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, AutomationUtilException, AxisFault {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        managerNode = getAutomationContextWithKey("manager");
        workerNode1 = getAutomationContextWithKey("worker-1");
        workerNode2 = getAutomationContextWithKey("worker-2");
        lbNode = getAutomationContextWithKey("lbwm");
        managerSessionCookie = login(managerNode);
        webAppAdminClient = new WebAppAdminClient(managerNode.getContextUrls().getBackEndUrl(),managerSessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void  clean() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, lbNode.getDefaultInstance().getHosts().get("worker"));
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
    }

    @Test(groups = "wso2.as", description = "Invoke web application and test dep sync",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApp() throws XPathExpressionException, IOException {

        //request are sent directly to the worker node , if dep sync works web app should send the IP of worker node
        String workerNode1_IP = workerNode1.getDefaultInstance().getHosts().get("default");
        int depSyncTimeOut = 30000;
        webAppURLWorkerNode = workerNode1.getContextUrls().getWebAppURL();

        String webAppURLLocal = webAppURLWorkerNode + "/" + webAppName + "/";

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);

        long currentTime = System.currentTimeMillis();

        // check whether dep sync correctly deployed the app on worker node within 30 seconds

        while ((response.getResponseCode() != 200) || ((System.currentTimeMillis() - currentTime) < depSyncTimeOut)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //ignored the exception here since it will exit the execution
            }
            response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        }

        assertTrue(response.getData().contains(workerNode1_IP), "Invalid response");

    }

    @Test(groups = "wso2.as", description = "Check all nodes serve web application requests",
            dependsOnMethods = "testInvokeWebApp")
    public void testAllNodesServeWebAppRequests() throws XPathExpressionException, IOException {

        //requests are sent to load balancer node, reply should include the originated worker node IP

        String workerNode1_IP = workerNode1.getDefaultInstance().getHosts().get("default");
        String workerNode2_IP = workerNode2.getDefaultInstance().getHosts().get("default");
        String webAppURLLocal = lbNode.getContextUrls().getWebAppURL() + "/" + webAppName + "/";

        boolean worker1 = false;
        boolean worker2 = false;

        HttpResponse response;

        for (int i = 0; i < 10; i++) {

            response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);

            if (response.getData().contains(workerNode1_IP)) {
                worker1 = true;
            }

            if (response.getData().contains(workerNode2_IP)) {
                worker2 = true;
            }

        }

        assertTrue(worker1 && worker2, "Web app not served in both worker nodes");
    }

}
