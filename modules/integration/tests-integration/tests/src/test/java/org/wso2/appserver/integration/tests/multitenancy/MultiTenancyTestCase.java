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
package org.wso2.appserver.integration.tests.multitenancy;

import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.AuthenticateStubUtil;
import org.wso2.appserver.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;
import org.wso2.carbon.security.mgt.stub.keystore.GetKeyStoresResponse;
import org.wso2.carbon.security.mgt.stub.keystore.KeyStoreAdminServiceStub;
import org.wso2.carbon.security.mgt.stub.keystore.xsd.KeyStoreData;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class contains test cases to test the different use cases of multi-tenancy
 */
public class MultiTenancyTestCase extends ASIntegrationTest {

	private final String FIRST_TENANT_DOMAIN = "multitenancytest1.com";
	private final String FIRST_TENANT_USER = "testuser1";
	private final String SECOND_TENANT_DOMAIN = "multitenancytest2.com";
	private final String SECOND_TENANT_USER = "testuser2";
	private final String THIRD_TENANT_DOMAIN = "multitenancytest3.com";
	private final String THIRD_TENANT_USER = "testuser3";
	private WebAppAdminClient webAppAdminClient;
	private TenantManagementServiceClient tenantManagementServiceClient;
	private TenantInfoBean tenantInfoBean;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init();
		tenantManagementServiceClient = new TenantManagementServiceClient(backendURL, loginLogoutClient.login());
	}

	@Test(groups = "wso2.as", description = "Test the information after adding a new tenant")
	public void testAddTenant() throws Exception {

		tenantManagementServiceClient = new TenantManagementServiceClient(backendURL, loginLogoutClient.login());
		tenantManagementServiceClient.addTenant(FIRST_TENANT_DOMAIN, "admin123", FIRST_TENANT_USER, "Demo");

		tenantInfoBean = tenantManagementServiceClient.getTenant(FIRST_TENANT_DOMAIN);
		assertNotNull(tenantInfoBean, "Tenant was not added properly");
		assertTrue(tenantInfoBean.getActive(), "The added tenant is not active");
		assertEquals(tenantInfoBean.getFirstname(), FIRST_TENANT_USER,
		             "Tenant first name is not matching with the added one");
		assertEquals(tenantInfoBean.getUsagePlan(), "", "Tenant usage plan is not matching with the one that added");

	}

	@Test(groups = "wso2.as", description = "Test logging to the added tenant and test tenant keystore",
			dependsOnMethods = "testAddTenant")
	public void testTenantLogin() throws Exception {
		sessionCookie = loginLogoutClient.login(FIRST_TENANT_USER + "@" + FIRST_TENANT_DOMAIN, "admin123",
		                                        asServer.getInstance().getHosts().get("default"));
		assertNotNull(sessionCookie, "Cannot login to the added tenant");

		String certificate = FIRST_TENANT_DOMAIN.replace('.', '-') + ".jks";

		ResourceAdminServiceClient resourceAdminServiceClient =
				new ResourceAdminServiceClient(backendURL, sessionCookie);
		ResourceData[] resourceData = resourceAdminServiceClient
				.getResource("/_system/governance/repository/security/key-stores/" + certificate);
		assertNotNull(resourceData, "Tenant key store is not created");

		//Verify tenant key store using stub
		String serviceName = "KeyStoreAdminService";
		String endPoint = backendURL + serviceName;

		KeyStoreAdminServiceStub keyStoreAdminServiceStub = new KeyStoreAdminServiceStub(endPoint);
		AuthenticateStubUtil.authenticateStub(sessionCookie, keyStoreAdminServiceStub);

		GetKeyStoresResponse getKeyStoresResponse = keyStoreAdminServiceStub.getKeyStores();
		KeyStoreData keyStoreData = getKeyStoresResponse.get_return()[0];

		assertNotNull(keyStoreData, "Tenant key store is null");
		assertEquals(keyStoreData.getKeyStoreName(), certificate, "Invalid tenant key store name");
	}

	@Test(groups = "wso2.as", description = "Test adding duplicate tenant",	dependsOnMethods = "testTenantLogin",
            expectedExceptions = { TenantMgtAdminServiceExceptionException.class },
			expectedExceptionsMessageRegExp = "TenantMgtAdminServiceExceptionException")
	public void testAddingDuplicateTenant() throws Exception {
		Date date = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		TenantInfoBean newTenantInfoBean = new TenantInfoBean();
		newTenantInfoBean.setActive(true);
		newTenantInfoBean.setEmail(FIRST_TENANT_USER + "@" + FIRST_TENANT_DOMAIN);
		newTenantInfoBean.setAdmin(FIRST_TENANT_USER);
		newTenantInfoBean.setAdminPassword("admin123");
		newTenantInfoBean.setUsagePlan("Demo");
		newTenantInfoBean.setLastname(FIRST_TENANT_USER + "wso2automation");
		newTenantInfoBean.setSuccessKey("true");
		newTenantInfoBean.setCreatedDate(calendar);
		newTenantInfoBean.setTenantDomain(FIRST_TENANT_DOMAIN);
		newTenantInfoBean.setFirstname(FIRST_TENANT_USER);

		tenantManagementServiceClient.addTenant(newTenantInfoBean);
	}

	@Test(groups = "wso2.as", description = "Deactivate loaded tenant and attempt to log",
			dependsOnMethods = "testAddingDuplicateTenant", expectedExceptions = {
			AutomationUtilException.class }, expectedExceptionsMessageRegExp = "Error while login as " +
			                                                                   FIRST_TENANT_USER + "@" +
			                                                                   FIRST_TENANT_DOMAIN)
	public void testTenantDeactivation() throws Exception {
		tenantManagementServiceClient.deactivateTenant(FIRST_TENANT_DOMAIN);
		loginLogoutClient.login(FIRST_TENANT_USER + "@" + FIRST_TENANT_DOMAIN, "admin123",
		                        asServer.getInstance().getHosts().get("default"));
	}

	@Test(enabled = true, groups = "wso2.as", description = "Test access a webapp deployed by a deactivated tenant " +
			"and re-activate that tenant and access web app",
			dependsOnMethods = "testTenantDeactivation")
	public void testAccessWebappOfDeactivatedTenant() throws Exception {
		tenantManagementServiceClient.activateTenant(FIRST_TENANT_DOMAIN);
		sessionCookie = loginLogoutClient.login(FIRST_TENANT_USER + "@" + FIRST_TENANT_DOMAIN, "admin123",
		                                        asServer.getInstance().getHosts().get("default"));
		webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
		webAppAdminClient.uploadWarFile(
				FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "AS" + File.separator +
						"war" + File.separator + "HelloWorldWebapp.war");
		assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, "HelloWorldWebapp"),
				"HelloWorldWebapp Deployment failed");
		tenantManagementServiceClient.deactivateTenant(FIRST_TENANT_DOMAIN);
		String webAppURL = getWebAppURL(WebAppTypes.WEBAPPS) +
		                   "/t/" + FIRST_TENANT_DOMAIN +
		                   "/webapps/HelloWorldWebapp/";
		HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
		
		//todo check the correct response status code once WSAS-2094 is fixed
		assertEquals(response.getData(), "",
		             "After de-activating a tenant tenant web app is accessible");

		tenantManagementServiceClient.activateTenant(FIRST_TENANT_DOMAIN);
		assertEquals(HttpRequestUtil.sendGetRequest(webAppURL, null).getResponseCode(), HttpStatus.SC_OK,
		             "After activating the tenant tenant web app is not accessible");

	}

	@Test(groups = "wso2.as", dependsOnMethods = "testTenantDeactivation",
            description = "Test Login after deactivating unloaded tenant", expectedExceptions = {
            AutomationUtilException.class }, expectedExceptionsMessageRegExp = "Error while login as " +
                                                                               SECOND_TENANT_USER + "@" +
                                                                               SECOND_TENANT_DOMAIN)
    public void testLoginToUnloadedDeactivatedTenant() throws Exception {
		tenantManagementServiceClient.addTenant(SECOND_TENANT_DOMAIN, "admin123", SECOND_TENANT_USER, "Demo");
		//Deactivate unloaded Tenant
		tenantManagementServiceClient.deactivateTenant(SECOND_TENANT_DOMAIN);

		loginLogoutClient.login(SECOND_TENANT_USER + "@" + SECOND_TENANT_DOMAIN, "admin123",
		                        asServer.getInstance().getHosts().get("default"));
	}

	@Test(groups = "wso2.as", description = "Test Login after activating unloaded tenant",
			dependsOnMethods = "testLoginToUnloadedDeactivatedTenant")
	public void testLoginToReActivatedTenant() throws Exception {

		tenantManagementServiceClient.activateTenant(SECOND_TENANT_DOMAIN);

		TenantInfoBean tenantInfoBean = tenantManagementServiceClient.getTenant(SECOND_TENANT_DOMAIN);
		sessionCookie = loginLogoutClient.login(tenantInfoBean.getFirstname() + "@" + SECOND_TENANT_DOMAIN, "admin123",
		                                        asServer.getInstance().getHosts().get("default"));
		assertNotNull(sessionCookie, "Cannot login to the tenant after activating");
	}

	@Test(groups = "wso2.as", description = "Test Login after deactivating unloaded tenant",
			dependsOnMethods = "testLoginToReActivatedTenant")
	public void testUpdateTenant() throws Exception {
		//Deactivate unloaded Tenant
		assertNotNull(loginLogoutClient.login(SECOND_TENANT_USER + "@" + SECOND_TENANT_DOMAIN, "admin123",
		                                      asServer.getInstance().getHosts().get("default")),
		              "Login failed to : " + SECOND_TENANT_DOMAIN);
		TenantInfoBean newTenantInfoBean = tenantManagementServiceClient.getTenant(SECOND_TENANT_DOMAIN);
		//Updating tenant info bean with new information
		newTenantInfoBean.setFirstname("updateduser_firstname");
		newTenantInfoBean.setLastname("updateduser_lastname");
		newTenantInfoBean.setEmail("updateduser_firstname@" + SECOND_TENANT_DOMAIN);
		tenantManagementServiceClient.updateTenant(newTenantInfoBean);

		TenantInfoBean updatedTenantInfo = tenantManagementServiceClient.getTenant(SECOND_TENANT_DOMAIN);
		assertEquals(updatedTenantInfo.getFirstname(), "updateduser_firstname", "Tenant update first name failed");
		assertEquals(updatedTenantInfo.getLastname(), "updateduser_lastname", "Tenant update last name failed");
		assertEquals(updatedTenantInfo.getEmail(), "updateduser_firstname@" + SECOND_TENANT_DOMAIN,
		             "Tenant update email failed");
	}

	@Test(groups = "wso2.as", dependsOnMethods = "testUpdateTenant",
            description = "Test adding new tenant from another tenant", expectedExceptions = { RemoteException.class })
    public void testAddingTenantFromDifferentTenant() throws Exception {
		tenantManagementServiceClient.addTenant(THIRD_TENANT_DOMAIN, "admin123", THIRD_TENANT_USER, "Demo");
		sessionCookie = loginLogoutClient.login(THIRD_TENANT_USER + "@" + THIRD_TENANT_DOMAIN, "admin123",
		                                        asServer.getInstance().getHosts().get("default"));
		assertNotNull(sessionCookie, "Cannot login to the added tenant : " + THIRD_TENANT_DOMAIN);

		//Try to add tenant after logging as multitenancytest3.com
		TenantManagementServiceClient newTenantManagementServiceClient =
				new TenantManagementServiceClient(backendURL, sessionCookie);
		newTenantManagementServiceClient
				.addTenant("multitenancytest4.com", "admin123", "testuser4.MultiTenancyTestCase", "Demo");

	}

	@AfterClass(alwaysRun = true)
	public void deleteTenants() throws Exception {
		//https://wso2.org/jira/browse/WSAS-2022

		//webAppAdminClient.deleteWebAppFile("HelloWorldWebapp.war", asServer.getInstance().getHosts().get("default"));
		//assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, "HelloWorldWebapp.war"),
		//"HelloWorldWebapp unDeployment failed");

		tenantManagementServiceClient.deactivateTenant(FIRST_TENANT_DOMAIN);
		tenantManagementServiceClient.deactivateTenant(SECOND_TENANT_DOMAIN);
		tenantManagementServiceClient.deactivateTenant(THIRD_TENANT_DOMAIN);

		assertFalse(tenantManagementServiceClient.getTenant(FIRST_TENANT_DOMAIN).getActive(),
		            "First tenant deactivation failed in multi-tenancy test case");
		assertFalse(tenantManagementServiceClient.getTenant(SECOND_TENANT_DOMAIN).getActive(),
		            "Second tenant deactivation failed in multi-tenancy test case");
		assertFalse(tenantManagementServiceClient.getTenant(THIRD_TENANT_DOMAIN).getActive(),
		            "Third tenant deactivation failed in multi-tenancy test case");
	}
}
