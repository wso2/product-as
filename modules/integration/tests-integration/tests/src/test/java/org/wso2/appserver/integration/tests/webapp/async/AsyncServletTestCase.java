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
 * KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.integration.tests.webapp.async;

import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;


import static org.testng.Assert.assertTrue;

public class AsyncServletTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(AsyncServletTestCase.class);

    private static final String webAppFileName = "example.war";
    private static final String webAppName = "example";
    private static final String webAppLocalURL = "/example";
    String hostname;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public AsyncServletTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);

        hostname = asServer.getInstance().getHosts().get("default");
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS) + webAppLocalURL;

        //using default example webapp hence no need to deploy or un-deploy for super tenant
        if (TestUserMode.TENANT_USER.equals(userMode)) {
            String webAppFilePath = FrameworkPathUtil.getCarbonHome() + File.separator + "repository" +
                    File.separator + "deployment" + File.separator + "server" + File.separator + "webapps" +
                    File.separator + webAppFileName;
            WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);
        }

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "test cdi alternatives with servlet")
    public void testAsyncServlet() throws Exception {

        String asyncContext0 = "/async/async0";
        String asyncContext1 = "/async/async1";
        String asyncContext2 = "/async/async2";
        String asyncContext3 = "/async/async3";
        String asyncContext4 = "/async/stockticker";

        testAsyncServletRequest(asyncContext0, "Async dispatch worked");
        testAsyncServletRequest(asyncContext1, "Output from async1.jspType is ASYNCCompleted async request");
        testAsyncServletRequest(asyncContext2, "Output from background thread");
        testAsyncServletRequest(asyncContext3, "Output from async3.jspType is ASYNCCompleted async 3 request");
        testAsyncServletRequest(asyncContext4, "STOCK#");
    }

    private void testAsyncServletRequest(String asyncContext, String expectedResponse) throws IOException {

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL + asyncContext, null);
        String result = response.getData();

        log.info("Response - " + result);

        assertTrue(result.startsWith(expectedResponse),
                "Response doesn't contain expected async servlet response" + webAppURL + asyncContext);
    }


    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        if (TestUserMode.TENANT_USER.equals(userMode)) {

            WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
            assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                    "Web Application unDeployment failed");
        }
    }

}
