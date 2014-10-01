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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.LogViewerClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogFileInfo;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class test the log viewer feature in the super tenant domain
 */
public class LogViewerTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(LogViewerTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        // start the server in super tenant mode
        super.init();
    }

    @Test(groups = "wso2.as", description = "Open the log viewer and get logs")
    public void testGetPaginatedLogEvents() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        PaginatedLogEvent logEvents = logViewerClient
                .getPaginatedLogEvents(0, "ALL", "", "", "");

        LogEvent receivedLogEvent = logEvents.getLogInfo()[0];

        assertEquals(receivedLogEvent.getServerName(), "AS",
                     "Unexpected server name was returned.");
        assertEquals(receivedLogEvent.getTenantId(), "-1234",
                     "Unexpected tenant Id was returned.");
    }

    @Test(groups = "wso2.as", description = "Open the application log viewer and get logs")
    public void testGetApplicationLogs() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        String appName = "echo";
        PaginatedLogEvent logEvents = logViewerClient.getPaginatedApplicationLogEvents(0, "ALL", "",
                                                                                       appName, "",
                                                                                       "");
        LogEvent receivedLogEvent = logEvents.getLogInfo()[0];
        // should always return the correct app name as requested
        assertEquals(receivedLogEvent.getAppName(), appName,
                     "Invalid app name was returened.");
        assertEquals(receivedLogEvent.getTenantId(), "-1234",
                     "Unexpected tenant Id was returned.");
    }

    @Test(groups = "wso2.as", description = "Get the local log file information")
    public void testGetLocalLogFiles() throws Exception {
        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        PaginatedLogFileInfo logFileInfo = logViewerClient.getLocalLogFiles(0, "", "");
        assertEquals(logFileInfo.getLogFileInfo()[0].getLogDate(), "0_Current Log",
                     "Unexpected log date was returned.");
    }

}
