/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.appserver.integration.tests.webapp.spring;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class SpringScopeTestCase extends ASIntegrationTest {

    private WebAppMode webAppMode;
    private WebAppAdminClient webAppAdminClient;

    @Factory(dataProvider = "webAppModeProvider")
    public SpringScopeTestCase(WebAppMode webAppMode) {
        this.webAppMode = webAppMode;
    }

    @DataProvider
    private static WebAppMode[][] webAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] { new WebAppMode("spring3-restful-simple-service", TestUserMode.SUPER_TENANT_ADMIN) },
                new WebAppMode[] { new WebAppMode("spring3-restful-simple-service", TestUserMode.TENANT_USER) },
                new WebAppMode[] { new WebAppMode("spring4-restful-simple-service", TestUserMode.SUPER_TENANT_ADMIN) },
                new WebAppMode[] { new WebAppMode("spring4-restful-simple-service", TestUserMode.TENANT_USER) },
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(webAppMode.getUserMode());
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + webAppMode.getWebAppName() + ".war");
        assertTrue(
                WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()));
    }

    @Test(groups = "wso2.as", description = "Verfiy Spring Request scope")
    public void testSpringRequestScope() throws Exception {

        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + "/scope/request";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        HttpGet httpget = new HttpGet(endpoint);
        HttpResponse response1 = httpClient.execute(httpget, httpContext);
        String responseMsg1 = new BasicResponseHandler().handleResponse(response1);
        HttpResponse response2 = httpClient.execute(httpget, httpContext);
        String responseMsg2 = new BasicResponseHandler().handleResponse(response2);
        httpClient.close();

        assertTrue(!responseMsg1.equalsIgnoreCase(responseMsg2), "Failed: Responses should not be the same");
    }

    @Test(groups = "wso2.as", description = "Verfiy Spring Session scope")
    public void testSpringSessionScope() throws Exception {
        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + "/scope/session";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        HttpGet httpget = new HttpGet(endpoint);
        HttpResponse response1 = httpClient.execute(httpget, httpContext);
        String responseMsg1 = new BasicResponseHandler().handleResponse(response1);
        HttpResponse response2 = httpClient.execute(httpget, httpContext);
        String responseMsg2 = new BasicResponseHandler().handleResponse(response2);
        httpClient.close();

        assertTrue(responseMsg1.equalsIgnoreCase(responseMsg2), "Failed: Responses should be the same");

    }

    @AfterClass(alwaysRun = true)
    public void deteleteWebApp() throws Exception {
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(webAppMode.getWebAppName() + ".war",
                                           asServer.getInstance().getHosts().get("default"));
    }


}
