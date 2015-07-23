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

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class is using to test the SaaSTest app
 */
public class CarbonSasSAppTestCase extends ASIntegrationTest {

	private TestUserMode userMode;

	private String username;
	private String password;
	private static final String APP_NAME = "CarbonSaasApp";
	private static final String WEBAPP_FILENAME = "CarbonSaasApp.war";
	private static final String RESOURCE_PREFIX = "_system/local/SaaSTest";
	private String resourceName;
	private String resourceType;
	private String resourceDescription;
	private String resourceContent;
	private String resourceLookupKey;
	private String resourceLookupValue;
	private String cacheKey;
	private String cacheValue;
	private String tempWebAppURLPrefix;

	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private HttpClientContext context = HttpClientContext.create();

	@Factory(dataProvider = "userModeDataProvider")
	public CarbonSasSAppTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init(userMode);
		initializeDefaultValues();
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
			webAppAdminClient.uploadWarFile(
					ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "war" + File.separator + WEBAPP_FILENAME);
			assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, APP_NAME),
			           "Web Application Deployment failed");
			tempWebAppURLPrefix = webAppURL;
		} else {
			String host = asServer.getInstance().getHosts().get("default");
			String port = asServer.getInstance().getPorts().get("http");
			tempWebAppURLPrefix = "http://" + host + ":" + port;
		}
	}

	@DataProvider
	private static TestUserMode[][] userModeDataProvider() {
		return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_USER } };
	}

	@Test(groups = "wso2.as", description = "Access registry through CarbonContext API")
	public void testRegistryOperationsInSaaSTestApp() throws Exception {
		//Set Tenant Specific Registry Values
		String url = tempWebAppURLPrefix + "/" + APP_NAME + "/context/registry.jsp?" +
		             "action=add" +
		             "&registryType=" + RegistryType.LOCAL_REPOSITORY.toString() +
		             "&regResourceName=" + resourceName +
		             "&regMediaType=" + resourceType +
		             "&regDesc=" + resourceDescription +
		             "&regContent=" + resourceContent +
		             "&propertyKey=" + resourceLookupKey +
		             "&propertyValue=" + resourceLookupValue;

		HttpGet preHttpGet = new HttpGet(tempWebAppURLPrefix + "/" + APP_NAME + "/index.jsp");
		HttpGet httpGet = new HttpGet(url);
		HttpPost httpPost = new HttpPost(tempWebAppURLPrefix + "/" + APP_NAME + "/j_security_check");

		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("j_username", username));
		nvps.add(new BasicNameValuePair("j_password", password));

		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps);
		httpPost.setEntity(urlEncodedFormEntity);

		try {
			executeAndConsumeRequest(preHttpGet);
			executeAndConsumeRequest(httpPost);
			executeAndConsumeRequest(preHttpGet);
			executeAndConsumeRequest(httpGet);

			//View Tenant Specific Values from Registry
			ResourceAdminServiceClient resourceAdminServiceClient =
					new ResourceAdminServiceClient(backendURL, sessionCookie);
			ResourceData[] resourceData = resourceAdminServiceClient.getResource(RESOURCE_PREFIX + resourceName);
			assertEquals(resourceData.length, 1, "Retrieved more than one resource from given path");
			assertEquals(resourceData[0].getDescription(), resourceDescription, "Retrieved resource is invalid");
		} finally {
			httpGet.releaseConnection();
			httpPost.releaseConnection();
			preHttpGet.releaseConnection();
		}
	}

	@Test(groups = "wso2.as", description = "Access cache through the SaaS app")
	public void testCacheOperationsInSaaSTestApp() throws Exception {
		String url = tempWebAppURLPrefix + "/" + APP_NAME + "/context/cache.jsp?" +
		             "action=add" +
		             "&cachekey=" + cacheKey +
		             "&cachevalue=" + cacheValue;

		HttpGet preHttpGet = new HttpGet(tempWebAppURLPrefix + "/" + APP_NAME + "/index.jsp");
		HttpGet httpGet = new HttpGet(url);
		HttpPost httpPost = new HttpPost(tempWebAppURLPrefix + "/" + APP_NAME + "/j_security_check");

		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("j_username", username));
		nvps.add(new BasicNameValuePair("j_password", password));

		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps);
		httpPost.setEntity(urlEncodedFormEntity);

		try {
			executeAndConsumeRequest(preHttpGet);
			executeAndConsumeRequest(httpPost);
			executeAndConsumeRequest(preHttpGet);
			executeAndConsumeRequest(httpGet);

			//View Tenant Specific Cache Values
			url = tempWebAppURLPrefix + "/" + APP_NAME + "/context/cache.jsp?" +
			      "action=view" +
			      "&cachekey=" + cacheKey;
			CloseableHttpResponse closeableHttpResponse = httpClient.execute(new HttpGet(url), context);
			Header[] headers = closeableHttpResponse.getHeaders("cache-value");
			assertEquals(headers.length, 1, "Retrieved more than one value for given path");
			assertEquals(headers[0].getValue(), cacheValue, "Retrieved cache value is invalid");
		} finally {
			httpGet.releaseConnection();
			httpPost.releaseConnection();
			preHttpGet.releaseConnection();
		}
	}

	/**
	 * This will execute the HttpGet/HttpPost and consume the response
	 *
	 * @param httpRequestBase HttpGet or HttpPost request
	 * @throws IOException
	 */
	private void executeAndConsumeRequest(HttpRequestBase httpRequestBase) throws IOException {
		CloseableHttpResponse httpResponse = httpClient.execute(httpRequestBase, context);
		EntityUtils.consume(httpResponse.getEntity());
	}

	private void initializeDefaultValues() {
		username = userInfo.getUserName();
		password = userInfo.getPassword();

		switch (userMode) {
			case SUPER_TENANT_ADMIN:
				resourceName = "/resourcePathSTA";
				resourceDescription = "Path-to-resource-in-STA";
				resourceType = "plain/text";
				resourceContent = "Test-Content-for-STA";
				resourceLookupKey = "STA_Lookup_Key";
				resourceLookupValue = "STA_Lookup_Value";
				cacheKey = "STA_cache_key";
				cacheValue = "STA_cache_value";
				break;
			case TENANT_USER:
				resourceName = "/resourcePathTA";
				resourceDescription = "Path-to-resource-in-TA";
				resourceType = "plain/text";
				resourceContent = "Test-Content-for-TA";
				resourceLookupKey = "TA_Lookup_Key";
				resourceLookupValue = "TA_Lookup_Value";
				cacheKey = "TA_cache_key";
				cacheValue = "TA_cache_value";
				break;
		}
	}

	/*@AfterClass(alwaysRun = true)
	private void testDeleteWebApplication() throws Exception {
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
			webAppAdminClient
					.deleteWebAppFile(WEBAPP_FILENAME, asServer.getDefaultInstance().getHosts().get("default"));
		}
	}*/
}