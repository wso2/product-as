/*
 *
 *  * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * WSO2 Inc. licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.wso2.appserver.integration.tests.carbontools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
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

/**
 * This class is to test change MySQL user password using chpasswd.sh/chpasswd.bat
 */
public class ChangeUserPasswordMySQLDBTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ChangeUserPasswordH2DBTestCase.class);
    public static String mySQL_DB_URL ;

    private TestServerManager testServerManager;
    private AutomationContext context;
    private AuthenticatorClient authenticatorClient;
    private boolean scriptRunStatus;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        context = new AutomationContext("AS", "appServerInstance0002", ContextXpathConstants.SUPER_TENANT,
                                        ContextXpathConstants.ADMIN);
        authenticatorClient = new AuthenticatorClient(context.getContextUrls().getBackEndUrl());
        authenticatorClient = new AuthenticatorClient(context.getContextUrls().getBackEndUrl());
        String mySQLPath  = "//databasePath/MySQL";
        AutomationContext context = new AutomationContext();
        mySQL_DB_URL = context.getConfigurationValue(mySQLPath);
    }

    // Enable it when testing with mysql
    @Test(groups = "wso2.as", description = "MySQL Password changing script run test",enabled = false)
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
                    cmdArray = new String[]{
                            "cmd.exe", "/c", "chpasswd.sh", "--db-url", mySQL_DB_URL,
                            "--db-driver", "com.mysql.jdbc.Driver", "--db-username", "root", "--db-password",
                            "root123", "--username", "testu1", "--new-password", "testu123"};
                    commandDirectory = testServerManager.getCarbonHome() + "/bin";
                } else {
                    cmdArray = new String[]{
                            "sh", "chpasswd.sh", "--db-url", mySQL_DB_URL,
                            "--db-driver", "com.mysql.jdbc.Driver", "--db-username", "root", "--db-password",
                            "root123", "--username", "testu1", "--new-password", "testu123"};
                    commandDirectory = testServerManager.getCarbonHome() + File.separator + "bin";
                }
                scriptRunStatus = CarbonCommandToolsUtil.isScriptRunSuccessfully(
                        commandDirectory, cmdArray, "Password updated successfully");
                log.info("Script running status : " + scriptRunStatus);
            }
        };

        testServerManager.startServer();
    }

    @Test(groups = "wso2.as", description = "MySQL password change test", dependsOnMethods = {"testScriptRun"},enabled = false)
    public void testUserPasswordChangeOnMySQLDB() throws Exception {
        String loginStatusString = authenticatorClient.login(
                "testu1", "testu123", context.getInstance().getHosts().get("default"));

        Assert.assertTrue(loginStatusString.contains("JSESSIONID"), "Unsuccessful login");
    }


    @AfterClass(alwaysRun = true)
    public void serverShutDown() throws Exception {
        testServerManager.stopServer();
    }


}
