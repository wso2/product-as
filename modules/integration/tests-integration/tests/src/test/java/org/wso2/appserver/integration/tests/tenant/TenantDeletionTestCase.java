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

package org.wso2.appserver.integration.tests.tenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.carbonserver.MultipleServersManager;
import org.wso2.carbon.automation.test.utils.http.client.*;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * This class will test the deletion of tenants when deactivated and unloaded scenarios
 *
 */
public class TenantDeletionTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(TenantDeletionTestCase.class);

    private TenantManagementServiceClient tenantManagementServiceClient;
    private HashMap<String, String> startupParameterMap = new HashMap<String, String>();
    private MultipleServersManager manager = new MultipleServersManager();
    private WebAppAdminClient webAppAdminClient;
    private final String ghostInfoWebAppFileName = "lazy-loading-info.war";
    private final String ghostInfoWebAppName = "lazy-loading-info";
    private final long idleTime = 60000;
    private final long maxIdleTime = 60000;
    private String backEndNewserverUrl;
    private String newSessionCookie;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        startupParameterMap.put("-Dtenant.idle.time", "1");
        startupParameterMap.put("-DportOffset", "1");
        startupParameterMap.put("-DtenantDelete", "true");

        // This context will be used when restarting the server with new configuration
        AutomationContext context = new AutomationContext("AS", "appServerInstance0002", "superTenant", "admin");
        CarbonTestServerManager server = new CarbonTestServerManager(context, System.getProperty("carbon.zip"),
                startupParameterMap);
        // Starting the server, because need to get the new server's CARBON_HOME
        manager.startServers(server);

        backEndNewserverUrl = context.getContextUrls().getBackEndUrl();
        newSessionCookie = new LoginLogoutClient(context).login();

        tenantManagementServiceClient = new TenantManagementServiceClient(backEndNewserverUrl, newSessionCookie);
        webAppAdminClient = new WebAppAdminClient(backEndNewserverUrl, newSessionCookie);
        // Uploading the ghost status receiver web app, this web will give the status of the web app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator
                + ghostInfoWebAppFileName);
        assertTrue(WebAppDeploymentUtil
                .isWebApplicationDeployed(backEndNewserverUrl, newSessionCookie, ghostInfoWebAppName),
                "Web Application Deployment failed");
        this.createTenants();

    }

    @Test(groups = { "wso2.as" }, description = "Delete tenant")
    public void deleteTenant() throws Exception {
        tenantManagementServiceClient.deleteTenant("wso2.com");
        assertNull(tenantManagementServiceClient.getTenant("wso2.com"), "Tenant information not available");
    }

    @Test(groups = { "wso2.as" }, description = "Delete deactivated tenant")
    public void deleteDeactivatedTenant()
            throws Exception {
        tenantManagementServiceClient.deactivateTenant("abc.com");
        tenantManagementServiceClient.deleteTenant("abc.com");
        assertNull(tenantManagementServiceClient.getTenant("abc.com"), "Tenant information not available");
    }

    @Test(groups = { "wso2.as" }, description = "Delete unloaded tenant")
    public void deleteUnloadedTenant()
            throws Exception {
        checkTenantAutoUnloadedState("test.com");
        tenantManagementServiceClient.deleteTenant("test.com");
        assertNull(tenantManagementServiceClient.getTenant("test.com"), "Tenant information not available");
    }

    /**
     * Create the tenant which need to be check in the deletion
     *
     */
    private void createTenants() throws Exception {
        tenantManagementServiceClient.addTenant("wso2.com", "admin", "admin", "Demo");
        tenantManagementServiceClient.addTenant("abc.com", "admin", "admin", "Demo");
        tenantManagementServiceClient.addTenant("test.com", "admin", "admin", "Demo");
    }

    /**
     * Check the web app is whether unloaded or not by sleeping web app idle time and some additional time,
     * because actual time taken to unloaded will vary
     *
     */
    private void checkTenantAutoUnloadedState(String tenantDomainKey) throws Exception {
        boolean isTenantLoaded;
        log.info("Sleeping  for " + idleTime + " milliseconds to unload the tenant.");
        try {
            Thread.sleep(idleTime);
        } catch (InterruptedException interruptedException) {
            String customErrorMessage = "InterruptedException occurs when sleeping for tenant idle time";
            log.warn(customErrorMessage);
        }

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < maxIdleTime) {
            isTenantLoaded = getTenantStatus(tenantDomainKey);
            // if web app is in ghost status and tenant is unloaded exit the loop or else sleep and continue the loop.
            if (!isTenantLoaded) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    String customErrorMessage = "InterruptedException occurs when sleeping 1000 milliseconds "
                            + "and while waiting tenant unloading";
                    log.warn(customErrorMessage, interruptedException);
                }
            }

        }
    }

    /**
     * This method will do a REST GET call to the lazy-loading-info service status information
     * to get the tenant status
     *
     * @return boolean which indicates whether the tenant is loaded or not
     * @throws Exception
     */
    private boolean getTenantStatus(String tenantDomain) throws Exception {

        String requestUrl = backEndNewserverUrl + "/" + ghostInfoWebAppName + "/tenant-status/" + tenantDomain;
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
