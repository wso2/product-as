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

import org.apache.commons.httpclient.HttpStatus;
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
 * In this class the DepSync deployment/undeployment functionality is tested using a webapp
 * Webapp is designed to return the IP address of the host machine in the response
 */
public class WebAppDepSyncTestCase extends ASPlatformBaseTest {

    private final int DEP_SYNC_TIME_OUT = 90000;
    private final int DEP_SYNC_RETRY_PERIOD = 1000;
    private final String webAppName = "wso2appserver-samples-hello-webapp-5.3.0-SNAPSHOT";
    private final String webAppFileName = webAppName + ".war";
    private WebAppAdminClient webAppAdminClient;
    private AutomationContext managerNode;
    private AutomationContext lbNode;
    private String managerSessionCookieDirect;
    private String webAppURLLocalWorker1;
    private String webAppURLLocalWorker2;
    private boolean webAppUndeployed = false;
    private final String MANAGER = "manager";
    private final String WORKER_1 = "worker-1";
    private final String WORKER_2 = "worker-2";
    private final String LB = "lbwm";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.initCluster(TestUserMode.SUPER_TENANT_ADMIN);

        // Used to login and get back end URLs
        managerNode = getAutomationContextWithKey(MANAGER);
        lbNode = getAutomationContextWithKey(LB);

        webAppURLLocalWorker1 =
                getAutomationContextWithKey(WORKER_1).getContextUrls().getWebAppURL() + "/" + webAppName + "/";
        webAppURLLocalWorker2 =
                getAutomationContextWithKey(WORKER_2).getContextUrls().getWebAppURL() + "/" + webAppName + "/";

        // This Direct mgr session is used to validate whether the web application is deployed/undeployed as expected
        managerSessionCookieDirect = login(managerNode);

