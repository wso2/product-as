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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.bean.DataSourceBean;
import org.wso2.appserver.integration.common.exception.CarbonToolsIntegrationTestException;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.CarbonCommandToolsUtil;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.enums.OperatingSystems;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.extensions.exceptions.AutomationExtensionException;
import org.wso2.carbon.integration.common.extensions.usermgt.UserPopulator;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test change H2DB user password with special characters using chpasswd.sh/chpasswd.bat
 * All test methods in this class has disabled because need same features from unreleased automation
 * framework after the automation framework 4.3.2 released have to enable test methods
 */
public class ChangeUserPasswordWithSpecialCharacterH2DBTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ChangeUserPasswordH2DBTestCase.class);
    private AutomationContext context;
    private int portOffset = 1;
    private DataSourceBean dataSourceBean;
    private AuthenticatorClient authenticatorClient;
    private final char[] userNewPassword = {'m', '7', 'c', 't', '6', 'b', ']', ']', ':', '}', 'a', '3', '#', 'F', 'B', 'n'};
    private String userName = "testu1";
    private TestServerManager testServerManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        context = new AutomationContext(ASIntegrationConstants.AS_PRODUCT_GROUP,
                                        ASIntegrationConstants.AS_INSTANCE_0002,
                                        ContextXpathConstants.SUPER_TENANT,
                                        ContextXpathConstants.SUPER_ADMIN);
        authenticatorClient = new AuthenticatorClient(context.getContextUrls().getBackEndUrl());
        dataSourceBean = CarbonCommandToolsUtil.getDataSourceInformation("default");

        portOffset = Integer.parseInt(context.getInstance().getProperty("portOffset"));
    }

    @Test(groups = "wso2.as", description = "H2DB Password changing script run test")
    public void testScriptRunChangeUserPasswordWithCharacterH2DBTestCase() throws Exception {
        AutomationContext autoCtx = new AutomationContext();
        testServerManager = new TestServerManager(autoCtx, portOffset) {
            public void configureServer() throws AutomationFrameworkException {

                try {
                    testServerManager.startServer();
                    UserPopulator userPopulator = new UserPopulator(ASIntegrationConstants.AS_PRODUCT_GROUP,
                                                                    ASIntegrationConstants.AS_INSTANCE_0002);
                    userPopulator.populateUsers();
                    testServerManager.stopServer();
                    carbonHome = testServerManager.getCarbonHome();
                    String commandDirectory = carbonHome + File.separator + "bin";
                    String[] cmdArray;

                    if ((CarbonCommandToolsUtil.getCurrentOperatingSystem().contains(
                            OperatingSystems.WINDOWS.name().toLowerCase()))) {

                        cmdArray =
                                new String[]{
                                        "cmd.exe", "/c", "chpasswd.bat",
                                        "--db-url", "jdbc:h2:" + carbonHome + dataSourceBean.getUrl(),
                                        "--db-driver", dataSourceBean.getDriverClassName(), "--db-username",
                                        dataSourceBean.getUserName(), "--db-password",
                                        String.valueOf(dataSourceBean.getPassWord()), "--username",
                                        userName, "--new-password", String.valueOf(userNewPassword)};
                    } else {

                        cmdArray =
                                new String[]{
                                        "sh", "chpasswd.sh", "--db-url",
                                        "jdbc:h2:" + carbonHome + dataSourceBean.getUrl(), "--db-driver",
                                        "org.h2.Driver", "--db-username", "wso2carbon",
                                        "--db-password", String.valueOf(dataSourceBean.getPassWord()),
                                        "--username", userName, "--new-password", String.valueOf(userNewPassword)};
                    }

                    boolean scriptRunStatus =
                            CarbonCommandToolsUtil.isScriptRunSuccessfully(commandDirectory, cmdArray,
                                                                           "Password updated successfully");
                    log.info("Script running status : " + scriptRunStatus);
                    assertTrue(scriptRunStatus, "Script executed unsuccessfully");

                } catch (IOException e) {
                    throw new AutomationFrameworkException("Error when starting the carbon server", e);
                } catch (CarbonToolsIntegrationTestException e) {
                    throw new AutomationFrameworkException("Error when running the chpasswd script", e);
                } catch (XPathExpressionException e) {
                    throw new AutomationFrameworkException("Error when starting the carbon server", e);
                } catch (AutomationExtensionException e) {
                    throw new AutomationFrameworkException("Error when populating users", e);
                }
            }
        };
        testServerManager.startServer();
    }

    @Test(groups = "wso2.as", description = "H2DB password change test",
            dependsOnMethods = {"testScriptRunChangeUserPasswordWithCharacterH2DBTestCase"})
    public void testChangeUserPasswordWithCharacterH2DB()
            throws XPathExpressionException, RemoteException,
                   LoginAuthenticationExceptionException {
        String loginStatusString = authenticatorClient.login
                (userName, String.valueOf(userNewPassword), context.getInstance().getHosts().get("default"));
        assertTrue(loginStatusString.contains("JSESSIONID"), "Unsuccessful login");
    }

    @AfterClass(alwaysRun = true)
    public void serverShutDown() throws CarbonToolsIntegrationTestException {
        CarbonCommandToolsUtil.serverShutdown(portOffset, context);
    }
}

