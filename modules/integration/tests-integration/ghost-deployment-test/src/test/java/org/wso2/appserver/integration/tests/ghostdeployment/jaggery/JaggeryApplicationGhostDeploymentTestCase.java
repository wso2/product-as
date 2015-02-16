/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.integration.tests.ghostdeployment.jaggery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.JaggeryApplicationUploaderClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.tests.ghostdeployment.GhostDeploymentBaseTest;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the ghost deployment of Jaggery application. For this test two tenants will be used and in each tenant two
 * Jaggery applications will be deployed.
 */
public class JaggeryApplicationGhostDeploymentTestCase extends GhostDeploymentBaseTest {

    private static final Log log = LogFactory.getLog(JaggeryApplicationGhostDeploymentTestCase.class);

    private static final String JAGGERY_APP_FILE_NAME1 = "hello.jag.zip";
    private static final String JAGGERY_APP_FILE_NAME2 = "bye.jag.zip";
    private static final String JAGGERY_APP_NAME1 = "hello";
    private static final String JAGGERY_APP_NAME2 = "bye";

    private static final String JAG_APP_FILE_PATH1 = ARTIFACTS_LOCATION + JAGGERY_APP_FILE_NAME1;
    private static final String JAG_APP_FILE_PATH2 = ARTIFACTS_LOCATION + JAGGERY_APP_FILE_NAME2;
    private static final String TENANT1_JAGG_APP1_RESPONSE = "Hello";
    private String tenant1JaggApp1Url;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        tenant1JaggApp1Url =
                webAppURL + "/t/" + TENANT_DOMAIN_1 + "/jaggeryapps/" + JAGGERY_APP_NAME1 + "/" + JAGGERY_APP_NAME1
                        + ".jag";

    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Deploying Jaggery application in Ghost Deployment enable" +
            "environment. Each Jaggery application should fully loaded" +
            "(non Ghost format) soon after the deployment") //TODO add alwas run true
    public void testJaggeryApplicationGhostDeployment()
            throws Exception {
        log.info("deployment of  Jaggery Application started");
        JaggeryApplicationUploaderClient jaggeryApplicationUploaderClient;

        //Tenant1
        loginAsTenantAdmin(TENANT_DOMAIN_1);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        jaggeryApplicationUploaderClient = new JaggeryApplicationUploaderClient(backendURL, sessionCookie);

        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME1, JAG_APP_FILE_PATH1);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME1),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME1 + " Tenant :"
                        + TENANT_DOMAIN_1);
        assertTrue(isWebAppLoaded(TENANT_DOMAIN_1, JAGGERY_APP_NAME1),
                "Jaggery application is  not filly loaded. It is in Ghost form. App name: " + JAGGERY_APP_NAME1
                        + " Tenant :" + TENANT_DOMAIN_1);

        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME2, JAG_APP_FILE_PATH2);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME2),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME2 + " Tenant :"
                        + TENANT_DOMAIN_1);
        assertTrue(isWebAppLoaded(TENANT_DOMAIN_1, JAGGERY_APP_NAME2),
                "Jaggery application is  not filly loaded. It is in Ghost form. App name: " + JAGGERY_APP_NAME2
                        + " Tenant :" + TENANT_DOMAIN_1);

        //Tenant2

        loginAsTenantAdmin(TENANT_DOMAIN_2);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        jaggeryApplicationUploaderClient = new JaggeryApplicationUploaderClient(backendURL, sessionCookie);

        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME1, JAG_APP_FILE_PATH1);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME1),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME1 + " Tenant :"
                        + TENANT_DOMAIN_2);
        assertTrue(isWebAppLoaded(TENANT_DOMAIN_2, JAGGERY_APP_NAME1),
                "Jaggery application is  not filly loaded. It is in Ghost form. App name: " + JAGGERY_APP_NAME1
                        + " Tenant :" + TENANT_DOMAIN_2);

        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME2, JAG_APP_FILE_PATH2);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME2),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME2 + " Tenant :"
                        + TENANT_DOMAIN_2);

    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Invoke Jaggery application in Ghost Deployment enable " +
            "environment. First test will restart the server gracefully. After the restart  all tenant context should" +
            " not be loaded. Then the it invokes the first Jaggery app on first tenant. After the invoke, only that " +
            "Jaggery app should loaded.", dependsOnMethods = "testJaggeryApplicationGhostDeployment")
    public void testInvokeJaggeryAppGhostDeployment()
            throws Exception {

        serverManager.restartGracefully();

        assertEquals(isTenantLoaded(TENANT_DOMAIN_1), false, " Tenant Name:" + TENANT_DOMAIN_1 + "loaded before access.");

        assertEquals(isTenantLoaded(TENANT_DOMAIN_2), false, " Tenant Name:" + TENANT_DOMAIN_2 + "loaded before access.");


        org.wso2.carbon.automation.test.utils.http.client.HttpResponse httpResponse = HttpURLConnectionClient
                .sendGetRequest(tenant1JaggApp1Url, null);

        assertEquals(httpResponse.getData(), TENANT1_JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);

        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, JAGGERY_APP_NAME1), true,
                "Jaggery-app is not loaded  after access. Tenant Name:" + TENANT_DOMAIN_1 + " Jaggery_app Name: "
                        + JAGGERY_APP_NAME1);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, JAGGERY_APP_NAME2), false,
                "Jaggery-app loaded before access and after access other web app in same Tenant. Tenant Name:"
                        + TENANT_DOMAIN_1 + " Jaggery_app Name: " + JAGGERY_APP_NAME2);

        assertEquals(isTenantLoaded(TENANT_DOMAIN_2), false, " Tenant Name:" + TENANT_DOMAIN_2 + "loaded before access.");

    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Test Jaggery application auto unload  and reload in Ghost" +
            " format. After access Jaggery app, it should be in fully load form  but after configured Jaggery app idle " +
            "time pass it should get auto unload ne reload in Ghost form.",
            dependsOnMethods = "testInvokeJaggeryAppGhostDeployment")
    public void testJaggeryAppAutoUnLoadAndReloadInGhostForm()
            throws Exception {
        serverManager.restartGracefully();

        org.wso2.carbon.automation.test.utils.http.client.HttpResponse httpResponse = HttpURLConnectionClient
                .sendGetRequest(tenant1JaggApp1Url, null);

        assertEquals(httpResponse.getData(), TENANT1_JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, JAGGERY_APP_NAME1), true,
                "Web-app is not loaded  after access. Tenant Name:" + TENANT_DOMAIN_1 + " Web_app Name: "
                        + JAGGERY_APP_NAME1);
        assertEquals(checkWebAppAutoUnloadingToGhostState(TENANT_DOMAIN_1, JAGGERY_APP_NAME1), true,
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass. Tenant Name:"
                        + TENANT_DOMAIN_1 + " Web_app Name: " + JAGGERY_APP_NAME1);

    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Test Unload of tenant configuration context  after tenant "
            + "idle time pass without any action with that tenant",
            dependsOnMethods = "testJaggeryAppAutoUnLoadAndReloadInGhostForm")
    public void testTenantUnloadInIdleTimeAfterJaggeryAPPUsage()
            throws Exception {
        serverManager.restartGracefully();

        assertEquals(isTenantLoaded(TENANT_DOMAIN_1), false,
                "Tenant context is  loaded before access. Tenant name: " + TENANT_DOMAIN_1);

        org.wso2.carbon.automation.test.utils.http.client.HttpResponse httpResponse = HttpURLConnectionClient
                .sendGetRequest(tenant1JaggApp1Url, null);

        assertEquals(httpResponse.getData(), TENANT1_JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);

        assertEquals(isTenantLoaded(TENANT_DOMAIN_1), true,
                "Tenant context is  not loaded after access. Tenant name: " + TENANT_DOMAIN_1);


        assertEquals(checkTenantAutoUnloading(TENANT_DOMAIN_1), true,
                "Tenant context is  not unloaded after idle time. Tenant name: " + TENANT_DOMAIN_1);

    }

    @AfterClass
    public void cleanJaggeryApplication() throws Exception {

        loginAsTenantAdmin(TENANT_DOMAIN_1);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME1, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + TENANT_DOMAIN_1);
        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME2, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed: Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + TENANT_DOMAIN_1);

        loginAsTenantAdmin(TENANT_DOMAIN_2);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME1, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + TENANT_DOMAIN_2);
        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME2, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed: Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + TENANT_DOMAIN_2);
    }

}