        // WebAppAdminClient is initialized with LB manager session which is used to deploy/undeploy web applications
        webAppAdminClient = new WebAppAdminClient(lbNode.getContextUrls().getBackEndUrl(), login(lbNode));

    }

    @Test(groups = "wso2.as", description = "Deploying web application in manager node")
    public void testWebApplicationDeploymentOnManager() throws Exception {

        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + webAppFileName);

        log.info(" ----- Validating web application deployment via manager node : " + managerNode.getContextUrls()
                .getBackEndUrl());

        assertTrue(WebAppDeploymentUtil
                .isWebApplicationDeployed(managerNode.getContextUrls().getBackEndUrl(), managerSessionCookieDirect,
                        webAppName), "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "Validating web application deployment on worker nodes",
            dependsOnMethods = "org.wso2.as.platform.tests.sample.WebAppDepSyncTestCase.testWebApplicationDeploymentOnManager")
    public void testWebApplicationDeploymentOnWorkers() throws Exception {

        // Requests are sent directly to worker nodes
        log.info(" ----- Validating web application deployment via worker node-1 : " + webAppURLLocalWorker1);

        HttpResponse worker1Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker1, null);

        long currentTime = System.currentTimeMillis();

        // check whether dep sync correctly deployed the app on worker1 node within DEP_SYNC_TIME_OUT
        while (((System.currentTimeMillis() - currentTime) < DEP_SYNC_TIME_OUT)) {

            if ((worker1Response.getResponseCode() == HttpStatus.SC_OK)) {
                break;
            }
            try {
                Thread.sleep(DEP_SYNC_RETRY_PERIOD);
            } catch (InterruptedException e) {
                //ignored the exception here since it will exit the execution
            }
            worker1Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker1, null);
        }

        log.info(" ----- ResponseCode received worker node-1 : " + worker1Response.getResponseCode());

        // Check whether the web app is serving the HTTP requests
        assertTrue(worker1Response.getResponseCode() == HttpStatus.SC_OK,
                "Failed to deploy web app on cluster worker1 node within the given time period");

        log.info(" ----- Validating web application deployment via worker node-2 : " + webAppURLLocalWorker2);

        HttpResponse worker2Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker2, null);

        // Reset currentTime for worker node 2
        currentTime = System.currentTimeMillis();

        // check whether dep sync correctly deployed the app on worker2 node within DEP_SYNC_TIME_OUT
        while (((System.currentTimeMillis() - currentTime) < DEP_SYNC_TIME_OUT)) {

            if ((worker2Response.getResponseCode() == HttpStatus.SC_OK)) {
                break;
            }
            try {
                Thread.sleep(DEP_SYNC_RETRY_PERIOD);
            } catch (InterruptedException e) {
                //ignored the exception here since it will exit the execution
            }
            worker2Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker2, null);
        }

        log.info(" ----- ResponseCode received worker node-2 : " + worker2Response.getResponseCode());

        // Check whether the web app is serving the HTTP requests as expected
        assertTrue(worker2Response.getResponseCode() == HttpStatus.SC_OK,
                "Failed to deploy web app on cluster worker2 node within the given time period");
    }

    @Test(groups = "wso2.as", description = "Undeploying web application on manager node",
            dependsOnMethods = "org.wso2.as.platform.tests.sample.WebAppDepSyncTestCase.testWebApplicationDeploymentOnWorkers")
    public void testWebApplicationUndeploymentOnManager() throws Exception {

        // The worker profile from automation xml is used since the virtual host applicable
        // to the deployed web application is appserver.carbon-test.org
        // Therefore this domain should be passed with the webapp name to undeploy the webapp from manager node.
        // Note that the webAppAdminClient used here is initialized by the managerSessionCookieLB mgr session
        webAppAdminClient.deleteWebAppFile(webAppFileName, lbNode.getDefaultInstance().getHosts().get("worker"));

        log.info(" ----- Validating web application undeployment via worker manager node : " + managerNode
                .getContextUrls().getBackEndUrl());

        assertTrue(WebAppDeploymentUtil
                .isWebApplicationUnDeployed(managerNode.getContextUrls().getBackEndUrl(), managerSessionCookieDirect,
                        webAppName), "Web Application Undeployment failed");
        webAppUndeployed = true;
    }

    @Test(groups = "wso2.as", description = "Validating undeployment on all worker nodes",
            dependsOnMethods = "org.wso2.as.platform.tests.sample.WebAppDepSyncTestCase.testWebApplicationUndeploymentOnManager",
            timeOut = 120000)
    public void testWebApplicationUndeploymentOnWorkers() throws Exception {

        // Requests are sent directly to worker nodes after undeploying web app in manager node
        log.info(" ----- Validating web application undeployment via worker node-1 : " + webAppURLLocalWorker1);

        HttpResponse worker1Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker1, null);

        long currentTime = System.currentTimeMillis();

        while (((System.currentTimeMillis() - currentTime) < DEP_SYNC_TIME_OUT)) {

            if (worker1Response.getResponseCode() == HttpStatus.SC_NOT_FOUND
                    || worker1Response.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                break;
            }

            try {
                Thread.sleep(DEP_SYNC_RETRY_PERIOD);
            } catch (InterruptedException e) {
                //ignored the exception here since it will exit the execution
            }
            worker1Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker1, null);
        }

        log.info(" ----- ResponseCode received worker node-1 : " + worker1Response.getResponseCode());

        // Validate dep sync undeployment on worker node within DEP_SYNC_TIME_OUT
        // After undeployment, HTTP response should be either HTTP 404 or HTTP 302
        assertTrue(worker1Response.getResponseCode() == HttpStatus.SC_NOT_FOUND
                        || worker1Response.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY,
                "Undeploying web app from all nodes failed");

        log.info(" ----- Validating web application undeployment via worker node-2 : " + webAppURLLocalWorker2);

        HttpResponse worker2Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker2, null);

        // Reset currentTime for worker node 2
        currentTime = System.currentTimeMillis();

        while (((System.currentTimeMillis() - currentTime) < DEP_SYNC_TIME_OUT)) {

            if (worker2Response.getResponseCode() == HttpStatus.SC_NOT_FOUND
                    || worker2Response.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                break;
            }

            try {
                Thread.sleep(DEP_SYNC_RETRY_PERIOD);
            } catch (InterruptedException e) {
                //ignored the exception here since it will exit the execution
            }
            worker2Response = HttpRequestUtil.sendGetRequest(webAppURLLocalWorker2, null);
        }

        log.info(" ----- ResponseCode received worker node-2 : " + worker2Response.getResponseCode());

        // Validate dep sync undeployment on worker node within DEP_SYNC_TIME_OUT
        // After undeployment, HTTP response should be either HTTP 404 or HTTP 302
        assertTrue(worker2Response.getResponseCode() == HttpStatus.SC_NOT_FOUND
                        || worker2Response.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY,
                "Undeploying web app from all nodes failed");

    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        if (!webAppUndeployed) {
            // The worker profile from automation xml is used since the virtual host applicable
            // to the deployed web application is appserver.carbon-test.org
            // Therefore this domain should be passed with the webapp name to undeploy the webapp from manager node.
            // Note that the webAppAdminClient used here is initialized by the managerSessionCookieLB mgr session
            webAppAdminClient.deleteWebAppFile(webAppFileName, lbNode.getDefaultInstance().getHosts().get("worker"));
        }
    }

}
