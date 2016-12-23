/*
* Copyright 2016 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.appserver.integration.tests.javaee;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.utils.ServerConstants;

import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.ASIntegrationLoggingUtil;

import java.io.File;

import static org.testng.Assert.assertFalse;

public class WSAS2099DuplicateDeploymentIDTestCase extends ASIntegrationTest {

    private TestUserMode userMode;
    private ServerConfigurationManager serverManager;
    private WebAppAdminClient webAppAdminClient;

    private static final String WEBAPP_FILENAME = "javaee-examples2.war";

    @Factory(dataProvider = "userModeDataProvider")
    public WSAS2099DuplicateDeploymentIDTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeDataProvider() {
        return new TestUserMode[][] {{ TestUserMode.SUPER_TENANT_ADMIN }};
    }

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        if(userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            super.init(userMode);

            File sourceFile = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
                    File.separator + "system.properties");
            File targetFile = new File(
                    System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                            "conf" + File.separator + "tomee" + File.separator + "system.properties");
            serverManager = new ServerConfigurationManager(asServer);
            serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);

            webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
            String path = TestConfigurationProvider.getResourceLocation() +
                    "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + WEBAPP_FILENAME;
            webAppAdminClient.uploadWarFile(path);

            serverManager.restartForcefully();
            super.init(userMode);
        }
    }

    @Test(groups = "wso2.as", description = "Deploying duplicate web application")
    public void deployDuplicateWebappTest() throws Exception {

        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            String[] carbonLogs = ASIntegrationLoggingUtil.getLogsFromLogfile(
                    new File(System.getProperty("carbon.home") + File.separator + "repository" + File.separator + "logs" +
                            File.separator + "wso2carbon.log"));
            boolean isLogInLogsRecorded = false;

            for (String logEvent : carbonLogs) {
                if (logEvent.contains("Application cannot be deployed as it contains deployment-ids which are in use")) {
                    isLogInLogsRecorded = true;
                    break;
                }
            }
            assertFalse(isLogInLogsRecorded,
                    "Application cannot be deployed as it contains deployment-ids which are in use " + userMode);
        }
    }
}

