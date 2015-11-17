package org.wso2.appserver.integration.tests.cahce;
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
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class is use to test the functionality of retrieving cached values.
 */
public class CacheRetrievalTestCase extends ASIntegrationTest {
    private static final String WEBAPP_FILENAME = "carbon-cache.war";
    private static final String APP_NAME = "carbon-cache";

    private TestUserMode userMode;
    private HttpClient httpClient = new HttpClient();
    private String cacheValue;
    private String cacheKey;

    @Factory(dataProvider = "userModeDataProvider")
    public CacheRetrievalTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        Path path = Paths.get(System.getProperty("basedir", "."), "target", "resources", "artifacts", "AS", "war",
                              WEBAPP_FILENAME);
        webAppAdminClient.uploadWarFile(path.toString());
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, APP_NAME),
                   "Web Application Deployment failed");

        //Deploy example webapp in tenant mode since we need it to access cache
        if (userMode == TestUserMode.TENANT_USER) {
            path = Paths.get(System.getProperty("basedir", "."), "target", "resources", "artifacts", "AS", "war",
                             "example.war");
            webAppAdminClient.uploadWarFile(path.toString());
            assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, "example"),
                       "Web Application Deployment failed");
        }
        cacheKey = userInfo.getKey() + "cacheKey";
        cacheValue = userInfo.getKey() + "cacheValue";
    }

    @DataProvider
    private static TestUserMode[][] userModeDataProvider() {
        return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_USER } };
    }

    @Test(groups = "wso2.as", description = "Check if cache hit or miss - Negative case")
    public void testCacheMiss() throws Exception {
        String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/CacheRetriever?" +
                     "action=getCache" +
                     "&key=" + cacheKey;

        GetMethod getMethod = new GetMethod(url);
        assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
                     "Method failed: " + getMethod.getStatusLine());
        assertEquals(getMethod.getResponseHeader("cached-value").getValue(), "tempCacheValue",
                     "Cache hit. But should be missed");
    }

    @Test(groups = "wso2.as", description = "Try to access cache from a separate app", dependsOnMethods = {
            "testCacheMiss" })
    public void testAccessCacheFromSeparateApp() throws Exception {
        String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/CacheRetriever?" +
                     "action=setCache" +
                     "&key=" + cacheKey +
                     "&value=" + cacheValue;

        GetMethod getMethod = new GetMethod(url);
        assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
                     "Method failed: " + getMethod.getStatusLine());
        assertEquals(getMethod.getResponseHeader("added-cached-value").getValue(), cacheValue,
                     "Retrieved cache value doesn't match");

        //Try to retrieve the cached value from a separate webapp
        url = getWebAppURL(WebAppTypes.WEBAPPS) + "/example/carbon/caching/index.jsp?view=View&key=" + cacheKey;
        getMethod = new GetMethod(url);
        assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
                     "Method failed: " + getMethod.getStatusLine());
        String cachedValue = getMethod.getResponseHeader("cache-value").getValue();
        assertEquals(cachedValue, cachedValue, "Retrieved cache value is invalid");
    }

    @Test(groups = "wso2.as", description = "Check if cache hit or miss - positive case", dependsOnMethods = {
            "testAccessCacheFromSeparateApp" })
    public void testCacheHit() throws Exception {
        String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/CacheRetriever?" +
                     "action=getCache" +
                     "&key=" + cacheKey;

        GetMethod getMethod = new GetMethod(url);
        assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
                     "Method failed: " + getMethod.getStatusLine());
        assertEquals(getMethod.getResponseHeader("cached-value").getValue(), cacheValue,
                     "Cache miss. But should be hit");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(WEBAPP_FILENAME, asServer.getDefaultInstance().getHosts().get("default"));
        if (userMode == TestUserMode.TENANT_USER) {
            webAppAdminClient.deleteWebAppFile("example.war", asServer.getDefaultInstance().getHosts().get("default"));
        }
    }
}
