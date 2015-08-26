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

package org.wso2.appserver.integration.tests.session.persistence;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.apache.commons.httpclient.Cookie;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.testng.Assert.assertTrue;

public class WSAS2060SessionPersistenceTestCase extends ASIntegrationTest {

    private TestUserMode userMode;
    private ServerConfigurationManager serverConfigurationManager;
    private WebAppAdminClient webAppAdminClient;
    private static final String HELLOWORLD_WEBAPP_NAME = "HelloWorldWebapp";
    private static int isRestarted = 0;
    private static String SERVER_URL;
    public static int isCarbonXMLApplied = 0;
    private int sessionCount = 0;
    private String endpoint;
    private HttpClient httpClient;
    private static String CONTEXT_XML_PATH =
            Paths.get(FrameworkPathUtil.getCarbonServerConfLocation(), "tomcat", "context.xml").toAbsolutePath()
                 .toString();
    private static String CARBON_XML_PATH =
            Paths.get(FrameworkPathUtil.getCarbonServerConfLocation(), "carbon.xml").toAbsolutePath().toString();

    @Factory(dataProvider = "userModeProvider")
    public WSAS2060SessionPersistenceTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        httpClient = new HttpClient();
        serverConfigurationManager =
                new ServerConfigurationManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN));


        //Restart the Server only once
        if (isRestarted == 0) {
            File resourceContextXML =
                    Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "tomcat", "session",
                              "context.xml").toFile();
            serverConfigurationManager
                    .applyConfiguration(resourceContextXML, Paths.get(CONTEXT_XML_PATH).toFile(), true, true);
            SERVER_URL = "http://" + asServer.getDefaultInstance().getHosts().get("default") + ":" +
                         asServer.getDefaultInstance().getPorts().get("http");

        }

        Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "tomcat", "session",
                  "context.xml").toAbsolutePath();
        ++isRestarted;
        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        String helloWorldWarPath = Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "war",
                                             HELLOWORLD_WEBAPP_NAME + ".war").toString();
        webAppAdminClient.uploadWarFile(helloWorldWarPath);
        WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME);
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        endpoint = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + HELLOWORLD_WEBAPP_NAME;
    }

    @AfterClass(alwaysRun = true)
    public void restoreServer() throws Exception {
        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        if (userMode.equals(TestUserMode.TENANT_USER)) {
            webAppAdminClient.deleteWebAppFile(HELLOWORLD_WEBAPP_NAME + ".war",
                    asServer.getInstance().getHosts().get("default"));
        }

        //Revert and restart only once
        --isRestarted;
        if (isRestarted == 0) {
            serverConfigurationManager.restoreToLastConfiguration();
        }
    }

    @Test(groups = "wso2.as", description = "Check if Session Webapp is available")
    public void testSessionWebappAvailable() throws Exception {
        GetMethod httpGet = null;
        GetMethod httpGetFabIcon = null;
        boolean isSecondSession = false;
        try {
            httpGet = new GetMethod(endpoint);
            int statusCode = httpClient.executeMethod(httpGet);
            ++sessionCount;
            assertTrue(HttpStatus.SC_OK == statusCode,
                       "Session example webapp is not available for Tenant: " + userMode);

            HttpClient httpClientFabIcon = new HttpClient();
            httpGetFabIcon = new GetMethod(SERVER_URL + "/" + "favicon.ico");
            statusCode = httpClientFabIcon.executeMethod(httpGetFabIcon);
            Cookie cookie = null;
            if (HttpStatus.SC_METHOD_NOT_ALLOWED == statusCode) {
                Cookie[] cookiesFabIcon = httpClientFabIcon.getState().getCookies();
                if (cookiesFabIcon.length > 0) {
                    cookie = cookiesFabIcon[0];
                    isSecondSession = true;
                }
            }

            if (!isSecondSession) {
                cookie = new Cookie();
                cookie.setDomain(asServer.getInstance().getHosts().get("default"));
                cookie.setPath("/");
                cookie.setName("JSESSIONID");
                cookie.setValue("C0FCA53EBF7F09D24E2B430847214934");
            }

            httpClient.getState().addCookie(cookie);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (httpGetFabIcon != null) {
                httpGetFabIcon.releaseConnection();
            }
        }
    }

    @Test(groups = "wso2.as", description = "Check Session Persistence when Ghost Deployment is disabled",
            dependsOnMethods = "testSessionWebappAvailable")
    public void testSessionPersistenceGhostDisabled() throws Exception {
        GetMethod httpGet = null;
        sessionCookie = loginLogoutClient.login();
        WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME);
        try {
            httpGet = new GetMethod(endpoint);
            int statusCode = httpClient.executeMethod(httpGet);
            ++sessionCount;
            assertTrue(HttpStatus.SC_OK == statusCode &&
                       httpGet.getResponseBodyAsString().contains("Hello " + sessionCount + "!"),
                       "Failed to add Session when Ghost Deployment is disabled for Tenant: " + userMode);
            serverConfigurationManager.restartGracefully();

            sessionCookie = loginLogoutClient.login();
            WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME);
            statusCode = httpClient.executeMethod(httpGet);
            ++sessionCount;
            assertTrue(HttpStatus.SC_OK == statusCode &&
                       httpGet.getResponseBodyAsString().contains("Hello " + sessionCount + "!"),
                       "Failed to add Session when Ghost Deployment is disabled for Tenant: " + userMode);
        } finally {
            if(httpGet != null) {
                httpGet.releaseConnection();
            }
        }
    }

    @Test(groups = "wso2.as", description = "Check Session Persistence when Ghost Deployment is enabled",
    dependsOnMethods = "testSessionPersistenceGhostDisabled")
    public void testSessionPersistenceGhostEnabled() throws  Exception {

        //Apply carbon.xml and restart the server once
        if (isCarbonXMLApplied == 0) {
            File ghostEnabled =
                    Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "tomcat", "session",
                              "ghostenabled.xml").toFile();
            serverConfigurationManager
                    .applyConfiguration(ghostEnabled, Paths.get(CARBON_XML_PATH).toFile(), true, true);
        }
        ++isCarbonXMLApplied;

        GetMethod httpGet = null;
        sessionCookie = loginLogoutClient.login();
        WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME);
        try {
            httpGet = new GetMethod(endpoint);
            int statusCode = httpClient.executeMethod(httpGet);
            ++sessionCount;
            assertTrue(HttpStatus.SC_OK == statusCode &&
                       httpGet.getResponseBodyAsString().contains("Hello " + sessionCount + "!"),
                       "Failed to add Session when Ghost Deployment is disabled for Tenant: " + userMode);
            serverConfigurationManager.restartGracefully();

            sessionCookie = loginLogoutClient.login();
            WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, HELLOWORLD_WEBAPP_NAME);
            statusCode = httpClient.executeMethod(httpGet);
            ++sessionCount;
            assertTrue(HttpStatus.SC_OK == statusCode &&
                       httpGet.getResponseBodyAsString().contains("Hello " + sessionCount + "!"),
                       "Failed to add Session when Ghost Deployment is disabled for Tenant: " + userMode);
        } finally {
            if(httpGet != null) {
                httpGet.releaseConnection();
            }
        }
    }
}
