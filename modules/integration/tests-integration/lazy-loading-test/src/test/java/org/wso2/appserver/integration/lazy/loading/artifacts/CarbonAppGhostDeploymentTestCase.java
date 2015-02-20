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

package org.wso2.appserver.integration.lazy.loading.artifacts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.lazy.loading.GhostDeploymentBaseTest;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;

import javax.activation.DataHandler;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test carbon application deployment. For this test two tenants will be used and
 * in each tenant two  Carbon applications will be deployed.
 */
public class CarbonAppGhostDeploymentTestCase extends GhostDeploymentBaseTest {

    private static final Log log = LogFactory.getLog(CarbonAppGhostDeploymentTestCase.class);
    URL carbonApp1FileURL;
    DataHandler carbonApp1URLDataHandler;
    final static String CARBON_APP_NAME1 = "WarCApp_1.0.0";
    final static String CARBON_APP_FILE1 = "WarCApp_1.0.0.car";
    final static String CARBON_APP1_WEB_APP_NAME = "appServer-valid-deploymant-1.0.0";
    final static String CARBON_APP1_WEB_APP_FILE = "appServer-valid-deploymant-1.0.0.war";
    URL carbonApp2FileURL;
    DataHandler carbonApp2URLDataHandler;
    final static String CARBON_APP_NAME2 = "webappunpackCar_1.0.0";
    final static String CARBON_APP_FILE2 = "unpackwebappCar_1.0.0.car";
    final static String CARBON_APP2_WEB_APP_NAME = "myWebapp-1.0.0";
    final static String CARBON_APP2_WEB_APP_FILE = "myWebapp-1.0.0.war";
    private static final String WEB_APP1_TENANT1_RESPONSE = "<status>success</status>";


