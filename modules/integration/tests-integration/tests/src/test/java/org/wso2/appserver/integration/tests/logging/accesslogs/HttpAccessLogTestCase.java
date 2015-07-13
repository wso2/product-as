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
package org.wso2.appserver.integration.tests.logging.accesslogs;

import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationLoggingUtil;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class contains test cases that to test the http access logs
 * are generated properly by catalina container of AS under different configurations
 */
@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
public class HttpAccessLogTestCase extends ASIntegrationTest {
	private static boolean isServerRestarted = false;
	private final String WEB_APP_NAME = "access-logs-test-webapp";
	private String date;
	private String logFileLocation;
	private TestUserMode userMode;
	private WebAppAdminClient webAppAdminClient;
	private ServerConfigurationManager serverConfigurationManager;
	private String tenantDomain;
	private File common_log_file;
	private File request_log_file;
	private File response_log_file;
	private File variable_log_file;
	private String[] logFileContentList;
	private String[] requestHeadersLogs;
	private String[] responseHeadersLogs;
	private String[] variablesLogs;

	@Factory(dataProvider = "userModeProvider")
	public HttpAccessLogTestCase(TestUserMode userMode) {
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

		if (!isServerRestarted) {
			serverConfigurationManager =
					new ServerConfigurationManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN));
			File sourceFile = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
			                           "artifacts" + File.separator + "AS" + File.separator + "tomcat" +
			                           File.separator + "test_http_access_logs" + File.separator +
			                           "catalina-server.xml");
			File targetFile = new File(
					System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
					"conf" +
					File.separator + "tomcat" + File.separator + "catalina-server.xml");
			serverConfigurationManager.applyConfiguration(sourceFile, targetFile, true, true);

			isServerRestarted = true;
		}
		sessionCookie = loginLogoutClient.login();
		logFileLocation =
				System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
				"logs" +
				File.separator;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		date = dateFormat.format(new Date());

		common_log_file = new File(logFileLocation + "http_access_" + date + ".log");
		request_log_file = new File(logFileLocation + "http_access_request_headers_test_" + date + ".log");
		response_log_file = new File(logFileLocation + "http_access_response_headers_test_" + date + ".log");
		variable_log_file = new File(logFileLocation + "http_access_variable_logging_" + date + ".log");

		tenantDomain = asServer.getContextTenant().getDomain();
	}

	@Test(groups = "wso2.as", description = "Upload webapp and verify deployment")
	public void testWebAppUpload() throws Exception {
		String sampleWebApp =
				System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
				File.separator + "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator +
				WEB_APP_NAME + ".war";
		sessionCookie = loginLogoutClient.login();
		webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
		webAppAdminClient.uploadWarFile(sampleWebApp);
		assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME));
	}

	@Test(groups = "wso2.as", description = "Send GET and POST requests to generate http access logs and read " +
	                                        "http access log files", dependsOnMethods = "testWebAppUpload")
	public void testWebAppResponse() throws Exception {
		//GET request
		HttpResponse response =
				HttpURLConnectionClient.sendGetRequest(getWebAppURL(WebAppTypes.WEBAPPS) + "/" + WEB_APP_NAME +
				                                       "/services/test_access_log/simpleget?name=abc&domain=wso2.com",
				                                       null);
		assertEquals(response.getResponseCode(), HttpStatus.SC_OK,
		             "GET Request was not successful in user mode : " + userMode);

		//POST Request
		assertEquals(makePostRequest(
				getWebAppURL(WebAppTypes.WEBAPPS) + "/" + WEB_APP_NAME + "/services/test_access_log/simplepost")
				             .toString(), "hello abc", "POST Request was not successful in user mode : " + userMode);

		//Register a watch service to wait until log files are created
		WatchService watcher = FileSystems.getDefault().newWatchService();
		Path filePath = Paths.get(logFileLocation);
		filePath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

		long time = System.currentTimeMillis() + 30 * 1000;

		boolean isNewLogFilesAreCreated = false;

		while (!isNewLogFilesAreCreated && System.currentTimeMillis() < time) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException ex) {
				return;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

					if (request_log_file.exists() && response_log_file.exists() && variable_log_file.exists()) {
						isNewLogFilesAreCreated = true;
						break;
					}
				}
			}

			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}

	}

	@Test(groups = "wso2.as", description = "Read contents of log files in to  string arrays",
			dependsOnMethods = "testWebAppResponse")
	public void testReadAccessLogFiles() throws Exception {
		logFileContentList = ASIntegrationLoggingUtil.getLogsFromLogfile(common_log_file);
		requestHeadersLogs = ASIntegrationLoggingUtil.getLogsFromLogfile(request_log_file);
		responseHeadersLogs = ASIntegrationLoggingUtil.getLogsFromLogfile(response_log_file);
		variablesLogs = ASIntegrationLoggingUtil.getLogsFromLogfile(variable_log_file);

		assertFalse((logFileContentList.length == 0),
		            "Http access log which uses combined pattern content is empty " + userMode);
		assertFalse((requestHeadersLogs.length == 0),
		            "Http access log used to record request headers content is empty " + userMode);
		assertFalse((responseHeadersLogs.length == 0),
		            "Http access log used to record response headers content is empty " + userMode);
		assertFalse((variablesLogs.length == 0), "Http access log used record variables content is empty " + userMode);
	}

	@Test(groups = "wso2.as", description = "Test access logs generated for GET request with default combined pattern",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testAccessLogForGetRequest() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					"GET /access-logs-test-webapp/services/test_access_log/simpleget?name=abc&domain=wso2.com HTTP/1.1",
					logFileContentList), "Access log is not properly updated for super tenant webapp - GET request");
		} else {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("GET /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/" +
			                                                    "test_access_log/simpleget?name=abc&domain=wso2.com HTTP/1.1",
			                                                    logFileContentList),
			           "Access log is not properly updated for tenant webapp - GET request");

		}
	}

	@Test(groups = "wso2.as", description = "Test access logs generated for POST request with default combined pattern",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testAccessLogForPostRequest() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					           "POST /access-logs-test-webapp/services/test_access_log/simplepost HTTP/1.1",
					           logFileContentList),
			           "Access log is not properly updated for super tenant webapp - POST request");
		} else {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("POST /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/" +
			                                                    "test_access_log/simplepost HTTP/1.1",
			                                                    logFileContentList),
			           "Access log is not properly updated for tenant webapp - POST request");
		}
	}

	@Test(groups = "wso2.as", description = "Test request headers for GET request",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testGetRequestHeaders() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					           "GET GET /access-logs-test-webapp/services/test_access_log/simpleget?name=abc&" +
					           "domain=wso2.com HTTP/1.1 - text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2",
					           requestHeadersLogs),
			           "Request headers are not properly logged for super tenant webapp - GET request");
		} else {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("GET GET /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/" +
			                                                    "test_access_log/simpleget?name=abc&domain=wso2.com HTTP/1.1 - " +
			                                                    "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2",
			                                                    requestHeadersLogs),
			           "Request headers are not properly logged for tenant webapp - GET request");
		}

	}

	@Test(groups = "wso2.as", description = "Test request headers for POST request",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testPostRequestHeaders() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					           "POST POST /access-logs-test-webapp/services/test_access_log/simplepost HTTP/1.1 text/" +
					           "plain text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", requestHeadersLogs),
			           "Request headers are not properly logged for super tenant webapp - POST request");
		} else {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("POST POST /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/" +
			                                                    "test_access_log/simplepost HTTP/1.1 text/plain text/" +
			                                                    "html, image/gif, image/jpeg, *; q=.2, */*; q=.2",
			                                                    requestHeadersLogs),
			           "Request headers are not properly logged for tenant webapp - POST request");
		}
	}

	@Test(groups = "wso2.as", description = "Test response headers for GET request",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testGetResponseHeaders() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					           "GET /access-logs-test-webapp/services/test_access_log/simpleget?name=abc&domain=" +
					           "wso2.com HTTP/1.1 text/html WSO2 Carbon Server 200", responseHeadersLogs),
			           "Response headers are not properly logged for super tenant webapp - GET request");
		} else {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("GET /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/test_access_log/" +
			                                                    "simpleget?name=abc&domain=wso2.com HTTP/1.1 text/html " +
			                                                    "WSO2 Carbon Server 200", responseHeadersLogs),
			           "Response headers are not properly logged for tenant webapp - GET request");
		}
	}

	@Test(groups = "wso2.as", description = "Test response headers logs for POST request",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testPostResponseHeaders() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					           "POST /access-logs-test-webapp/services/test_access_log/simplepost HTTP/1.1 text/" +
					           "html WSO2 Carbon Server 200", responseHeadersLogs),
			           "Response headers are not properly logged for super tenant webapp - POST request");
		} else {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("POST /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/test_access_log/" +
			                                                    "simplepost HTTP/1.1 text/html WSO2 Carbon Server 200",
			                                                    responseHeadersLogs),
			           "Response headers are not properly logged for tenant webapp - POST request");
		}

	}

	@Test(groups = "wso2.as", description = "Test GET request variable logs",
			dependsOnMethods = "testReadAccessLogFiles")
	public void testLoggingVariables() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(ASIntegrationLoggingUtil.searchLogRecord(
					           "HTTP/1.1 GET /access-logs-test-webapp/services/test_access_log/simpleget?name=abc&domain=" +
					           "wso2.com HTTP/1.1 ?name=abc&domain=wso2.com 127.0.0.1 200", variablesLogs),
			           "Variables are not properly logged for super tenant webapp");
		} else {

			assertTrue(ASIntegrationLoggingUtil.searchLogRecord("HTTP/1.1 GET /t/" + tenantDomain +
			                                                    "/webapps/access-logs-test-webapp/services/" +
			                                                    "test_access_log/simpleget?name=abc&domain=wso2.com HTTP/" +
			                                                    "1.1 ?name=abc&domain=wso2.com 127.0.0.1 200",
			                                                    variablesLogs),
			           "Variables are not properly logged for tenant webapp");
		}
	}

	@AfterClass(alwaysRun = true)
	public void restoreServer() throws Exception {
		sessionCookie = loginLogoutClient.login();
		webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
		webAppAdminClient
				.deleteWebAppFile(WEB_APP_NAME + ".war", asServer.getDefaultInstance().getHosts().get("default"));
		if (isServerRestarted) {
			serverConfigurationManager.restoreToLastConfiguration();
			isServerRestarted = false;
		}
	}

	private Writer makePostRequest(String endpoint) throws AutomationFrameworkException, MalformedURLException {
		Writer writer = new StringWriter();
		URL url = new URL(endpoint);
		Reader data = new StringReader("abc");

		HttpURLConnectionClient.sendPostRequest(data, url, writer, "text/plain");
		return writer;
	}
}
