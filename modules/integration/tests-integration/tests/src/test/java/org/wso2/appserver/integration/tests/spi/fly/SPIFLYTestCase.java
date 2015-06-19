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
 *
 */

package org.wso2.appserver.integration.tests.spi.fly;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;

public class SPIFLYTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(SPIFLYTestCase.class);

    protected ServerConfigurationManager serverManager;

    private static final String SPIFLY_EXAMPLE_SPI_BUNDLE = "org.apache.aries.spifly.examples.spi.bundle-1.0.1-SNAPSHOT.jar";
    private static final String SPIFLY_EXAMPLE_PROVIDER_BUNDLE = "org.apache.aries.spifly.examples.provider1.bundle-1.0.1-SNAPSHOT.jar";
    private static final String SPIFLY_EXAMPLE_CLIENT_BUNDLE = "org.apache.aries.spifly.examples.client1.bundle-1.0.1-SNAPSHOT_spifly.jar";
    private static final String ARIES_UTIL_BUNDLE = "org.apache.aries.util-1.1.0.jar";
    private static final String ARIES_STATIC_WEAVING_BUNDLE = "org.apache.aries.spifly.static.bundle-1.0.0.jar";
    protected String ARTIFACTS_LOCATION = TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator +
            "AS" + File.separator + "bundles" + File.separator;

    File spiBundle = new File(ARTIFACTS_LOCATION + SPIFLY_EXAMPLE_SPI_BUNDLE);
    File providerBundle = new File(ARTIFACTS_LOCATION + SPIFLY_EXAMPLE_PROVIDER_BUNDLE);
    File clientBundle = new File(ARTIFACTS_LOCATION + SPIFLY_EXAMPLE_CLIENT_BUNDLE);
    File utilBundle = new File(ARTIFACTS_LOCATION + ARIES_UTIL_BUNDLE);
    File weavingBundle = new File(ARTIFACTS_LOCATION + ARIES_STATIC_WEAVING_BUNDLE);

    String checkString = "Result from invoking the SPI consume via library: Doing it!";


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(asServer);

        serverManager.copyToComponentDropins(spiBundle);
        serverManager.copyToComponentDropins(providerBundle);
        serverManager.copyToComponentDropins(clientBundle);
        serverManager.copyToComponentDropins(utilBundle);
        serverManager.copyToComponentDropins(weavingBundle);

        serverManager.restartGracefully();
        super.init();
        log.info("Server Restarted after applying spi-fly sample bundles");

    }

    @Test(groups = "wso2.as", description = "Check SPI Logs to verify")
    public void testGetApplicationLogs() throws RemoteException, LogViewerLogViewerException {
        LogViewerStub logViewerStub = new LogViewerStub(backendURL + "LogViewer");
        AuthenticateStubUtil.authenticateStub(sessionCookie, logViewerStub);
        LogEvent[] logEvents = logViewerStub.getAllSystemLogs();
        boolean spiFlyWorked = false;

        for (LogEvent logEvent : logEvents) {
            if (logEvent.getMessage().contains(checkString)) {
                spiFlyWorked =true;
            }
        }
        Assert.assertTrue(spiFlyWorked, "SPI-FLY Client failed to get provider !!");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        try {
            serverManager.removeFromComponentDropins(SPIFLY_EXAMPLE_SPI_BUNDLE);
            serverManager.removeFromComponentDropins(SPIFLY_EXAMPLE_PROVIDER_BUNDLE);
            serverManager.removeFromComponentDropins(SPIFLY_EXAMPLE_CLIENT_BUNDLE);
            serverManager.removeFromComponentDropins(ARIES_UTIL_BUNDLE);
            serverManager.removeFromComponentDropins(ARIES_STATIC_WEAVING_BUNDLE);
        } catch (IOException e) {
            log.error("Failed to cleanup dropins");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
