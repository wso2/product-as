/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.tests.ciphertool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.PasswordEncryptionUtil;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.extensions.servers.utils.ClientConnectionUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.admin.client.ServerAdminClient;
import org.wso2.carbon.integration.common.extensions.carbonserver.MultipleServersManager;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class test H2DB password encryption by doing the following steps.
 * Configure the cipher-text.properties.
 * Run the ciphertool.sh using /usr/bin/expect and give a password to encrypt.
 * And check the master-datasources.xml has encrypted password.
 */
public class H2DBPasswordEncryptionTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(H2DBPasswordEncryptionTestCase.class);
    private ServerConfigurationManager serverManager;
    private HashMap<String, String> serverPropertyMap = new HashMap<String, String>();
    private MultipleServersManager manager = new MultipleServersManager();
    private String CARBON_HOME = null;
    private AutomationContext autoCtx = null;
    private static final long DEFAULT_START_STOP_WAIT_MS = 1000 * 60 * 5;
    private LogViewerClient logViewerClient;
    private static final String SERVER_START_LINE = "Starting WSO2 Carbon";
    private static final String MANAGEMENT_CONSOLE_URL = "Mgt Console URL";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        File createdFile = new File("/usr/bin/expect");
        if (!createdFile.isFile()) {
            throw new SkipException("Skipping tests because /usr/bin/expect was not available.");
        }
        super.init();
        serverPropertyMap.put("-DportOffset", "1");
        autoCtx = new AutomationContext();
        CarbonTestServerManager server =
                new CarbonTestServerManager(autoCtx, System.getProperty("carbon.zip"), serverPropertyMap);
        manager.startServers(server);
        CARBON_HOME = server.getCarbonHome();
        serverManager = new ServerConfigurationManager(asServer);

        File sourceFile = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
                                   "artifacts" + File.separator + "AS" + File.separator + "ciphertool" +
                                   File.separator + "cipher-text.properties");

        File targetFile = new File(CARBON_HOME + File.separator + "repository" + File.separator + "conf" +
                                   File.separator + "security" + File.separator + "cipher-text.properties");

        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
    }

    @AfterClass(alwaysRun = true)
    public void stopServers() throws Exception {
        manager.stopAllServers();
    }

    @Test(groups = {"wso2.as"}, description = "Test the password before encryption")
    public void testCheckBeforeEncrypt() throws Exception {
        boolean passwordBeforeEncryption = PasswordEncryptionUtil.isPasswordEncrypted(CARBON_HOME);
        assertFalse(passwordBeforeEncryption, "Password has already encrypted");
    }

    @Test(groups = {"wso2.as"}, description = "Test script run successfully",
            dependsOnMethods = {"testCheckBeforeEncrypt"})
    public void testCheckScriptRunSuccessfully() throws Exception {

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            throw new SkipException("Skipping tests because of windows.");
        } else {
            File sourceRunFile = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
                                          "artifacts" + File.separator + "AS" + File.separator + "ciphertool" +
                                          File.separator + "run.sh");

            String[] cmdArray = new String[]{"/usr/bin/expect", "run.sh"};

            File targetRunFile = new File(CARBON_HOME + File.separator + "bin" + File.separator + "run.sh");
            serverManager.applyConfigurationWithoutRestart(sourceRunFile, targetRunFile, false);
            boolean isScriptSuccess = PasswordEncryptionUtil.runCipherToolScriptAndCheckStatus(CARBON_HOME, cmdArray);
            assertTrue(isScriptSuccess, "H2DB Password Encryption failed");
        }

    }

    @Test(groups = {"wso2.as"}, description = "H2DB Password Encryption Test",
            dependsOnMethods = {"testCheckScriptRunSuccessfully"})
    public void testCheckEncryptedPassword() throws Exception {
        boolean passwordAfterEncryption = PasswordEncryptionUtil.isPasswordEncrypted(CARBON_HOME);
        assertTrue(passwordAfterEncryption, "H2DB Password Encryption failed");
    }

    @Test(groups = {"wso2.as"}, description = "Restart encrypted server test",
            dependsOnMethods = {"testCheckEncryptedPassword"})
    public void testRestartEncryptedServer() throws Exception {

        AutomationContext automationContext =
                new AutomationContext("AS", "appServerInstance0002",
                                      ContextXpathConstants.SUPER_TENANT,
                                      ContextXpathConstants.ADMIN);

        ServerAdminClient serverAdmin =
                new ServerAdminClient(automationContext.getContextUrls().getBackEndUrl(),
                                      autoCtx.getContextTenant().getContextUser().getUserName(),
                                      autoCtx.getContextTenant().getContextUser().getPassword());

        File sourceTempPasswordFile =
                new File(TestConfigurationProvider.getResourceLocation() + File.separator +
                         "artifacts" + File.separator + "AS" + File.separator + "ciphertool" +
                         File.separator + "password-tmp");

        File targetTempPasswordFile = new File(CARBON_HOME + File.separator + "password-tmp");

        serverManager.applyConfigurationWithoutRestart(sourceTempPasswordFile, targetTempPasswordFile, false);
        serverAdmin.restartGracefully();

        ClientConnectionUtil.waitForPort(
                Integer.parseInt(automationContext.getInstance().getPorts().get("http")),
                DEFAULT_START_STOP_WAIT_MS, false, automationContext.getInstance().getHosts().get("default"));

        ClientConnectionUtil.waitForLogin(automationContext);

        logViewerClient =
                new LogViewerClient(automationContext.getContextUrls().getBackEndUrl(),
                                    automationContext.getSuperTenant().getTenantAdmin().getUserName(),
                                    automationContext.getSuperTenant().getTenantAdmin().getPassword());
    }

    @Test(groups = "wso2.as", description = "verify server startup errors",
            dependsOnMethods = {"testRestartEncryptedServer"})
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void testVerifyLogs() throws RemoteException, LogViewerLogViewerException {
        boolean status = false;
        int startLine = 0;
        int stopLine = 0;
        LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
        if (logEvents.length > 0) {
            for (int i = 0; i < logEvents.length; i++) {
                if (logEvents[i] != null) {
                    if (logEvents[i].getMessage().contains(SERVER_START_LINE)) {
                        stopLine = i;
                        log.info("Server started message found - " + logEvents[i].getMessage());
                    }
                    if (logEvents[i].getMessage().contains(MANAGEMENT_CONSOLE_URL)) {
                        startLine = i;
                        log.info("Server stopped message found - " + logEvents[i].getMessage());
                    }
                }
                if (startLine != 0 && stopLine != 0) {
                    status = true;
                    break;
                }
            }
        }
        assertTrue(status, "Couldn't start the server");
    }
}
