/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.tests.jmx;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.jmsserver.JMSBrokerController;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class will test the issue of carbon selector context is read only returned from CarbonJavaURLContextFactory
 */
public class CARBON15274CarbonLookupContextReadOnlyExceptionTestCase extends ASIntegrationTest {
	private static final Log log = LogFactory.getLog(CARBON15274CarbonLookupContextReadOnlyExceptionTestCase.class);

	private final String WEBAPP_NAME = "spring-jmx-test-agent";
	private final String WEBAPP_FILE_NAME = "spring-jmx-test-agent.war";
	private JMSBrokerController jmsBrokerController;
	private WebAppAdminClient webAppAdminClient;
	private ServerConfigurationManager serverManager = null;
	private static boolean isBrokerStarted;
	private final String ACTIVEMQ_BROKER = "activemq-broker-5.9.0.jar";
	private final String GERONIMO_J2EE_MANAGEMENT = "geronimo-spec-j2ee-management-1.0-rc4.jar";
	private final String GERONIMO_JMS = "geronimo-jms_1.1_spec-1.1.1.jar";
	private final String ACTIVEMQ_CLIENT = "activemq-client-5.9.0.jar";
	private TestUserMode userMode;
	private static int isServerRestarted = 0;

	@Factory(dataProvider = "userModeProvider")
	public CARBON15274CarbonLookupContextReadOnlyExceptionTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@DataProvider
	private static TestUserMode[][] userModeProvider() {
		return new TestUserMode[][]{
				new TestUserMode[]{ TestUserMode.SUPER_TENANT_ADMIN},
				new TestUserMode[]{TestUserMode.TENANT_USER}
		};
	}

	@BeforeClass(alwaysRun = true) public void init() throws Exception {
		super.init(userMode);

		webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
		String warFilePath =
				System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
				File.separator + "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator +
				WEBAPP_FILE_NAME;

		webAppAdminClient.uploadWarFile(warFilePath);
		assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEBAPP_NAME));

		serverManager = new ServerConfigurationManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN));

		//Copy Activemq jars and restart server
		if (isServerRestarted == 0) {
			startJMSBrokerAndConfigureAS();
		}
		++isServerRestarted;

	}

	@Test(groups = "wso2.as", description = "Send request to the webapp")
	public void testWebAppLookUpContext() throws Exception {
		String webAppURLLocal;
		if(userMode== TestUserMode.SUPER_TENANT_ADMIN){
			webAppURLLocal = webAppURL + "/" + WEBAPP_NAME;
		}else{
			webAppURLLocal = webAppURL+"/"+"webapps"+"/"+WEBAPP_NAME+"/";
		}
		HttpRequestUtil client = new HttpRequestUtil();
		HttpResponse response = client.sendGetRequest(webAppURLLocal, null);
		assertEquals(response.getResponseCode(), 200, "Webapp lookup context is read only");
	}

	@AfterClass(alwaysRun = true)
	public void deleteWebApplication() throws Exception {
		sessionCookie = loginLogoutClient.login();
		webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
		webAppAdminClient.deleteWebAppFile(WEBAPP_FILE_NAME, asServer.getInstance().getHosts().get("default"));
		stopJMSBrokerRevertASConfiguration();
		//Revert and restart only once
		--isServerRestarted;
		if (isServerRestarted==0) {
			serverManager.removeFromComponentLib(ACTIVEMQ_BROKER);
			serverManager.removeFromComponentLib(ACTIVEMQ_CLIENT);
			serverManager.removeFromComponentLib(GERONIMO_J2EE_MANAGEMENT);
			serverManager.removeFromComponentLib(GERONIMO_JMS);
			serverManager.restoreToLastConfiguration();
		}

	}

	public void startJMSBrokerAndConfigureAS() throws Exception {

		BrokerService broker = new BrokerService();
		broker.setUseJmx(true);
		broker.getManagementContext().setConnectorPort(1199);

		jmsBrokerController = new JMSBrokerController("localhost");

		if (!isBrokerStarted) {
			assertTrue(jmsBrokerController.start(broker), "JMS Broker(ActiveMQ) stating failed");
			isBrokerStarted = true;
		}

		serverManager.copyToComponentLib(
				new File(System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
				         File.separator + "artifacts" + File.separator + "AS" + File.separator + "jms" +
				         File.separator +
				         "activemq" + File.separator + "runtime" + File.separator + ACTIVEMQ_BROKER));

		serverManager.copyToComponentLib(
				new File(System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
				         File.separator + "artifacts" + File.separator + "AS" + File.separator + "jms" +
				         File.separator +
				         "activemq" + File.separator + "runtime" + File.separator + ACTIVEMQ_CLIENT));

		serverManager.copyToComponentLib(
				new File(System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
				         File.separator + "artifacts" + File.separator + "AS" + File.separator + "jms" +
				         File.separator +
				         "activemq" + File.separator + "runtime" + File.separator + GERONIMO_J2EE_MANAGEMENT));

		serverManager.copyToComponentLib(
				new File(System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
				         File.separator + "artifacts" + File.separator + "AS" + File.separator + "jms" +
				         File.separator +
				         "activemq" + File.separator + "runtime" + File.separator + GERONIMO_JMS));

		File sourceFile = new File(
				FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "AS" + File.separator +
				"jmx" + File.separator + "carbon.xml");

		File targetFile = new File(
				System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator
				+ "conf" + File.separator + "carbon.xml");

		serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);

		serverManager.restartGracefully();
	}

	public void stopJMSBrokerRevertASConfiguration() throws Exception {
		try {
			//reverting the changes done to as sever
			Thread.sleep(10000); //let server to clear the artifact undeployment
			if (serverManager != null) {
				serverManager.removeFromComponentLib(ACTIVEMQ_BROKER);
				serverManager.removeFromComponentLib(GERONIMO_J2EE_MANAGEMENT);
				serverManager.removeFromComponentLib(GERONIMO_JMS);
				serverManager.removeFromComponentLib(ACTIVEMQ_CLIENT);
				serverManager.restartGracefully();
			}

		} finally {
			if (jmsBrokerController != null) {
				isBrokerStarted = false;
				assertTrue(jmsBrokerController.stop(), "JMS Broker(ActiveMQ) Stopping failed");
			}
		}
	}
}
