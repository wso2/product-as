/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.tests.webapp.mgt;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tomcat unpacks the webapps of tenants to super tenant space
 *
 * https://wso2.org/jira/browse/WSAS-1702
 *
 */
public class WSAS1702WebAppUnpackCheckTestCase extends ASIntegrationTest {
    private final String webAppFileName = "WSAS1702WebApp.war";
    private final String webAppName = "WSAS1702WebApp";
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;
    private int tenantID;
    private String carbonHome;
    private TenantManagementServiceClient tenantManagementServiceClient;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void testWebApplicationDeploymentAndAccessibility() throws Exception {

        AutomationContext superTenantContext = new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN);
        String superAdminSession = new LoginLogoutClient(superTenantContext).login();
        tenantManagementServiceClient = new TenantManagementServiceClient(superTenantContext.getContextUrls().
                getBackEndUrl(), superAdminSession);

        carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "deploy webApp as tenant")
    public void testDeployWebAppAsTenant() throws Exception {
        super.init(TestUserMode.TENANT_USER);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
        String webAppURLLocal = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + webAppName + "/" + "Calendar.html";
        HttpResponse response1 = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertTrue(response1.getData().contains("<h1>GWT Calendar</h1>"), "Webapp invocation fail");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "check the unpack webapp in tenant space",
            dependsOnMethods = "testDeployWebAppAsTenant")
    public void testCheckTenantSpace() throws Exception {

        tenantID = 1;

        String pathToWebAppInTenant = carbonHome + File.separator + "repository" + File.separator +
                "tenants" + File.separator + tenantID + File.separator + "webapps" + File.separator;
        assertTrue(new File(pathToWebAppInTenant + webAppName).exists(),
                "unpacked webapp file not available in tenant space at " + pathToWebAppInTenant + webAppName);
        assertTrue(new File(pathToWebAppInTenant + webAppFileName).exists(),
                "webapp file not available in tenant space at " + pathToWebAppInTenant + webAppFileName);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "check the unpack webapp in super tenant space",
            dependsOnMethods = "testCheckTenantSpace")
    public void testCheckSuperTenantSpace() throws Exception {
        String pathToWebAppInSuperTenant = carbonHome + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;
        assertFalse(new File(pathToWebAppInSuperTenant + webAppName).exists(),
                "unpacked webapp file available in super tenant space at " + pathToWebAppInSuperTenant + webAppName);
        assertFalse(new File(pathToWebAppInSuperTenant + webAppFileName).exists(),
                "webapp file available in super tenant space at " + pathToWebAppInSuperTenant + webAppFileName);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName), "Web Application unDeployment failed");
        String webAppURLLocal = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + webAppName;
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getResponseCode(), 404, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed");
    }
}
