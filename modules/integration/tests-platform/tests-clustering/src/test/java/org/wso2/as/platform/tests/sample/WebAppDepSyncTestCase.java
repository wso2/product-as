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
*
* puppet          192.168.57.160  Ubuntu 14.04.1  203.94.95.139
* as530-node-01   192.168.57.161  Ubuntu 14.04.1  203.94.95.152 mgr
* as530-node-02   192.168.57.162  Ubuntu 14.04.1  203.94.95.195
* as530-node-03   192.168.57.163  Ubuntu 14.04.1  203.94.95.196
* as530-node-04   192.168.57.164  Ubuntu 14.04.1  203.94.95.197 wkr1
* as530-node-05   192.168.57.165  Ubuntu 14.04.1  203.94.95.198 wkr2
*
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
import java.lang.System;
import java.lang.Thread;
import java.util.HashMap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * In this class we upload web app to manager node, and access the same web app in worker node
 * to check depsync working properly
 * The web app returns the IP address of the host machine in the response
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
    HashMap<String, String> ipCache = new HashMap<String, String>();
    private boolean webAppUndeployed = false;

    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, AutomationUtilException, AxisFault {
        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);
        managerNode = getAutomationContextWithKey("manager");
        workerNode1 = getAutomationContextWithKey("worker-1");
        workerNode2 = getAutomationContextWithKey("worker-2");
        lbNode = getAutomationContextWithKey("lbwm");
        managerSessionCookie = login(managerNode);
        webAppAdminClient = new WebAppAdminClient(managerNode.getContextUrls().getBackEndUrl(), managerSessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        if (webAppUndeployed = false) {
            webAppAdminClient.deleteWebAppFile(webAppFileName, lbNode.getDefaultInstance().getHosts().get("worker"));
            assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                               managerNode.getContextUrls().getBackEndUrl(), managerSessionCookie, webAppName),
                       "Web Application Undeployment failed");
        }
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
        int depSyncTimeOut = 90000;
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
        // As per the web app logic if a valid IP address could not be found, following is returned
        //  <li>InetAddress: error</li>
        assertTrue(!response.getData().contains("error"), "Invalid response");
    }

    @Test(groups = "wso2.as", description = "Check all nodes serve web application requests",
            dependsOnMethods = "testInvokeWebApp")
    public void testAllNodesServeWebAppRequests() throws XPathExpressionException, IOException {

        // 10 requests are sent to load balancer node, reply should include the originated worker node IP
        String webAppURLLocal = lbNode.getContextUrls().getWebAppURL() + "/" + webAppName + "/";
        HttpResponse response;
        String modifiedWebappResponse;

        for (int i = 0; i < 10; i++) {

            response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
            modifiedWebappResponse = response.getData().toString().replaceAll("\"", "");
            // Extract the IP Address from the response received from the web app and store it in the
            // HashMap. Duplicate entries will be disregarded.
            ipCache.put(modifiedWebappResponse.substring(modifiedWebappResponse.indexOf("<li>InetAddress: ") + 17,
                                                         modifiedWebappResponse.indexOf("</li>")), "Node IP");
        }

        // Since the web app will be served via both worker nodes, the HashMap should contain more
        // than one unique IP Address entries; in this case 2 unique IP entries.
        assertTrue(ipCache.size() > 1, "Web app not served in both worker nodes");
    }

    @Test(groups = "wso2.as", description = "Undeploying web application",
            dependsOnMethods = "testAllNodesServeWebAppRequests")
    public void testWebApplicationUndeployment() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, lbNode.getDefaultInstance().getHosts().get("worker"));
        webAppUndeployed = true;
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                           managerNode.getContextUrls().getBackEndUrl(), managerSessionCookie, webAppName),
                   "Web Application Undeployment failed");
    }

    @Test(groups = "wso2.as", description = "Check all nodes undeployed web application",
            dependsOnMethods = "testWebApplicationUndeployment")
    public void testAllNodesUndeployedWebApp() throws Exception {
        // Send web app requests to worker nodes after undeploying web app in manager node
        String workerNode1_IP = workerNode1.getDefaultInstance().getHosts().get("default");
        int depSyncTimeOut = 90000;
        webAppURLWorkerNode = workerNode1.getContextUrls().getWebAppURL();

        String webAppURLLocal = webAppURLWorkerNode + "/" + webAppName + "/";

        //Thread.sleep(40000);

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);

        long currentTime = System.currentTimeMillis();

        while ((response.getResponseCode() != 302) || ((System.currentTimeMillis() - currentTime) < depSyncTimeOut)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //ignored the exception here since it will exit the execution
            }
            response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        }

        // check whether dep sync correctly undeployed the app on worker node within 30 seconds
        assertTrue(response.getResponseCode() == 302, "Undeploying web app from all nodes failed");
    }

}
