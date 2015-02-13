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

package org.wso2.appserver.integration.tests.ghostdeployment.suppertenant;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.tests.ghostdeployment.GhostDeploymentBaseTest;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestSupperTenantGhostDeploymentTestCase extends GhostDeploymentBaseTest {

    private static final Log log = LogFactory.getLog(TestSupperTenantGhostDeploymentTestCase.class);

    private static final String WEB_APP_FILE_NAME1 = "appServer-valied-deploymant-1.0.0.war";
    private static final String WEB_APP_NAME1 = "appServer-valied-deploymant-1.0.0";
    private static final String WEB_APP_FILE_NAME2 = "helloworld.war";
    private static final String WEB_APP_NAME2 = "helloworld";
    private static final String WEB_APP1_LOCATION = ARTIFACTS_LOCATION + WEB_APP_FILE_NAME1;
    private static final String WEB_APP2_LOCATION = ARTIFACTS_LOCATION + WEB_APP_FILE_NAME2;
    private static final String WEB_APP1_RESPONSE = "<status>success</status>";
    private String webApp1URL;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webApp1URL = webAppURL + "/" + WEB_APP_NAME1 + "/";

    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Deploying web application in Ghost Deployment enable environment. "
            + "Each Web application should fully loaded (non Ghost format) soon after the deployment")
    public void testDeployWebApplicationGhostDeploymentOnSupperTenant() throws Exception {
        log.info("deployment of  web application started");

        loginAsTenantAdmin(SUPPER_TENANT_DOMAIN);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.warFileUplaoder(WEB_APP1_LOCATION);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application deployment failed: " + WEB_APP_NAME1 + "on " + TENANT_DOMAIN_1);
        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME1), true,
                "Web app " + WEB_APP_FILE_NAME1 + "is  not loaded after deployment in super tenant");

        webAppAdminClient.warFileUplaoder(WEB_APP2_LOCATION);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application deployment failed: " + WEB_APP_NAME2 + "on " + TENANT_DOMAIN_1);
        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME2), true,
                "Web app " + WEB_APP_FILE_NAME2 + "is  not loaded after deployment in super tenant");

        log.info("deployment of web application finished");

    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Invoke web application in Ghost Deployment enable environment.First test "
            + "will restart the server gracefully.After the restart  all web apps should be in ghost format.Then,  it "
            + "invokes the first web app on first tenant. After the invoke, only that web app should loaded fully and" +
            "all other web apps should be in Ghost format.", dependsOnMethods = "testDeployWebApplicationGhostDeploymentOnSupperTenant")
    public void testInvokeWebAppGhostDeploymentOnSupperTenant() throws Exception {

        serverManager.restartGracefully();


        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME1), false,
                "Web-app loaded before access in super tenant. Web_app Name: " + WEB_APP_FILE_NAME1);
        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME2), false,
                "Web-app loaded before access in super tenant.  Web_app Name: " + WEB_APP_FILE_NAME2);


        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(webApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail. web app URL:" + webApp1URL);

        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME1), true,
                "Web-app is not loaded  after access in super tenant. Web_app Name: " + WEB_APP_FILE_NAME1);
        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME2), false,
                "Web-app loaded before access and after access other web app in super tenant. Web_app Name: "
                        + WEB_APP_FILE_NAME2);


    }

    @Test(groups = "wso2.as.ghost.deployment", description = "Test web application auto unload  and reload in Ghost format. After access"
            + "web app, it should be in fully load form  but after configured web app idle time pass it should get auto"
            + "unload ne reload in Ghost form.", dependsOnMethods = "testInvokeWebAppGhostDeploymentOnSupperTenant")
    public void testWebAppAutoUnLoadAndReloadInGhostFormOnSupperTenant() throws Exception {
        serverManager.restartGracefully();

        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(webApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail");

        assertEquals(isSupperTenantWebAppLoaded(WEB_APP_FILE_NAME1), true,
                "Web-app is not loaded  after access in Super Tenant. Web_app Name: " + WEB_APP_FILE_NAME1);


        assertEquals(checkWebAppAutoUnloadingToGhostStateInSupperTenant(WEB_APP_FILE_NAME1), true,
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass in super tenant " +
                        "Web_app Name: " + WEB_APP_FILE_NAME1);

    }


    @AfterClass
    public void cleanWebApplications() throws Exception {

        loginAsTenantAdmin(SUPPER_TENANT_DOMAIN);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME1, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + WEB_APP_NAME1 + " on super tenant");
        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME2, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application un-deployment failed: Web app :" + WEB_APP_NAME2 + " on super tenant");


    }


}
