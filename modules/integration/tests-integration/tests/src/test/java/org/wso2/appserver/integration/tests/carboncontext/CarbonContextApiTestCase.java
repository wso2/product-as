package org.wso2.appserver.integration.tests.carboncontext;
/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

import static org.testng.Assert.*;

/**
 * This test class is use to test the basic functionality of CarbonContext
 */
public class CarbonContextApiTestCase extends ASIntegrationTest {
	private static final String APP_NAME = "carbon-context";
	private static final String WEBAPP_FILENAME = "carbon-context.war";

	private TestUserMode userMode;
	private HttpClient httpClient = new HttpClient();
	private ServerConfigurationManager serverManager;
	private WebAppAdminClient webAppAdminClient;

	@Factory(dataProvider = "userModeDataProvider")
	public CarbonContextApiTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@DataProvider
	private static TestUserMode[][] userModeDataProvider() {
		return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_USER } };
	}

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init(userMode);

		//restart server with changed carboncontext-osgi-services.properties file in SUPER_ADMIN mode
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			File sourceFile = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
			                           "artifacts" + File.separator + "AS" + File.separator + "carboncontext" +
			                           File.separator + "carboncontext-osgi-services.properties");
			File targetFile = new File(
					System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
					"conf" +
					File.separator + "etc" + File.separator + "carboncontext-osgi-services.properties");
			serverManager = new ServerConfigurationManager(asServer);
			serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
			serverManager.restartForcefully();

			super.init(userMode);
		}

		webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
		String path = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "war" + File.separator + WEBAPP_FILENAME;
		webAppAdminClient.uploadWarFile(path);
		assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, APP_NAME),
		           "Web Application Deployment failed");
	}

	@Test(groups = "wso2.as", description = "Set tenant ID without resolving domain and check if domain is null")
	public void testSetTenantIdWithoutResolveDomain() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=resolveTenantDomain" +
		             "&tenantId=1";

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertNull(getMethod.getResponseHeader("resolved-tenantDomain"), "Tenant domain has resolved");
	}

	@Test(groups = "wso2.as", description = "Set tenant ID with resolving domain and check if domain is correctly resolved")
	public void testSetTenantIdWithResolveDomain() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=resolveTenantDomain" +
		             "&tenantId=1" +
		             "&setWithResolve=true";

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("resolved-tenantDomain").getValue(), "wso2.com",
		             "Tenant resolve to wrong tenant");
	}

	@Test(groups = "wso2.as", description = "Set tenant ID without resolving domain and get domain with resolve true")
	public void testSetTenantIdAndGetDomainWithResolve() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=resolveTenantDomain" +
		             "&tenantId=1" +
		             "&getWithResolve=true";

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("resolved-tenantDomain").getValue(), "wso2.com",
		             "Tenant resolve to wrong tenant");
	}

	@Test(groups = "wso2.as", description = "Set tenant domain without resolving id and check if id is -1")
	public void testSetTenantDomainWithoutResolveId() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=resolveTenantId" +
		             "&tenantDomain=wso2.com";

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("resolved-tenantId").getValue(), "-1", "Tenant Id resolving error");
	}

	@Test(groups = "wso2.as", description = "Set tenant domain with resolving id and check if id is 1")
	public void testSetTenantDomainWithResolveId() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=resolveTenantId" +
		             "&tenantDomain=wso2.com" +
		             "&setWithResolve=true";
		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("resolved-tenantId").getValue(), "1", "Tenant Id resolving error");
	}

	@Test(groups = "wso2.as", description = "Set tenant domain without resolving id and get id with resolve true")
	public void testSetTenantDomainAndGetIdWithResolve() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=resolveTenantId" +
		             "&tenantDomain=wso2.com" +
		             "&getWithResolve=true";

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("resolved-tenantId").getValue(), "1", "Tenant Id resolving error");
	}

	@Test(groups = "wso2.as", description = "get OSGI service reference through the carboncontext")
	public void testGetOSGiService() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/OSGiServiceServlet?action=cc";
		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("tomcat-service-name").getValue(), "Catalina",
		             "OSGi service retrieval failed");
	}

	@Test(groups = "wso2.as", description = "get OSGI service reference through the PrivilegeCarbonContext")
	public void testGetOSGiServiceFromPrivilegeCarbonCOntext() throws Exception {
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/OSGiServiceServlet?action=pcc";
		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("tomcat-service-name").getValue(), "Catalina",
		             "OSGi service retrieval failed");
	}

	@Test(groups = "wso2.as", description = "set application name through the carboncontext")
	public void testSetApplicationName() throws Exception {
		String appName = userInfo.getKey() + "CCTxTestApp";
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=setAppName" +
		             "&appName=" + appName;

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("set-app-name").getValue(), appName, "Retrieved invalid app name");
	}

	@Test(groups = "wso2.as", description = "set username through the carboncontext")
	public void testSetUsername() throws Exception {
		String userName = "testuser";
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=setUsername" +
		             "&username=" + userName;

		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("set-username").getValue(), userName, "Retrieved invalid username");
	}

	@Test(groups = "wso2.as", description = "Get registry through the CarbonContext")
	public void testGetRegistry() throws Exception {
		String regKey = userInfo.getKey() + "regKey";
		String regValue = userInfo.getKey() + "regValue";
		String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/TenantServlet?" +
		             "action=getRegistry" +
		             "&regKey=" + regKey +
		             "&regValue=" + regValue;
		GetMethod getMethod = new GetMethod(url);
		assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
		             "Method failed: " + getMethod.getStatusLine());
		assertEquals(getMethod.getResponseHeader("retrieved-registry-value").getValue(), regValue,
		             "Retrieved invalid registry value");
	}

	@AfterClass(alwaysRun = true)
	public void testDeleteWebApplication() throws Exception {
		webAppAdminClient.deleteWebAppFile(WEBAPP_FILENAME, asServer.getDefaultInstance().getHosts().get("default"));
		//reverting the changes
		if (serverManager != null && userMode == TestUserMode.TENANT_USER) {
			serverManager.restoreToLastConfiguration();
		}
	}
}
