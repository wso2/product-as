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

package org.wso2.appserver.integration.tests.spring;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.utils.ServerConstants;
import java.io.File;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class Spring3NegativeTestCase extends ASIntegrationTest {

    private TestUserMode userMode;
    private WebAppAdminClient webAppAdminClient;

    @Factory(dataProvider = "userModeProvider")
    public Spring3NegativeTestCase(TestUserMode userMode) {
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
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.as", description = "Upload a file that has some other extension (not .war)")
    public void testUploadNotWarFile() throws Exception {
        String webAppDeploymentDir;
        //ToDo : Need to support for Tenant also
        if (userMode.equals(TestUserMode.SUPER_TENANT_ADMIN)) {

            webAppDeploymentDir =
                    System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                    "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;

            String webAppName = "spring3-restful-simple-service";
            File sourceFile = new File(
                    ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                    "spring3" + File.separator + webAppName + ".war");
            File modifiedExtensionFile = new File(
                    ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                    "spring3" + File.separator + "tmp" + File.separator + webAppName + ".tar");
            FileManipulator.copyFile(sourceFile, modifiedExtensionFile);

            webAppAdminClient.uploadWarFile(modifiedExtensionFile.getAbsolutePath());
            assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName));
            FileManipulator.deleteDir(webAppDeploymentDir + File.separator + webAppName + ".tar");
        }
    }

    @Test(groups = "wso2.as", description = "Upload a faulty webapp")
    public void testUploadFaultyWebapp() throws Exception {
        String webAppName = "spring3-restful-faulty-service";
        String webappFilePath =
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + "spring3" +
                File.separator + webAppName + ".war";
        webAppAdminClient.uploadWarFile(webappFilePath);
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName));
        webAppAdminClient.deleteFaultyWebAppFile(webAppName + ".war", asServer.getInstance().getHosts().get("default"));
    }

    @Test(groups = "wso2.as", description = "Upload faulty webapp and then upload webapp with the same name which has no issues")
    public void testUploadFaultyWebappFollowedByFixedWebApp() throws Exception {
        String faultyWebAppName = "spring3-restful-faulty-service";
        String webAppName = "spring3-restful-simple-service";
        String resourcePath = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + "spring3";
        String tmpResourcePath =
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + "spring3" +
                File.separator + "tmp";
        File tmpFaultyWebApp = new File(tmpResourcePath + File.separator + webAppName + ".war");
        File faultyWebApp = new File(resourcePath + File.separator + faultyWebAppName + ".war");
        File simpleWebApp = new File(resourcePath + File.separator + webAppName + ".war");
        FileManipulator.copyFile(faultyWebApp, tmpFaultyWebApp);

        webAppAdminClient.uploadWarFile(tmpFaultyWebApp.getAbsolutePath());
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName));

        webAppAdminClient.uploadWarFile(simpleWebApp.getAbsolutePath());
        try {
            Thread.sleep(90000); //Todo: WSAS-1991 : Unpacked directory doesn't get re-created
        } catch (InterruptedException ignored) {
        }
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName));

        webAppAdminClient.deleteWebAppFile(webAppName + ".war", asServer.getInstance().getHosts().get("default"));
    }
}
