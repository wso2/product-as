/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.tests.logging.tenantawarelogs;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.LogViewerClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.testng.Assert.assertTrue;

/**
 * This class contains test cases that to test the logs are generated with tenant information
 * are recorded properly under different configurations
 */
public class TenantAwareLoggingTestCase extends ASIntegrationTest {
	private TestUserMode userMode;

	@Factory(dataProvider = "userModeProvider")
	public TenantAwareLoggingTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@DataProvider
	private static TestUserMode[][] userModeProvider() {
		return new TestUserMode[][] { new TestUserMode[] { TestUserMode.SUPER_TENANT_ADMIN },
		                              new TestUserMode[] { TestUserMode.TENANT_USER } };
	}

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init(userMode);
		sessionCookie = loginLogoutClient.login();
	}

	@Test(groups = "wso2.as", description = "Test memory appender for login information")
	public void testMemoryAppenderTenantLogs() throws Exception {
		LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
		PaginatedLogEvent paginatedLogEvents = logViewerClient.getPaginatedLogEvents(0, "ALL", "", "", "");

		LogEvent[] logEvents = paginatedLogEvents.getLogInfo();
		boolean isLogInLogsRecorded = false;
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			for (LogEvent logEvent : logEvents) {
				String logMessage = logEvent.getMessage();
				if (logMessage.contains("'" + userInfo.getUserName() + "@carbon.super [-1234]' logged in at")) {
					isLogInLogsRecorded = true;
					break;
				}
			}
		} else if (userMode == TestUserMode.TENANT_USER) {
			for (LogEvent logEvent : logEvents) {
				String logMessage = logEvent.getMessage();
				if (logMessage.contains("'" + userInfo.getUserName() + " [1]' logged in at")) {
					isLogInLogsRecorded = true;
					break;
				}
			}
		}
		assertTrue(isLogInLogsRecorded,
		           "Logging details with tenant information is not logged to Carbon memory appender in user mode " +
		           userMode);
	}

	@Test(groups = "wso2.as", description = "Test tenant log records in audit log file")
	public void testAuditLogFile() throws Exception {
		File file = new File(
				System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
				"logs" +
				File.separator + "audit.log");
		assertTrue(file.exists(), "Audit log file is not created in user mode " + userMode);
		String fileContent = FileManager.readFile(file);
		String[] logList = fileContent.split(System.getProperty("line.separator"));
		String rolledLogFilecontent;
		String[] rolledLogs = null;

		//Load rolled audit log file
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);

		File rolledLogfile = new File(
				System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
				"logs" +
				File.separator + "audit.log." + dateFormat.format(calendar.getTime()));

		boolean isLogInLogsRecorded = false;
		boolean isUserAddingRecorded = false;

		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			for (String logRecord : logList) {
				if (logRecord.contains("'" + userInfo.getUserName() + "@carbon.super [-1234]' logged in at")) {
					isLogInLogsRecorded = true;
					break;
				}
			}
			//Check in rolled log file if the log is not in audit.log
			if (!isLogInLogsRecorded) {
				rolledLogFilecontent = FileManager.readFile(rolledLogfile);
				rolledLogs = rolledLogFilecontent.split(System.getProperty("line.separator"));
				for (String logRecord : rolledLogs) {
					if (logRecord.contains("'" + userInfo.getUserName() + "@carbon.super [-1234]' logged in at")) {
						isLogInLogsRecorded = true;
						break;
					}
				}
			}

		} else if (userMode == TestUserMode.TENANT_USER) {
			String userName = userInfo.getUserName().split("@")[0];
			for (String logRecord : logList) {
				if (logRecord.contains("Action : Add User | Target : " +
				                       userName + " | Data : { Roles :admin, } | Result : Success  ")) {
					isUserAddingRecorded = true;
				}
				if (logRecord.contains("'" + userInfo.getUserName() + " [1]' logged in at")) {
					isLogInLogsRecorded = true;
					break;
				}
			}

			//Check in rolled log file if the log is not in audit.log
			if (!isUserAddingRecorded) {
				if (rolledLogs == null) {
					rolledLogFilecontent = FileManager.readFile(rolledLogfile);
					rolledLogs = rolledLogFilecontent.split(System.getProperty("line.separator"));
				}
				for (String logRecord : rolledLogs) {
					if (logRecord.contains("Action : Add User | Target : " +
					                       userName + " | Data : { Roles :admin, } | Result : Success  ")) {
						isUserAddingRecorded = true;
					}
					if (logRecord.contains("'" + userInfo.getUserName() + " [1]' logged in at")) {
						isLogInLogsRecorded = true;
						break;
					}
				}
			}
			assertTrue(isUserAddingRecorded, "Adding new user from super tenant is not recorded in the audit log file");
		}
		assertTrue(isLogInLogsRecorded,
		           "Logging details with tenant information is not logged to audit log file in user mode " + userMode);
	}

	@Test(groups = "wso2.as", description = "Test logged in log records in carbon log file")
	public void testCarbonLogFile() throws Exception {
		File file = new File(
				System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
				"logs" +
				File.separator + "wso2carbon.log");
		assertTrue(file.exists(), "Carbon log file is not created");
		String fileContent = FileManager.readFile(file);

		String[] logList = fileContent.split(System.getProperty("line.separator"));
		boolean isLogInLogsRecorded = false;
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			for (String logRecord : logList) {
				if (logRecord.contains("'" + userInfo.getUserName() + "@carbon.super [-1234]' logged in at")) {
					isLogInLogsRecorded = true;
					break;
				}
			}
		} else if (userMode == TestUserMode.TENANT_USER) {
			for (String logRecord : logList) {
				if (logRecord.contains("'" + userInfo.getUserName() + " [1]' logged in at")) {
					isLogInLogsRecorded = true;
					break;
				}
			}
		}
		assertTrue(isLogInLogsRecorded,
		           "Logging details with tenant information is not logged to audit log file in user mode " + userMode);
	}
}
