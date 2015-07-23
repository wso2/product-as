package org.wso2.appserver.integration.tests.registry;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import javax.xml.xpath.XPathExpressionException;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class will add/view/delete registry collection in super and multi tenant modes
 */
public class RegistryCollectionUsageTestCase extends ASIntegrationTest {

	private static final String COLLECTION_PATH = "path/to/test/collection";
	private static final String WEBAPP_FILENAME = "example.war";
	private static final String WEBAPP_NAME = "example";
	private static final Log log = LogFactory.getLog(RegistryResourceUsageTestCase.class);

	private TestUserMode userMode;
	private HttpClient httpClient = new HttpClient();
	private WebAppAdminClient webAppAdminClient;

	@Factory(dataProvider = "userModeDataProvider")
	public RegistryCollectionUsageTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init(userMode);
		if (userMode == TestUserMode.TENANT_USER) {
			webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
			String path = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "war" + File.separator + WEBAPP_FILENAME;
			webAppAdminClient.uploadWarFile(path);
			assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEBAPP_NAME),
			           "Web Application Deployment failed");
		}
	}

	@DataProvider
	private static TestUserMode[][] userModeDataProvider() {
		return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_USER } };
	}

	@Test(groups = "wso2.as", description = "Adding a collection")
	public void testRegistryCollectionAddUsage() throws Exception {
		log.info("Running registry collection add usage test case");
		String urlOne = getTenantSpecificUrl() + "?addCollection=Add&collectionPath=" + COLLECTION_PATH;
		GetMethod getMethodOne = new GetMethod(urlOne);

		try {
			log.info("Adding test collection to registry");
			assertEquals(httpClient.executeMethod(getMethodOne), HttpStatus.SC_OK,
			             "Method failed: " + getMethodOne.getStatusLine());
		} finally {
			getMethodOne.releaseConnection();
		}

		String urlTwo = getTenantSpecificUrl() + "?viewCollection=View&collectionPath=" + COLLECTION_PATH;
		GetMethod getMethodTwo = new GetMethod(urlTwo);
		try {
			log.info("Getting test resource content from registry");
			assertEquals(httpClient.executeMethod(getMethodTwo), HttpStatus.SC_OK,
			             "Method failed: " + getMethodTwo.getStatusLine());
			String resourceContent = getMethodTwo.getResponseHeader("collection-exist").getValue();
			assertEquals(resourceContent, "true", "Retrieved registry value is not what expected");
		} finally {
			getMethodTwo.releaseConnection();
		}
	}

	@Test(groups = {
			"wso2.as" }, description = "Deleting a registry path", dependsOnMethods = "testRegistryCollectionAddUsage")
	public void testRegistryCollectionDeleteUsage() throws Exception {
		log.info("Running registry collection delete usage test case");
		String urlOne = getTenantSpecificUrl() + "?delete=Delete&registryPath=" + COLLECTION_PATH;
		GetMethod getMethodOne = new GetMethod(urlOne);

		try {
			log.info("Deleting a registry path from registry");
			assertEquals(httpClient.executeMethod(getMethodOne), HttpStatus.SC_OK,
			             "Method failed: " + getMethodOne.getStatusLine());
			String isPathDeleted = getMethodOne.getResponseHeader("resource-deleted").getValue();
			assertEquals(isPathDeleted, "true", "Path deletion failed");
		} finally {
			getMethodOne.releaseConnection();
		}
	}

	public String getTenantSpecificUrl() throws XPathExpressionException {
		return getWebAppURL(WebAppTypes.WEBAPPS) + "/example/carbon/registry/index.jsp";
	}

	@AfterClass(alwaysRun = true)
	public void testDeleteWebApplication() throws Exception {
		if (userMode == TestUserMode.TENANT_USER) {
			webAppAdminClient
					.deleteWebAppFile(WEBAPP_FILENAME, asServer.getDefaultInstance().getHosts().get("default"));
		}
	}
}
