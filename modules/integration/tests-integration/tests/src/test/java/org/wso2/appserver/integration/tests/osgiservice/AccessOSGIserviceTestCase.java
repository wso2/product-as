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

package org.wso2.appserver.integration.tests.osgiservice;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

import static org.testng.Assert.assertTrue;

// This test will upload a OSGI bundle to dropins and add service name to carboncontext-osgi-services.properties file
// Then it will upload a webapp via tenant user and will access uploaded webapp
// This webapp will access above exposed OSGI service with the new method "CarbonContext.getOSGIService(clazz,props)"

public class AccessOSGIserviceTestCase extends ASIntegrationTest {
    private ServerConfigurationManager serverManager;
    private WebAppAdminClient webAppAdminClient;
    private final String webAppFileName = "org.wso2.carbon.tenant.config.test.war";
    private final String webAppName = "org.wso2.carbon.tenant.config.test";
    private AutomationContext tenantContext;
    private String tenantSession;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(asServer);
        //adding osgi bundle to dropins
        File tempjar = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
                "artifacts" + File.separator + "AS" + File.separator + "osgi" + File.separator +
                "org.wso2.carbon.client.configcontext.provider-1.0.0.jar");
        serverManager.copyToComponentDropins(tempjar);
        //adding osgi service name to carboncontext-osgi-services.properties file
        File propertiesFile = new File(TestConfigurationProvider.getResourceLocation() +
                File.separator + "artifacts" + File.separator + "AS" + File.separator + "properties"
                + File.separator + "carboncontext-osgi-services.properties");
        File targetFile = new File(System.getProperty(ServerConstants.CARBON_HOME) +
                File.separator + "repository" + File.separator + "conf" + File.separator + "etc" +
                File.separator + "carboncontext-osgi-services.properties");
        serverManager.applyConfigurationWithoutRestart(propertiesFile, targetFile, true);
        serverManager.restartGracefully();
        super.init();

        loginAsTenant();

        webAppAdminClient = new WebAppAdminClient(tenantContext.getContextUrls().getBackEndUrl(),
                tenantSession);
    }

    private void loginAsTenant() throws Exception {
        tenantContext = new AutomationContext("AS", TestUserMode.TENANT_USER);
        tenantSession = new LoginLogoutClient(tenantContext).login();
    }

    @AfterTest(alwaysRun = true)
    public void revertConfiguration() throws Exception {
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                tenantContext.getContextUrls().getBackEndUrl(), tenantSession, webAppName)
                , "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as", description = "Invoke web application",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApp() throws Exception {
        String webAppURLLocal = webAppURL + "/t/" + tenantContext.getContextTenant().getDomain() +
                "/webapps/org.wso2.carbon.tenant.config.test/tenant.do";
        // access web application org.wso2.carbon.tenant.config.test
        // will invoke CarbonContext.getThreadLocalCarbonContext().getOSGiService(
        // Axis2ClientConfigContextProvider.class);
        // if the osgi service is invoked successfully, it will return value "axis2/services" else will return null
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(webAppURLLocal, "", "text/html");
        assertTrue(response.getData().contains("axis2/services"), "Unexpected response: " + response.getData());
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
            dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                        tenantContext.getContextUrls().getBackEndUrl(), tenantSession, webAppName),
                "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + "/t/" + tenantContext.getContextTenant().getDomain() +
                "/webapps/org.wso2.carbon.tenant.config.test/";
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(webAppURLLocal, "", "text/html");
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_NOT_FOUND,
                "Response code mismatch. Client request " +
                        "got a response even after web app is undeployed");
    }
}

