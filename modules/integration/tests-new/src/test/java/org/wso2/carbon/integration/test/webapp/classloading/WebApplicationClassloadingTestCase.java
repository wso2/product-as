/*
 *Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.integration.test.webapp.classloading;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.automation.utils.httpclient.HttpClientUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public abstract class WebApplicationClassloadingTestCase extends
		ASIntegrationTest {

	public static final String PASS = "Pass";
	public static final String FAIL = "Fail";

	private String webAppFileName;
	private String webAppName;
	private String webAppURL;
	protected WebAppAdminClient webAppAdminClient;
	

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init(ProductConstant.ADMIN_USER_ID);
		webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(),
				asServer.getSessionCookie());

	}

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName);
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                webAppName), "Web Application unDeployment failed");

    }

	@Test(groups = "wso2.as", description = "Deploying web application")
	public void webApplicationDeploymentTest() throws Exception {
		webAppAdminClient
				.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
						+ "artifacts" + File.separator + "AS" + File.separator
						+ "war" + File.separator + webAppFileName);

		assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
				asServer.getBackEndUrl(), asServer.getSessionCookie(),
				webAppName), "Web Application Deployment failed");
	}
	
	@Test(groups = "wso2.as", description = "Invoke web application", dependsOnMethods = "webApplicationDeploymentTest")
	public void testInvokeWebApp() throws Exception {	
		Map<String, String> results = toResultMap(runAndGetResultAsString(webAppURL));
		assertEquals(PASS, results.get("Tomcat"));
		assertEquals(PASS, results.get("Carbon"));
		assertEquals(PASS, results.get("CXF"));
		assertEquals(PASS, results.get("Spring"));
	}

	protected OMElement runAndGetResultAsOMElement(String webAppURL)
			throws Exception {
		HttpClientUtil client = new HttpClientUtil();
		return client.get(webAppURL);
	}

	protected String runAndGetResultAsString(String webAppURL) throws Exception {
		HttpClientUtil client = new HttpClientUtil();
		return client.get(webAppURL).toString();
	}

	protected Map<String, String> toResultMap(String resultString)
			throws Exception {
		if (resultString == null) {
			System.out.println("resultString is null");
			return null;
		}
		resultString = resultString.replace("<status>", "").replace(
				"</status>", "");
		Map<String, String> resultMap = new HashMap<String, String>();
		String[] resultArray = resultString.split(",");
		for (String s : resultArray) {
			String[] temp = s.split("-");
			if (temp != null && !temp.equals("")) {
				resultMap.put(temp[0], temp[1]);
			}
		}
		System.out.println(resultMap);
		return resultMap;
	}

	public String getWebAppFileName() {
		return webAppFileName;
	}

	public String getWebAppName() {
		return webAppName;
	}
	
	public void setWebAppFileName(String webAppFileName) {
		this.webAppFileName = webAppFileName;
	}

	public void setWebAppName(String webAppName) {
		this.webAppName = webAppName;
	}
	public String getWebAppURL() {
		return webAppURL;
	}

	public void setWebAppURL(String webAppURL) {
		this.webAppURL = webAppURL;
	}

}
