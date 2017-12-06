/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.appserver.integration.tests.logviewer;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.LogViewerClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;

import java.rmi.RemoteException;

/**
 * This test case addresses the path traversal security vulnerability in LogViewer admin service,
 */
public class PathTraversalTestCase extends ASIntegrationTest {

    private LogViewerClient logViewerClient;
    private static final String WSO2CARBON_LOGFILE = "wso2carbon.log";
    private static final String PATCH_LOGFILE = "patches.log";
    private static final String CARBON_CONFIG_FILE = "../conf/carbon.xml";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        logViewerClient = new LogViewerClient(backendURL, sessionCookie);
    }

    @Test(groups = {"wso2.greg"}, description = "tests the possibility of reading CARBON_LOGFILE appender log " +
            "file from LogViewer admin service.")
    public void testReadingWSO2CarbonLogFile() throws LogViewerLogViewerException, RemoteException {
        String[] wso2carbonLogs = logViewerClient.getLogLinesFromFile(WSO2CARBON_LOGFILE, 40, 0, 40);
        Assert.assertTrue(wso2carbonLogs.length > 0, "WSO2 Carbon log file is empty");
    }

    @Test(groups = {"wso2.greg"}, description = "tests the possibility of reading other log files other than " +
            "CARBON_LOGFILE appender log file in carbon logs directory.",
            expectedExceptions = LogViewerLogViewerException.class)
    public void testReadingLogFilesOtherThanWSO2CarbonLog() throws LogViewerLogViewerException, RemoteException {
        logViewerClient.getLogLinesFromFile(PATCH_LOGFILE, 40, 0, 40);
    }

    @Test(groups = {"wso2.greg"}, description = "tests the possibility of reading files outside carbon logs directory.",
            expectedExceptions = LogViewerLogViewerException.class)
    public void testReadingFilesOutsideCarbonLogsDirectory() throws LogViewerLogViewerException, RemoteException {
        logViewerClient.getLogLinesFromFile(CARBON_CONFIG_FILE, 40, 0, 40);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
