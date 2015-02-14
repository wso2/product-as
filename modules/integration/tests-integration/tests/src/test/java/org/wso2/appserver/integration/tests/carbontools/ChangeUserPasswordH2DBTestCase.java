/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appserver.integration.tests.carbontools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.CarbonCommandToolsUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.extensions.usermgt.UserPopulator;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test change H2DB user password using chpasswd.sh/chpasswd.bat
 */

public class ChangeUserPasswordH2DBTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ChangeUserPasswordH2DBTestCase.class);
    public static final String H2DB_DB_URL = "/repository/database/WSO2CARBON_DB";
    private TestServerManager testServerManager;
    private AuthenticatorClient authenticatorClient;
    private boolean scriptRunStatus;
    private AutomationContext context;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        context = new AutomationContext("AS", "appServerInstance0002", ContextXpathConstants.SUPER_TENANT,
                                        ContextXpathConstants.ADMIN);
        authenticatorClient = new AuthenticatorClient(context.getContextUrls().getBackEndUrl());
    }


    @Test(groups = "wso2.as", description = "H2DB Password changing script run test")
    public void testScriptRun() throws Exception {

        testServerManager = new TestServerManager(context, 1) {
            public void configureServer() throws Exception {
                testServerManager.startServer();
                UserPopulator userPopulator = new UserPopulator("AS", "appServerInstance0002");
                userPopulator.populateUsers();
                testServerManager.stopServer();
                String[] cmdArray;
                String commandDirectory;
                if (CarbonCommandToolsUtil.isCurrentOSWindows()) {
                    cmdArray = new String[]
                            {"cmd.exe", "/c", "chpasswd.bat", "--db-url", "jdbc:h2:" +
                             testServerManager.getCarbonHome() + H2DB_DB_URL,
                             "--db-driver", "org.h2.Driver", "--db-username", "wso2carbon", "--db-password",
                             "wso2carbon", "--username", "testu1", "--new-password", "testu123"};
                    commandDirectory = testServerManager.getCarbonHome() + File.separator + "bin";
                } else {
                    cmdArray = new String[]
                            {"sh", "chpasswd.sh", "--db-url", "jdbc:h2:" + testServerManager.getCarbonHome() +
                             H2DB_DB_URL, "--db-driver", "org.h2.Driver",
                             "--db-username", "wso2carbon", "--db-password", "wso2carbon", "--username",
                             "testu1", "--new-password", "testu123"};
                    commandDirectory = testServerManager.getCarbonHome() + "/bin";
                }

                scriptRunStatus =
                        CarbonCommandToolsUtil.isScriptRunSuccessfully(commandDirectory, cmdArray,
                                                                       "Password updated successfully");
                log.info("Script running status : " + scriptRunStatus);
                assertTrue(scriptRunStatus, "Script executed successfully");
            }
        };

        testServerManager.startServer();

    }

    @Test(groups = "wso2.as", description = "H2DB password change test", dependsOnMethods = {"testScriptRun"})
    public void testUserPasswordOnH2DBChanged() throws Exception {
        String loginStatusString = authenticatorClient.login
                ("testu1", "testu123", context.getInstance().getHosts().get("default"));
        assertTrue(loginStatusString.contains("JSESSIONID"), "Unsuccessful login");

    }

    @AfterClass(alwaysRun = true)
    public void cleanResources() throws Exception {
        testServerManager.stopServer();
    }


}