    String tenant1WebApp1URL;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        tenant1WebApp1URL = webAppURL + "/t/" + TENANT_DOMAIN_1 + "/webapps/" + CARBON_APP1_WEB_APP_NAME + "/";
        carbonApp1FileURL = new URL("file://" + ARTIFACTS_LOCATION + CARBON_APP_FILE1);
        carbonApp1URLDataHandler = new DataHandler(carbonApp1FileURL);
        carbonApp2FileURL = new URL("file://" + ARTIFACTS_LOCATION + CARBON_APP_FILE2);
        carbonApp2URLDataHandler = new DataHandler(carbonApp2FileURL);
    }

    @Test(groups = "wso2.as.lazy.loading", description = "Upload car file and verify in ghost deployment enable " +
            "environment. After the the deployment all the web applications of  the carbon application should be " +
            "deployed correctly and  they should be loaded fully(Not in ghost form) ", alwaysRun = true)
    public void carApplicationUploadGhostDeployment() throws Exception {
        log.info("Carbon application deployment start");
        CarbonAppUploaderClient carbonAppClient;

        loginAsTenantAdmin(TENANT_DOMAIN_1_kEY);

        carbonAppClient = new CarbonAppUploaderClient(backendURL, sessionCookie);

        carbonAppClient.uploadCarbonAppArtifact(CARBON_APP_FILE1, carbonApp1URLDataHandler);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, CARBON_APP1_WEB_APP_NAME),
                "Web Application deployment failed: " + CARBON_APP1_WEB_APP_NAME + "on " + TENANT_DOMAIN_1);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, CARBON_APP1_WEB_APP_FILE), true,
                "Web app " + CARBON_APP1_WEB_APP_FILE + "is  not loaded after deployment:"
                        + TENANT_DOMAIN_1);
        assertTrue(isCarbonAppListed(CARBON_APP_NAME1), "Carbon Application is not listed :" + CARBON_APP_NAME1);

        carbonAppClient.uploadCarbonAppArtifact(CARBON_APP_FILE2, carbonApp2URLDataHandler);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, CARBON_APP2_WEB_APP_NAME),
                "Web Application deployment failed: " + CARBON_APP2_WEB_APP_NAME + "on " + TENANT_DOMAIN_1);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, CARBON_APP2_WEB_APP_FILE), true,
                "Web app " + CARBON_APP2_WEB_APP_FILE + "is  not loaded after deployment:" + TENANT_DOMAIN_1);
        assertTrue(isCarbonAppListed(CARBON_APP_NAME2), "Carbon Application is not listed :" + CARBON_APP_NAME2);

        loginAsTenantAdmin(TENANT_DOMAIN_2_KEY);

        carbonAppClient = new CarbonAppUploaderClient(backendURL, sessionCookie);

        carbonAppClient.uploadCarbonAppArtifact(CARBON_APP_FILE1, carbonApp1URLDataHandler);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, CARBON_APP1_WEB_APP_NAME),
                "Web Application deployment failed: " + CARBON_APP1_WEB_APP_NAME + "on " + TENANT_DOMAIN_2);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_2, CARBON_APP1_WEB_APP_FILE), true,
                "Web app " + CARBON_APP1_WEB_APP_FILE + "is  not loaded after deployment:"
                        + TENANT_DOMAIN_2);
        assertTrue(isCarbonAppListed(CARBON_APP_NAME1), "Carbon Application is not listed :" + CARBON_APP_NAME1);

        carbonAppClient.uploadCarbonAppArtifact(CARBON_APP_FILE2, carbonApp2URLDataHandler);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, CARBON_APP2_WEB_APP_NAME),
                "Web Application deployment failed: " + CARBON_APP2_WEB_APP_NAME + "on " + TENANT_DOMAIN_2);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_2, CARBON_APP2_WEB_APP_FILE), true,
                "Web app " + CARBON_APP2_WEB_APP_FILE + "is  not loaded after deployment:" + TENANT_DOMAIN_2);
        assertTrue(isCarbonAppListed(CARBON_APP_NAME2), "Carbon Application is not listed :" + CARBON_APP_NAME2);
        log.info("Carbon application deployment end");
    }

    @Test(groups = "wso2.as.lazy.loading", description = "  Invoke web application that is deployed as Carbon " +
            "application in Ghost Deployment enable environment.First test will restart the server gracefully.After the " +
            "restart  all   tenant context not be loaded.Then,  it invokes the first web app on first tenant. After the" +
            " invoke, only that web app should loaded fully.", dependsOnMethods = "carApplicationUploadGhostDeployment")
    public void testInvokeWebAppInCarbonAppGhostDeployment() throws Exception {

        serverManager.restartGracefully();


        assertEquals(isTenantLoaded(TENANT_DOMAIN_1), false, " Tenant Name:" + TENANT_DOMAIN_1 + "loaded before access.");
        assertEquals(isTenantLoaded(TENANT_DOMAIN_2), false, " Tenant Name:" + TENANT_DOMAIN_2 + "loaded before access.");

        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_TENANT1_RESPONSE,
                "Web app invocation fail. web app URL:" + tenant1WebApp1URL);

        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, CARBON_APP1_WEB_APP_FILE), true,
                "Web-app is not loaded  after access. Tenant Name:" + TENANT_DOMAIN_1 + " Web_app Name: "
                        + CARBON_APP1_WEB_APP_FILE);
        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, CARBON_APP2_WEB_APP_FILE), false,
                "Web-app loaded before access and after access other web app in same Tenant. Tenant Name:"
                        + TENANT_DOMAIN_1 + " Web_app Name: " + CARBON_APP2_WEB_APP_FILE);

        assertEquals(isTenantLoaded(TENANT_DOMAIN_2), false, " Tenant Name:" + TENANT_DOMAIN_2 + "loaded before access.");
    }


    @Test(groups = "wso2.as.lazy.loading", description = "Test web application that is deployed as Carbon " +
            "application, auto unload  and reload in Ghost format. After access web app, it should be in fully load " +
            "form  but after configured web app idle time pass it should get auto unload ne reload in Ghost form.",
            dependsOnMethods = "testInvokeWebAppInCarbonAppGhostDeployment")
    public void testWebAppInCarbonAppAutoUnLoadAndReloadInGhostForm() throws Exception {
        serverManager.restartGracefully();

        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_TENANT1_RESPONSE, "Web app invocation fail");

        assertEquals(isWebAppLoaded(TENANT_DOMAIN_1, CARBON_APP1_WEB_APP_FILE), true,
                "Web-app is not loaded  after access. Tenant Name:" + TENANT_DOMAIN_1 + " Web_app Name: "
                        + CARBON_APP1_WEB_APP_FILE);


        assertEquals(checkWebAppAutoUnloadingToGhostState(TENANT_DOMAIN_1, CARBON_APP1_WEB_APP_FILE), true,
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass. Tenant Name:"
                        + TENANT_DOMAIN_1 + " Web_app Name: " + CARBON_APP1_WEB_APP_FILE);

    }


    @Test(groups = "wso2.as.lazy.loading", description = "Test Unload of tenant configuration context  after tenant "
            + "idle time pass without any action with that tenant",
            dependsOnMethods = "testWebAppInCarbonAppAutoUnLoadAndReloadInGhostForm")
    public void testTenantUnloadInIdleTimeAfterWebAPPInCarbonAppUsage() throws Exception {
        serverManager.restartGracefully();

        assertEquals(isTenantLoaded(TENANT_DOMAIN_1), false,
                "Tenant context is  loaded before access. Tenant name: " + TENANT_DOMAIN_1);
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_TENANT1_RESPONSE, "Web app invocation fail");
        assertEquals(isTenantLoaded(TENANT_DOMAIN_1), true,
                "Tenant context is  not loaded after access. Tenant name: " + TENANT_DOMAIN_1);

        assertEquals(checkTenantAutoUnloading(TENANT_DOMAIN_1), true,
                "Tenant context is  not unloaded after idle time. Tenant name: " + TENANT_DOMAIN_1);

    }


    @AfterClass(alwaysRun = true)
    public void cleanCarbonApplications() throws Exception {
        ApplicationAdminClient appAdminClient;
        loginAsTenantAdmin(TENANT_DOMAIN_1_kEY);
        appAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
        appAdminClient.deleteApplication(CARBON_APP_NAME1);
        log.info("Carbon application deleted : " + CARBON_APP_NAME1 + "on " + TENANT_DOMAIN_1);
        appAdminClient.deleteApplication(CARBON_APP_NAME2);
        log.info("Carbon application deleted : " + CARBON_APP_NAME2 + "on " + TENANT_DOMAIN_1);

        loginAsTenantAdmin(TENANT_DOMAIN_2_KEY);
        appAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
        appAdminClient.deleteApplication(CARBON_APP_NAME1);
        log.info("Carbon application deleted : " + CARBON_APP_NAME1 + "on " + TENANT_DOMAIN_2);
        appAdminClient.deleteApplication(CARBON_APP_NAME2);
        log.info("Carbon application deleted : " + CARBON_APP_NAME2 + "on " + TENANT_DOMAIN_2);
    }

}
