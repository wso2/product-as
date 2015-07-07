/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.appserver.integration.tests.webapp.spring;

import org.h2.server.web.WebApp;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.*;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;
import java.io.File;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SpringNegativeTestCase extends ASIntegrationTest {

    private WebAppMode webAppMode;
    private WebAppAdminClient webAppAdminClient;
    private static final String SPRING3_SIMPLE_WEBAPP = "spring3-restful-simple-service";
    private static final String SPRING4_SIMPLE_WEBAPP = "spring4-restful-simple-service";
    private static final String SPRING3_FAULTY_WEBAPP = "spring3-restful-faulty-service";
    private static final String SPRING4_FAULTY_WEBAPP = "spring4-restful-faulty-service";

    @Factory(dataProvider = "webAppModeProvider")
    public SpringNegativeTestCase(WebAppMode webAppMode) {
        this.webAppMode = webAppMode;
    }

    @DataProvider
    private static WebAppMode[][] webAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] {new WebAppMode(SPRING3_SIMPLE_WEBAPP, TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode(SPRING3_SIMPLE_WEBAPP, TestUserMode.TENANT_USER)},
                new WebAppMode[] {new WebAppMode(SPRING4_SIMPLE_WEBAPP, TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode(SPRING4_SIMPLE_WEBAPP, TestUserMode.TENANT_USER)},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(webAppMode.getUserMode());
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.as", description = "Upload a file that has some other extension (not .war)")
    public void testUploadNotWarFile() throws Exception {
        String webAppDeploymentDir;
        //ToDo : Need to support for Tenant also
        if (webAppMode.getUserMode().equals(TestUserMode.SUPER_TENANT_ADMIN)) {

            webAppDeploymentDir =
                    System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                    "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;

            File sourceFile = new File(
                    ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                    webAppMode.getWebAppName() + ".war");
            File modifiedExtensionFile = new File(
                    ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                    "tmp" + File.separator + webAppMode.getWebAppName() + ".tar");
            FileManipulator.copyFile(sourceFile, modifiedExtensionFile);

            webAppAdminClient.uploadWarFile(modifiedExtensionFile.getAbsolutePath());
            assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()));
            FileManipulator.deleteDir(webAppDeploymentDir + File.separator + webAppMode.getWebAppName() + ".tar");
        }
    }

    @Test(groups = "wso2.as", description = "Upload a faulty webapp")
    public void testUploadFaultyWebapp() throws Exception {
        String faultyWebAppName = "";
        if (webAppMode.getWebAppName().equalsIgnoreCase(SPRING3_SIMPLE_WEBAPP)) {
            faultyWebAppName = SPRING3_FAULTY_WEBAPP;
        } else if (webAppMode.getWebAppName().equalsIgnoreCase(SPRING4_SIMPLE_WEBAPP)) {
            faultyWebAppName = SPRING4_FAULTY_WEBAPP;
        } else {
            assertTrue(false);
        }
        String webappFilePath =
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + faultyWebAppName + ".war";
        webAppAdminClient.uploadWarFile(webappFilePath);
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, faultyWebAppName));
        webAppAdminClient
                .deleteFaultyWebAppFile(faultyWebAppName + ".war", asServer.getInstance().getHosts().get("default"));
    }

    @Test(groups = "wso2.as", description = "Upload faulty webapp and then upload webapp with the same name which has no issues")
    public void testUploadFaultyWebappFollowedByFixedWebApp() throws Exception {
        String faultyWebAppName = "";
        if (webAppMode.getWebAppName().equalsIgnoreCase(SPRING3_SIMPLE_WEBAPP)) {
            faultyWebAppName = SPRING3_FAULTY_WEBAPP;
        } else if (webAppMode.getWebAppName().equalsIgnoreCase(SPRING4_SIMPLE_WEBAPP)) {
            faultyWebAppName = SPRING4_FAULTY_WEBAPP;
        } else {
            assertTrue(false);
        }
        String resourcePath = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" ;
        String tmpResourcePath =
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + "tmp";
        File tmpFaultyWebApp = new File(tmpResourcePath + File.separator + webAppMode.getWebAppName() + ".war");
        File faultyWebApp = new File(resourcePath + File.separator + faultyWebAppName + ".war");
        File simpleWebApp = new File(resourcePath + File.separator + webAppMode.getWebAppName() + ".war");
        FileManipulator.copyFile(faultyWebApp, tmpFaultyWebApp);

        webAppAdminClient.uploadWarFile(tmpFaultyWebApp.getAbsolutePath());
        WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName());
        webAppAdminClient.uploadWarFile(simpleWebApp.getAbsolutePath());
        WebAppDeploymentUtil.isFaultyWebApplicationUnDeployed(backendURL, sessionCookie, webAppMode.getWebAppName());
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()));

        webAppAdminClient.deleteWebAppFile(webAppMode.getWebAppName() + ".war", asServer.getInstance().getHosts().get("default"));
    }

    @Test(groups = "wso2.as", description = "Upload Webapp twice and check if its created duplicates")
    public void testUploadWebappTwiceforDuplicates() throws Exception {
        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + "/student/deployedtime";
        String webApp = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + webAppMode.getWebAppName() + ".war";
        webAppAdminClient.uploadWarFile(webApp);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()));
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        webAppAdminClient.uploadWarFile(webApp);
        assertTrue(WebAppDeploymentUtil.isWebAppRedeployed(webAppMode.getWebAppName(), response.getData(), endpoint),
                   "Web app redeployment failed: " + webAppMode.getWebAppName());
        List<String> webAppList = webAppAdminClient.getWebApplist(webAppMode.getWebAppName());
        assertTrue(webAppList.size() == 1, "Duplicate web apps have been created");
        webAppAdminClient.deleteWebAppFile(webAppMode.getWebAppName() + ".war",
                                           asServer.getInstance().getHosts().get("default"));
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()));
    }
}
