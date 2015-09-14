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

import java.io.File;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.carbonserver.MultipleServersManager;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.extensions.exceptions.AutomationExtensionException;
import org.wso2.carbon.integration.common.extensions.usermgt.UserPopulator;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class will test the issue of application name not being set in the carbon context,
 * if an initial context request comes from a webapp after the webapp is unloaded.
 */

public class CARBON15199CarbonContextAppNameNullTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON15199CarbonContextAppNameNullTest.class);

    private final String webAppFileName = "AppNameReceiverRestExample-1.0.0.war";
    private final String webAppName = "AppNameReceiverRestExample-1.0.0";
    private final String ghostInfoWebAppFileName = "lazy-loading-info.war";
    private final String ghostInfoWebAppName = "lazy-loading-info";
    private final long idleTime = 120000;
    private final long maxIdleTime = 60000;
    private final String productGroupName = "AS";
    private final String instanceName = "appServerDummy";
    private final String superTenantDomainKey = "superTenant";
    private final String userKey = "admin";
    private final String tenantDomainKey = "wso2.com";
    private final String tenantUserKey = "user1";

    private MultipleServersManager manager = new MultipleServersManager();
    private WebAppAdminClient superTenantWebAppAdminClient;
    private WebAppAdminClient tenantWebAppAdminClient;
    private TestServerManager testServerManager;
    private AutomationContext automationContext;

    private String superTenantSession;
    private String superTenantServerBackEndUrl;
    private String superTenantServerWebAppUrl;
    private String tenantSession;
    private String tenantServerBackEndUrl;
    private String tenantServerWebAppUrl;

    private int portOffset = 1;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true, enabled = true)
    public void init() throws Exception {
        super.init();
        AutomationContext autoCtx = new AutomationContext();

        testServerManager = new TestServerManager(autoCtx, portOffset) {
            public void configureServer() throws AutomationFrameworkException {
                try {
                    testServerManager.startServer();
                    UserPopulator userPopulator = new UserPopulator(ASIntegrationConstants.AS_PRODUCT_GROUP,
                            ASIntegrationConstants.AS_INSTANCE_0002);
                    userPopulator.populateUsers();
                    testServerManager.stopServer();
                    carbonHome = testServerManager.getCarbonHome();
                    //                    String commandDirectory = carbonHome + File.separator + "bin";
                    //                    String[] cmdArray;

                } catch (IOException | XPathExpressionException e) {
                    throw new AutomationFrameworkException("Error when starting the carbon server", e);
                } catch (AutomationExtensionException e) {
                    throw new AutomationFrameworkException("Error when populating users", e);
                }
            }
        };
        testServerManager.startServer();

        // This context will be used when restarting the server with new configuration
        AutomationContext context = new AutomationContext(productGroupName,
                ASIntegrationConstants.AS_INSTANCE_0002, superTenantDomainKey, userKey);
        // This Context will be used when doing tenant related activities.
        AutomationContext tenantContext = new AutomationContext(productGroupName,
                ASIntegrationConstants.AS_INSTANCE_0002, tenantDomainKey, tenantUserKey);

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

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Deploying web application", enabled = true)
    public void testWebApplicationDeployment() throws Exception {

        String applicationNameReceiverWebAppUrl = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + webAppFileName;
        superTenantWebAppAdminClient.uploadWarFile(applicationNameReceiverWebAppUrl);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(superTenantServerBackEndUrl, superTenantSession,
                webAppName), "Web Application Deployment failed");

        tenantWebAppAdminClient.uploadWarFile(applicationNameReceiverWebAppUrl);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(tenantServerBackEndUrl, tenantSession, webAppName),
                "Web Application Deployment failed");

        // Uploading the ghost status receiver web app, this web will give the status of the web app
        superTenantWebAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator
                + ghostInfoWebAppFileName);
        assertTrue(WebAppDeploymentUtil
                        .isWebApplicationDeployed(superTenantServerBackEndUrl, superTenantSession, ghostInfoWebAppName),
                "Web Application Deployment failed");

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Invoke web application", dependsOnMethods = "testWebApplicationDeployment",
            enabled = true)
    public void testApplicationNameInResponse() throws Exception {
        checkWebAppAutoUnloadingToGhostState();
        // Testing the application name when web app is unloaded.
        String webAppURLLocal = superTenantServerWebAppUrl + "/" + webAppName + "/app/name";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getData(), webAppName, "Application name not set in the first request");

        // Testing the application name when tenant is unloaded.
        String tenantWebAppURLLocal = tenantServerWebAppUrl + "/webapps/" + webAppName + "/app/name";
        HttpResponse tenantResponse = HttpRequestUtil.sendGetRequest(tenantWebAppURLLocal, null);
        assertEquals(tenantResponse.getData(), webAppName, "Application name not set in the first request");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "UnDeploying web application",
            dependsOnMethods = "testWebApplicationDeployment", enabled = true)
    public void testDeleteWebApplication() throws Exception {
        superTenantWebAppAdminClient.deleteWebAppFile(webAppFileName, "localhost");
        superTenantWebAppAdminClient.deleteWebAppFile(ghostInfoWebAppFileName, "localhost");
        tenantWebAppAdminClient.deleteWebAppFile(ghostInfoWebAppFileName, "localhost");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterClass(alwaysRun = true, enabled = true)
    public void clean() throws Exception {
        manager.stopAllServers();
    }

    /**
     * Check the web app is whether unloaded or not by sleeping web app idle time and some additional time,
     * because actual time taken to unloaded will vary
     */
    private void checkWebAppAutoUnloadingToGhostState() throws Exception {
        boolean isWebAppInGhostState = false;
        boolean isTenantLoaded;
        log.info("Sleeping  for " + idleTime + " milliseconds to unload both tenant and the web app.");
        try {
            Thread.sleep(idleTime);
        } catch (InterruptedException interruptedException) {
            String customErrorMessage = "InterruptedException occurs when sleeping for web app idle time";
            log.warn(customErrorMessage);
        }

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < maxIdleTime) {
            // Since tenant idle time is greater than web app idle time after receiving the web app status,
            // its no need to get the ghost status of web app again and again
            if (!isWebAppInGhostState) {
                // get the web app ghost status
                isWebAppInGhostState = getWebAppStatus("carbon.super");
            }
            isTenantLoaded = getTenantStatus(tenantDomainKey);
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
                superTenantServerWebAppUrl + "/" + ghostInfoWebAppName + "/webapp-status/" + tenantDomain + "/" +
                        webAppName;
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
                superTenantServerWebAppUrl + "/" + ghostInfoWebAppName + "/tenant-status/" + tenantDomain;
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