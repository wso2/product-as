/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appserver.integration.tests.webapp.virtualhost;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;
import org.testng.annotations.Factory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertTrue;

/*
 *  This class will test uploading of webapps into Virtual Host where the appbase path contains some repeated folder
 *  name. Ex: /home/wso2/wso2as/repository/deployment/server/wso2
 */
public class WSAS2058AppbasePathRepeatedVirtualHostTestCase extends ASIntegrationTest {

    private static final String HELLOWORLD_WEBAPP_NAME = "HelloWorldWebapp";
    private static final String HELLOWWORLD_WEBAPP_PATH =
            Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "war",
                      HELLOWORLD_WEBAPP_NAME + ".war").toString();
    private static final String VHOST = "www.virtualhost.com";
    private static int isRestarted = 0;
    private static String virtualHostAppBase;
    private TestUserMode userMode;
    private ServerConfigurationManager serverConfigurationManager;
    private WebAppAdminClient webAppAdminClient;

    @Factory(dataProvider = "userModeDataProvider")
    public WSAS2058AppbasePathRepeatedVirtualHostTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    protected static TestUserMode[][] userModeDataProvider() {
        return new TestUserMode[][] {
                { TestUserMode.SUPER_TENANT_ADMIN },
                { TestUserMode.TENANT_ADMIN }
        };
    }

    @BeforeClass(alwaysRun = true, enabled = false)
    public void setEnvironment() throws Exception {
        super.init(userMode);
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN));
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);

        //Restart the Server only once
        if (isRestarted == 0) {
            File sourceFile =
                    Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "tomcat",
                              "catalina-server-repeated-appbase-name.xml").toFile();

            Path targetFilePath =
                    Paths.get(FrameworkPathUtil.getCarbonServerConfLocation(), "tomcat", "catalina-server.xml");
            serverConfigurationManager.applyConfigurationWithoutRestart(sourceFile, targetFilePath.toFile(), true);

            //Get first path and replace it in the server without restarting
            virtualHostAppBase = Paths.get(FrameworkPathUtil.getCarbonHome()).getParent().getFileName().toString();
            Charset charset = StandardCharsets.UTF_8;
            String content = new String(Files.readAllBytes(targetFilePath), charset);
            content = content.replaceAll("virtual_appbase_name", virtualHostAppBase);
            Files.write(targetFilePath, content.getBytes(charset));

            serverConfigurationManager.restartGracefully();
            sessionCookie = loginLogoutClient.login();
        }

        ++isRestarted;
    }

    @AfterClass(alwaysRun = true, enabled = false)
    public void restoreConfig() throws Exception {
        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient
                .deleteWebAppFile(HELLOWORLD_WEBAPP_NAME + ".war", asServer.getInstance().getHosts().get("default"));
        webAppAdminClient.deleteWebAppFile(HELLOWORLD_WEBAPP_NAME + ".war", VHOST);

        //Revert and restart only once
        --isRestarted;
        if (isRestarted == 0) {
            //Restore to original settings
            serverConfigurationManager.restoreToLastConfiguration();
        }

    }

    @Test(groups = "wso2.as", description = "Upload Webapp to default host", enabled = false)
    public void testUploadWebappDefaultHost() throws Exception {
        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(HELLOWWORLD_WEBAPP_PATH);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME),
                   "Webapp deployment failed");
        GetMethod getRequest = WebAppDeploymentUtil.invokeWebAppWithVirtualHost(webAppURL, HELLOWORLD_WEBAPP_NAME,
                                                                                null);
        int statusCode = getRequest.getStatusCode();
        assertTrue(statusCode == HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
    }

    @Test(groups = "wso2.as", description = "Upload Webapp to virtual host",
            dependsOnMethods = "testUploadWebappDefaultHost", enabled = false)
    public void testUploadWebappVirtualHost() throws Exception {
        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(HELLOWWORLD_WEBAPP_PATH, VHOST);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME,
                                                                 VHOST), "Webapp deployment failed");

        GetMethod getRequest = null;
        if (TestUserMode.TENANT_ADMIN.equals(userMode)) {
            getRequest = WebAppDeploymentUtil.invokeWebAppWithVirtualHost(
                    asServer.getContextUrls().getWebAppURL() + "/" + virtualHostAppBase + "/", HELLOWORLD_WEBAPP_NAME,
                    VHOST);
        } else {
            getRequest = WebAppDeploymentUtil.invokeWebAppWithVirtualHost(webAppURL, HELLOWORLD_WEBAPP_NAME, VHOST);
        }
        int statusCode = getRequest.getStatusCode();
        assertTrue(statusCode == HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
    }
}
