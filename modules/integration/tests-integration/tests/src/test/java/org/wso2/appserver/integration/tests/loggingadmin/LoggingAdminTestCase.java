/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.tests.loggingadmin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.LogLevel;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.LoggingAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.logging.admin.stub.types.carbon.AppenderData;

import static org.testng.Assert.assertEquals;

public class LoggingAdminTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(LoggingAdminTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        // start the server in super tenant mode
        super.init();
    }

    @Test(groups = "wso2.as", description = "Set the global logging to different level and restore defaults")
    public void testRestoreDefaults() throws Exception {
        LoggingAdminClient loggingAdminClient = new LoggingAdminClient(backendURL, sessionCookie);

        String logPattern = "[%d] - %x %m {%c}%n";
        String logLevel = LogLevel.FATAL.getLabel();

        loggingAdminClient.updateSystemLog(logLevel, logPattern, true);
        assertEquals(loggingAdminClient.getSysLog().getLogLevel(), logLevel, "Unexpected log level was returned.");
        assertEquals(loggingAdminClient.getSysLog().getLogPattern(), logPattern,
                "Unexpected log pattern was returned.");

        String defaultLogLevel = LogLevel.INFO.getLabel();
        String defaultLogPattern = "[%d] %5p - %x %m {%c}%n";

        loggingAdminClient.restoreToDefaults();
        assertEquals(loggingAdminClient.getSysLog().getLogLevel(), defaultLogLevel,
                "Default log level was not returned.");
        assertEquals(loggingAdminClient.getSysLog().getLogPattern(), defaultLogPattern,
                "Default log pattern was not returned.");
    }

    @Test(groups = "wso2.as", description = "Get Appender data for appender name")
    public void testGetAppenderData() throws Exception {
        LoggingAdminClient loggingAdminClient = new LoggingAdminClient(backendURL, sessionCookie);

        String appenderName = "AUDIT_LOGFILE";
        AppenderData appenderData = loggingAdminClient.getAppenderData(appenderName);
        assertEquals(appenderData.getName(), appenderName, "Unexpected appender data was returned.");
    }
}
