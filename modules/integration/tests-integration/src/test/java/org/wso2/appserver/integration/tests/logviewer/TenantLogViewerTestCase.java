/*
 * Copyright 2005,2014 WSO2, Inc. http://www.wso2.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appserver.integration.tests.logviewer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.LogViewerClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogFileInfo;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class test the log viewer feature in the tenant domains
 */
public class TenantLogViewerTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(TenantLogViewerTestCase.class);
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
    }

    @Factory(dataProvider = "userModeProvider")
    public TenantLogViewerTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][] {
                new TestUserMode[]{TestUserMode.TENANT_USER}
        };
    }

    @AfterClass(alwaysRun = true)
    public void webApplicationDelete() throws Exception {
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile("jaxrs_sample_02.war");
        log.info("jaxrs_sample_02.war deleted successfully");
    }

    @Test(groups = "wso2.as", description = "Deploy a jaxrs webapp and then" +
                                            "Open the application log viewer and get logs")
    public void testGetApplicationLogs() throws Exception {
        // deploy a web app in the current tenant domain before acquiring application logs
        deploySampleWebAppInTenantDomain();
        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        String appName = "jaxrs_sample_02";
        PaginatedLogEvent logEvents = logViewerClient.getPaginatedApplicationLogEvents(0, "ALL", "",
                                                                                       appName, "",
                                                                                       "");
        LogEvent receivedLogEvent = logEvents.getLogInfo()[0];
        assertEquals(receivedLogEvent.getAppName(), appName,
                     "Invalid app name was returened.");
        assertEquals(receivedLogEvent.getTenantId(), "1",
                     "Unexpected tenant Id was returned.");
    }

    @Test(groups = "wso2.as", description = "Open the log viewer and get logs of the tenant")
    public void testGetPaginatedLogEvents() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        PaginatedLogEvent logEvents = logViewerClient
                .getPaginatedLogEvents(0, "ALL", "", "wso2.com", "");

        LogEvent receivedLogEvent = logEvents.getLogInfo()[0];

        assertEquals(receivedLogEvent.getServerName(), "AS",
                     "Unexpected server name was returned.");
        assertEquals(receivedLogEvent.getTenantId(), "1",
                     "Unexpected tenant Id was returned.");
    }

    @Test(groups = "wso2.as", description = "Get the local log file information per tenant")
    public void testGetLocalLogFiles() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        PaginatedLogFileInfo logFileInfo = logViewerClient.getLocalLogFiles(0, "", "");
        // Since there's no per tenant log file present, backend is supposed to send a msg
        // which says No log files are present.
        assertEquals(logFileInfo.getLogFileInfo()[0].getLogName(), "NO_LOG_FILES",
                     "Unexpected log was returned.");
    }

    /**
     * Deploys a sample jaxrs web app in the tenant domain.
     *
     * @throws Exception
     */
    private void deploySampleWebAppInTenantDomain() throws Exception {
        WebAppAdminClient webAppAdminClient =
                new WebAppAdminClient(backendURL, sessionCookie);
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator + "AS" + File.separator + "jaxrs" + File
                .separator;
        webAppAdminClient.warFileUplaoder(location + File.separator + "jaxrs_sample_02.war");
        boolean isDeployed =
                WebAppDeploymentUtil
                        .isWebApplicationDeployed(backendURL, sessionCookie, "jaxrs_sample_02");
        assertTrue(isDeployed, "WebApp not deployed");
    }

}
