/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.appserver.integration.tests.webapp.spring;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class SpringJNDITestCase extends ASIntegrationTest {

    private WebAppMode webAppMode;
    private WebAppAdminClient webAppAdminClient;
    private SqlDataSourceUtil sqlDataSource;
    private final String endpointURL = "/student";
    private final String contentType = "application/json";

    @Factory(dataProvider = "webAppModeProvider")
    public SpringJNDITestCase(WebAppMode webAppMode) {
        this.webAppMode = webAppMode;
    }

    @DataProvider
    private static WebAppMode[][] webAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] {new WebAppMode("spring3-restful-jndi-service", TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode("spring3-restful-jndi-service", TestUserMode.TENANT_USER)},
                new WebAppMode[] {new WebAppMode("spring4-restful-jndi-service", TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode("spring4-restful-jndi-service", TestUserMode.TENANT_USER)},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(webAppMode.getUserMode());
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        createTable();
        createDataSource(webAppMode.getWebAppName(), sqlDataSource);
    }

    @Test(groups = "wso2.as", description = "Upload Spring 3 WAR and verify deployment")
    public void testSpringWARUpload() throws Exception {
        String springWarFilePath = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                                   webAppMode.getWebAppName() + ".war";
        webAppAdminClient.uploadWarFile(springWarFilePath);
        assertTrue(
                WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()));
    }

    @Test(groups = "wso2.as", description = "Verify Get Operation", dependsOnMethods = "testSpringWARUpload")
    public void testGetOperation() throws Exception {
        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        try {
            JSONArray jsonArray = new JSONArray(response.getData());
            assertTrue(jsonArray.length() > 0);
        } catch (JSONException e) {
            assertTrue(false);
        }
    }

    @Test(groups = "wso2.as", description = "Verify Put Operation", dependsOnMethods = "testGetOperation")
    public void testPutOperation() throws Exception {
        URL endpoint = new URL(webAppURL + "/" + webAppMode.getWebAppName() + endpointURL);
        String getEndpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL;
        Reader data = new StringReader("{\"id\": 3,\"firstName\": \"Jack\", \"lastName\":\"Peter\", \"age\":30}");
        Writer writer = new StringWriter();
        HttpResponse response;
        JSONArray jsonArray;

        try {
            response = HttpRequestUtil.sendGetRequest(getEndpoint, null);
            jsonArray = new JSONArray(response.getData());
            int initialSize = jsonArray.length();
            HttpRequestUtil.sendPutRequest(data, endpoint, writer, contentType);
            response = HttpRequestUtil.sendGetRequest(getEndpoint, null);
            jsonArray = new JSONArray(response.getData());
            assertTrue(jsonArray.length() == (initialSize + 1));
        } catch (JSONException e) {
            assertTrue(false);
        }
    }

    @Test(groups = "wso2.as", description = "Verify Update Operation", dependsOnMethods = "testPutOperation")
    public void testUpdateOperation() throws Exception {
        URL endpoint = new URL(webAppURL + "/" + webAppMode.getWebAppName() + endpointURL);
        String expectedData = "{\"id\": 3,\"firstName\": \"Jack\", \"lastName\":\"Peter\", \"age\":16}";
        String getEndpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL + "/3";
        Reader data = new StringReader(expectedData);
        Writer writer = new StringWriter();
        HttpResponse response;

        try {
            HttpRequestUtil.sendPostRequest(data, endpoint, writer, contentType);
            response = HttpRequestUtil.sendGetRequest(getEndpoint, null);
            JSONAssert.assertEquals(expectedData, new JSONObject(response.getData()), false);
        } catch (JSONException e) {
            assertTrue(false);
        }
    }

    @Test(groups = "wso2.as", description = "Verify Update Operation", dependsOnMethods = "testUpdateOperation")
    public void testDeleteOperation() throws Exception {
        URL endpoint = new URL(webAppURL + "/" + webAppMode.getWebAppName() + endpointURL + "/3");
        String getEndpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL;
        HttpResponse response;
        JSONArray jsonArray;

        try {
            response = HttpRequestUtil.sendGetRequest(getEndpoint, null);
            jsonArray = new JSONArray(response.getData());
            int initialSize = jsonArray.length();
            sendDeleteRequest(endpoint, contentType);
            response = HttpRequestUtil.sendGetRequest(getEndpoint, null);
            jsonArray = new JSONArray(response.getData());
            assertTrue(jsonArray.length() == (initialSize - 1));
        } catch (JSONException e) {
            assertTrue(false);
        }
    }

    @Test(groups = "wso2.as", description = "Verfiy if the webapp is unpacked for Tenants",
            dependsOnMethods = "testDeleteOperation")
    public void testTenantWebappUnpack() throws Exception {
        if (webAppMode.getUserMode().equals(TestUserMode.TENANT_USER)) {
            assertTrue(WebAppDeploymentUtil
                               .isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()),
                       "Web Application Deployment failed");
        }
    }

    @AfterClass(alwaysRun = true)
    public void deteleteWebApp() throws Exception {
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(webAppMode.getWebAppName() + ".war",
                                           asServer.getInstance().getHosts().get("default"));
    }

    private void createTable() throws Exception {
        sqlDataSource = new SqlDataSourceUtil(sessionCookie,asServer.getContextUrls().getBackEndUrl());
        File sqlFile = new File(TestConfigurationProvider.getResourceLocation() + "artifacts" + File.separator + "AS" +
                                File.separator + "spring" + File.separator + "studentDb.sql");
        List<File> sqlFileList = new ArrayList<>();
        sqlFileList.add(sqlFile);
        sqlDataSource.createDataSource(sqlFileList, "dataService");
    }

    private static int sendDeleteRequest(URL endpoint, String contentType) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;
        int responseCode;
        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("DELETE");
            } catch (ProtocolException var33) {
                throw new AutomationFrameworkException(
                        "Shouldn\'t happen: HttpURLConnection doesn\'t support DELETE?? " + var33.getMessage(), var33);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-type", contentType);
            responseCode = urlConnection.getResponseCode();
        } catch (IOException var36) {
            throw new AutomationFrameworkException(
                    "Connection error (is server running at " + endpoint + " ?): " + var36.getMessage(), var36);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return responseCode;
    }
}