/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.appserver.integration.tests.webapp.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.extensions.carbonserver.MultipleServersManager;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class will test the issue of application name not being set in the carbon context,
 * if an initial context request comes from a webapp after the webapp is unloaded.
 */

public class CARBON15199CarbonContextAppNameNullTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON15199CarbonContextAppNameNullTest.class);

    private static final String WEB_APP_FILE_NAME = "AppNameReceiverRestExample-1.0.0.war";
    private static final String WEB_APP_NAME = "AppNameReceiverRestExample-1.0.0";
    private static final String GHOST_INFO_WEB_APP_FILE_NAME = "lazy-loading-info.war";
    private static final String GHOST_INFO_WEB_APP_NAME = "lazy-loading-info";
    private static final long IDLE_TIME = 120000;
    private static final long MAX_IDLE_TIME = 60000;
    private static final String PRODUCT_GROUP_NAME = "AS";
    private static final String INSTANCE_NAME = "appServerDummy";
    private static final String SUPER_TENANT_DOMAIN_KEY = "superTenant";
    private static final String USER_KEY = "admin";
    private static final String TENANT_DOMAIN_KEY = "wso2.com";
    private static final String TENANT_USER_KEY = "user1";

    public HashMap<String, String> startupParameterMap = new HashMap<String, String>();
    public MultipleServersManager manager = new MultipleServersManager();
    private WebAppAdminClient superTenantWebAppAdminClient;
    private WebAppAdminClient tenantWebAppAdminClient;
    private String superTenantSession;
    private String superTenantServerBackEndUrl;
    private String superTenantServerWebAppUrl;
    private String tenantSession;
    private String tenantServerBackEndUrl;
    private String tenantServerWebAppUrl;

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        startupParameterMap.put("-DportOffset", "1");
        startupParameterMap.put("-Dwebapp.idle.time", "1");
        startupParameterMap.put("-Dtenant.idle.time", "2");

        // There is a port setting issue in automation framework which it doesn't
        // correctly set the port offset, as a workaround need to create with a dummy context.
        AutomationContext newServerContext = new AutomationContext(PRODUCT_GROUP_NAME, INSTANCE_NAME,
                SUPER_TENANT_DOMAIN_KEY, USER_KEY);
        CarbonTestServerManager server = new CarbonTestServerManager(newServerContext, System.getProperty("carbon.zip"),
                startupParameterMap);

        // Starting the server, because need to get the new server's CARBON_HOME
        manager.startServers(server);

        // This context will be used when restarting the server with new configuration
        AutomationContext context = new AutomationContext(PRODUCT_GROUP_NAME, "appServerInstance0002",
                SUPER_TENANT_DOMAIN_KEY, USER_KEY);
        // This Context will be used when doing tenant related activities.
        AutomationContext tenantContext = new AutomationContext(PRODUCT_GROUP_NAME, "appServerInstance0002",
                TENANT_DOMAIN_KEY, TENANT_USER_KEY);

        ServerConfigurationManager serverManager = new ServerConfigurationManager(context);

        String ghostCarbonConfig = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "ghostconfig" + File.separator + "carbon.xml";
        File sourceFile = new File(ghostCarbonConfig);
        File targetFile = new File(
                System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator
                        + "conf" + File.separator + "carbon.xml");

        // backup the current carbon.xml configuration and applying the changes
        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        log.info("carbon.xml replaced with :" + sourceFile);

        serverManager.restartGracefully();
        log.info("Server Restarted after applying ghost configuration to carbon.xml");

        TenantManagementServiceClient tenantManagementServiceClient = new TenantManagementServiceClient(
                context.getContextUrls().getBackEndUrl(), (new LoginLogoutClient(context)).login());
        tenantManagementServiceClient.addTenant("wso2.com", "testuser11", "testuser11", "Demo");

        superTenantServerBackEndUrl = context.getContextUrls().getBackEndUrl();
        superTenantServerWebAppUrl = context.getContextUrls().getWebAppURL();
        superTenantSession = new LoginLogoutClient(context).login();

        tenantServerBackEndUrl = tenantContext.getContextUrls().getBackEndUrl();
        tenantServerWebAppUrl = tenantContext.getContextUrls().getWebAppURL();
        tenantSession = new LoginLogoutClient(tenantContext).login();

        superTenantWebAppAdminClient = new WebAppAdminClient(superTenantServerBackEndUrl, superTenantSession);
        tenantWebAppAdminClient = new WebAppAdminClient(tenantServerBackEndUrl, tenantSession);
    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {

        String applicationNameReceiverWebAppUrl = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + WEB_APP_FILE_NAME;
        superTenantWebAppAdminClient.warFileUplaoder(applicationNameReceiverWebAppUrl);
        assertTrue(WebAppDeploymentUtil
                        .isWebApplicationDeployed(superTenantServerBackEndUrl, superTenantSession, WEB_APP_NAME),
                "Web Application Deployment failed");

        tenantWebAppAdminClient.warFileUplaoder(applicationNameReceiverWebAppUrl);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(tenantServerBackEndUrl, tenantSession, WEB_APP_NAME),
                "Web Application Deployment failed");

        // Uploading the ghost status receiver web app, this web will give the status of the web app
        superTenantWebAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator
                + GHOST_INFO_WEB_APP_FILE_NAME);
        assertTrue(WebAppDeploymentUtil
                .isWebApplicationDeployed(superTenantServerBackEndUrl, superTenantSession, GHOST_INFO_WEB_APP_NAME),
                "Web Application Deployment failed");

    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @Test(groups = "wso2.as", description = "Invoke web application",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testApplicationNameInResponse() throws Exception {
        checkWebAppAutoUnloadingToGhostState();
        // Testing the application name when web app is unloaded.
        String webAppURLLocal = superTenantServerWebAppUrl + "/" + WEB_APP_NAME + "/app/name";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getData(), WEB_APP_NAME, "Application name not set in the first request");

        // Testing the application name when tenant is unloaded.
        String tenantWebAppURLLocal = tenantServerWebAppUrl + "/webapps/" + WEB_APP_NAME + "/app/name";
        HttpResponse tenantResponse = HttpRequestUtil.sendGetRequest(tenantWebAppURLLocal, null);
        assertEquals(tenantResponse.getData(), WEB_APP_NAME, "Application name not set in the first request");
    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @Test(groups = "wso2.as", description = "UnDeploying web application",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testDeleteWebApplication() throws Exception {
        superTenantWebAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME, "localhost");
        superTenantWebAppAdminClient.deleteWebAppFile(GHOST_INFO_WEB_APP_FILE_NAME, "localhost");
        tenantWebAppAdminClient.deleteWebAppFile(GHOST_INFO_WEB_APP_FILE_NAME, "localhost");
    }

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        manager.stopAllServers();
    }

    /**
     * Check the web app is whether unloaded or not by sleeping web app idle time and some additional time,
     * because actual time taken to unloaded will vary
     *
     */
    private void checkWebAppAutoUnloadingToGhostState() throws Exception {
        boolean isWebAppInGhostState = false;
        boolean isTenantLoaded;
        log.info("Sleeping  for " + IDLE_TIME + " milliseconds to unload both tenant and the web app.");
        try {
            Thread.sleep(IDLE_TIME);
        } catch (InterruptedException interruptedException) {
            String customErrorMessage = "InterruptedException occurs when sleeping for web app idle time";
            log.warn(customErrorMessage);
        }

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_IDLE_TIME) {
            // Since tenant idle time is greater than web app idle time after receiving the web app status,
            // its no need to get the ghost status of web app again and again
            if (!isWebAppInGhostState) {
                // get the web app ghost status
                isWebAppInGhostState = getWebAppStatus("carbon.super");
            }
            isTenantLoaded = getTenantStatus(TENANT_DOMAIN_KEY);
            // if web app is in ghost status and tenant is unloaded exit the loop or else sleep and continue the loop.
            if (isWebAppInGhostState && !isTenantLoaded) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    String customErrorMessage = "InterruptedException occurs when sleeping 1000 milliseconds "
                            + "and while waiting for Web-app to auto unload";
                    log.warn(customErrorMessage, interruptedException);
                }
            }

        }
    }

    /**
     * This method will do a REST GET call to the lazy-loading-info service status information
     * to get the whether web app is in ghost mode or not
     *
     * @return boolean which indicates whether the web application is in ghost mode or not
     * @throws Exception
     */
    private boolean getWebAppStatus(String tenantDomain) throws Exception {

        String requestUrl =
                superTenantServerWebAppUrl + "/" + GHOST_INFO_WEB_APP_NAME + "/webapp-status/" + tenantDomain + "/"
                        + WEB_APP_NAME;
        boolean isWebAppGhost = false;
        try {
            HttpResponse response = HttpURLConnectionClient.sendGetRequest(requestUrl, null);
            JSONObject webAppStatusJSON = new JSONObject(response.getData()).getJSONObject("WebAppStatus");
            isWebAppGhost = webAppStatusJSON.getBoolean("webAppGhost");
        } catch (IOException ioException) {
            String customErrorMessage = "IOException when sending the Get request to:" + requestUrl;
            log.error(customErrorMessage, ioException);
            throw new Exception(customErrorMessage, ioException);
        } catch (JSONException jsonException) {
            String customErrorMessage =
                    "JSONException when retrieving the values from json object WebAppStatus" + requestUrl;
            log.error(customErrorMessage, jsonException);
            throw new Exception(customErrorMessage, jsonException);
        }
        return isWebAppGhost;
    }

    /**
     * This method will do a REST GET call to the lazy-loading-info service status information
     * to get the tenant status
     *
     * @return boolean which indicates whether the tenant is loaded or not
     * @throws Exception
     */
    private boolean getTenantStatus(String tenantDomain) throws Exception {

        String requestUrl =
                superTenantServerWebAppUrl + "/" + GHOST_INFO_WEB_APP_NAME + "/tenant-status/" + tenantDomain;
        boolean isTenantLoaded = true;
        try {
            HttpResponse response = HttpURLConnectionClient.sendGetRequest(requestUrl, null);
            JSONObject tenantStatusJSON = new JSONObject(response.getData());
            isTenantLoaded = tenantStatusJSON.getJSONObject("TenantStatus").getBoolean("tenantContextLoaded");
        } catch (IOException ioException) {
            String customErrorMessage = "IOException when sending the Get request to:" + requestUrl;
            log.error(customErrorMessage, ioException);
            throw new Exception(customErrorMessage, ioException);
        } catch (JSONException jsonException) {
            String customErrorMessage =
                    "JSONException when retrieving the values from json object WebAppStatus" + requestUrl;
            log.error(customErrorMessage, jsonException);
            throw new Exception(customErrorMessage, jsonException);
        }
        return isTenantLoaded;
    }
}
