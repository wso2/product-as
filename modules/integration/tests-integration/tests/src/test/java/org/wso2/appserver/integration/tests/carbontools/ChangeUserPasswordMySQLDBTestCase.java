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

package org.wso2.appserver.integration.tests.carbontools;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.CarbonCommandToolsUtil;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.extensions.servers.carbonserver.MultipleServersManager;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.extensions.usermgt.UserPopulator;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test change MySQL user password using chpasswd.sh/chpasswd.bat
 * All test methods in this class has disabled because need same features from unreleased automation
 * framework after the automation framework 4.3.2 released have to enable test methods
 * All the test cases in this class are disabled because need to test this with mysql
 */
public class ChangeUserPasswordMySQLDBTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ChangeUserPasswordMySQLDBTestCase.class);
    private boolean scriptRunStatus;
    private AutomationContext context;
    private int portOffset = 1;
    private HashMap<String, String> serverPropertyMap = new HashMap<String, String>();
    private MultipleServersManager manager = new MultipleServersManager();
    private String carbonHome = null;
    private static String MYSQL_DB_URL ;
    private AuthenticatorClient authenticatorClient;
    private static String PRODUCT_NAME = "AS";
    private static String INSTANCE = "appServerInstance0002";
    private char[] userNewPassword = {'t', 'e', 's', 't', 'u', '1', '2', '3'};

    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, AxisFault {
        context = new AutomationContext(PRODUCT_NAME, INSTANCE,
                                        ContextXpathConstants.SUPER_TENANT,
                                        ContextXpathConstants.SUPER_ADMIN);
        authenticatorClient = new AuthenticatorClient(context.getContextUrls().getBackEndUrl());
        MYSQL_DB_URL = context.getConfigurationValue(String.format(ASIntegrationConstants.DB_URL, "MySQL"));
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "H2DB Password changing script run test", enabled = false)
    public void testScriptRun() throws Exception {

        serverPropertyMap.put("-DportOffset", Integer.toString(portOffset));
        AutomationContext autoCtx = new AutomationContext();
        TestServerManager server =
                new TestServerManager(autoCtx, System.getProperty("carbon.zip"), serverPropertyMap);
        manager.startServers(server);
        carbonHome = server.getCarbonHome();
        UserPopulator userPopulator = new UserPopulator("AS", "appServerInstance0002");
        userPopulator.populateUsers();
        manager.stopAllServers();

        String[] cmdArray;
        String commandDirectory = carbonHome + File.separator + "bin";
        char[] dbPassword = {'r', 'o', 'o', 't'};

        if (CarbonCommandToolsUtil.isCurrentOSWindows()) {
            cmdArray = new String[]{
                    "cmd.exe", "/c", "chpasswd.sh", "--db-url", MYSQL_DB_URL,
                    "--db-driver", "com.mysql.jdbc.Driver", "--db-username", "root", "--db-password",
                    String.valueOf(dbPassword), "--username", "testu1", "--new-password",
                    String.valueOf(userNewPassword)};
        } else {
            cmdArray = new String[]{
                    "sh", "chpasswd.sh", "--db-url", MYSQL_DB_URL,
                    "--db-driver", "com.mysql.jdbc.Driver", "--db-username", "root", "--db-password",
                    String.valueOf(dbPassword), "--username", "testu1", "--new-password",
                    String.valueOf(userNewPassword)};
        }

        scriptRunStatus =
                CarbonCommandToolsUtil.isScriptRunSuccessfully(commandDirectory, cmdArray,
                                                               "Password updated successfully");
        log.info("Script running status : " + scriptRunStatus);
        assertTrue(scriptRunStatus, "Script executed successfully");


        manager.startServers(server);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "H2DB password change test", dependsOnMethods = {"testScriptRun"},
    enabled = false)
    public void testChangeUserPasswordMySQL() throws Exception {
        String loginStatusString = authenticatorClient.login
                ("testu1", String.valueOf(userNewPassword), context.getInstance().getHosts().get("default"));
        assertTrue(loginStatusString.contains("JSESSIONID"), "Unsuccessful login");

    }

    @AfterClass(alwaysRun = true)
    public void serverShutDown() throws Exception {
        manager.stopAllServers();
    }


}
