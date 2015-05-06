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

package org.wso2.appserver.integration.tests.logviewer;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.AuthenticateStubUtil;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.LogViewerStub;

import java.io.File;
import java.rmi.RemoteException;

/*
 * This test case is to test the proper log viewing functionality of the log viewer when org.apache.log4j.FileAppender
 * is set as the Appender for CARBON_LOGFILE.
 *
 */
public class CARBON15188LogViewerWithFileAppenderTestCase extends ASIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(asServer);
        String log4jFilePath = TestConfigurationProvider.getResourceLocation() + File.separator + "log4j"
                               + File.separator + "carbon15188" + File.separator + "log4j.properties";
        serverConfigurationManager.applyConfiguration(new File(log4jFilePath));
        super.init();
    }

    @Test(groups = {"wso2.as"}, description = "tests the log viewer functionality when FileAppender is set as the Appender for CARBON_LOGFILE")
    public void testLogViewerWithFileAppender() throws RemoteException, LogViewerLogViewerException {
        LogViewerStub logViewerStub = new LogViewerStub(backendURL + "LogViewer");
        AuthenticateStubUtil.authenticateStub(sessionCookie, logViewerStub);

        // for super tenant logViewerStub.getLocalLogFiles returns null, hence the null check
        Assert.assertNull(logViewerStub.getLocalLogFiles(0, "", ""), "Error in system log viewer when FileAppender is set as the Appender for CARBON_LOGFILE");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        serverConfigurationManager.restoreToLastConfiguration();
    }

}
