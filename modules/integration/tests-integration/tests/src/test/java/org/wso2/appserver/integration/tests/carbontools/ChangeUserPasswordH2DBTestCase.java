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
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.CarbonCommandToolsUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.frameworkutils.enums.OperatingSystems;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.extensions.usermgt.UserPopulator;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;

import java.io.File;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test change H2DB user password using chpasswd.sh/chpasswd.bat
 * All test methods in this class has disabled because need same features from unreleased automation
 * framework after the automation framework 4.3.2 released have to enable test methods
 */

public class ChangeUserPasswordH2DBTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ChangeUserPasswordH2DBTestCase.class);
    private boolean scriptRunStatus;
    private AutomationContext context;
    private int portOffset = 1;
    private HashMap<String, String> serverPropertyMap = new HashMap<String, String>();
    private String carbonHome = null;
    private static String H2DB_URL;
    private AuthenticatorClient authenticatorClient;
    private static final char[] userNewPassword = {'t', 'e', 's', 't', 'u', '1', '2', '3'};
    private static final  String userName = "testu1";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        String PRODUCT_NAME = "AS";
        String INSTANCE = "appServerInstance0002";
        context = new AutomationContext(PRODUCT_NAME, INSTANCE,
                                        ContextXpathConstants.SUPER_TENANT,
                                        ContextXpathConstants.SUPER_ADMIN);
        authenticatorClient = new AuthenticatorClient(context.getContextUrls().getBackEndUrl());
        H2DB_URL = context.getConfigurationValue(String.format(ASIntegrationConstants.CONTEXT_XPATH_DB_CONNECTION_URL, "H2DB"));
    }


    @Test(groups = "wso2.as", description = "H2DB Password changing script run test")
    public void testScriptRunChangeUserPasswordH2DB() throws Exception {
        final char[] dbPassword = {'w', 's', 'o', '2', 'c', 'a', 'r', 'b', 'o', 'n'};
        serverPropertyMap.put("-DportOffset", Integer.toString(portOffset));
        AutomationContext autoCtx = new AutomationContext();
        CarbonTestServerManager server = new CarbonTestServerManager(autoCtx, System.getProperty("carbon.zip"), serverPropertyMap);
        carbonHome = server.startServer();
        UserPopulator userPopulator = new UserPopulator("AS", "appServerInstance0002");
        userPopulator.populateUsers();
        server.stopServer();
        String[] cmdArray;
        final String commandDirectory = carbonHome + File.separator + "bin";


            if ((CarbonCommandToolsUtil.getCurrentOperatingSystem().
                    contains(OperatingSystems.WINDOWS.name().toLowerCase()))) {
                cmdArray = new String[]
                        {"cmd.exe", "/c", "chpasswd.bat", "--db-url", "jdbc:h2:" + carbonHome + H2DB_URL,
                         "--db-driver", "org.h2.Driver", "--db-username", "wso2carbon", "--db-password",
                         String.valueOf(dbPassword), "--username", userName, "--new-password",
                         String.valueOf(userNewPassword)};
            } else {
                cmdArray = new String[]
                        {"sh", "chpasswd.sh", "--db-url", "jdbc:h2:" + carbonHome + H2DB_URL, "--db-driver",
                         "org.h2.Driver", "--db-username", "wso2carbon", "--db-password",
                         String.valueOf(dbPassword), "--username", userName, "--new-password",
                         String.valueOf(userNewPassword)};
            }

            scriptRunStatus =
                    CarbonCommandToolsUtil.isScriptRunSuccessfully(commandDirectory, cmdArray,
                                                                   "Password updated successfully");
            log.info("Script running status : " + scriptRunStatus);
            assertTrue(scriptRunStatus, "Script executed unsuccessfully");


        CarbonCommandToolsUtil.startServerUsingCarbonHome(carbonHome,1,context,null);


    }

    @Test(groups = "wso2.as", description = "H2DB password change test",
            dependsOnMethods = {"testScriptRunChangeUserPasswordH2DB"})
    public void testChangeUserPasswordH2DB() throws Exception {
        String loginStatusString = authenticatorClient.login
                (userName, String.valueOf(userNewPassword), context.getInstance().getHosts().get("default"));
        assertTrue(loginStatusString.contains("JSESSIONID"), "Unsuccessful login");

    }

    @AfterClass(alwaysRun = true)
    public void serverShutDown() throws Exception {
        CarbonCommandToolsUtil.serverShutdown(1,context);
    }


}

