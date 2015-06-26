/*
 * Copyright 2015 WSO2, Inc. (http://wso2.com)
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
package org.wso2.appserver.integration.tests.webapp.jsp;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests JSTL 1.2 EL expressions
 * WSAS-1687 - Move on to Tomcat implementations of Jasper/EL from Glassfish implementations
 */
public class WSAS1687JSTL12ELExpressionsTestCase extends ASIntegrationTest {
    private final String webAppFileName = "jstl12-example.war";
    private final String webAppName = "jstl12-example";
    private final String webAppContext = "/" + webAppName;
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public WSAS1687JSTL12ELExpressionsTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
            super.init(userMode);
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);

    }

    @Test(groups = "wso2.as", description = "Deploying JSTL web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as", description = "Test EL expressions",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApp() throws Exception {
        String webAppURLLocal = null;
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + webAppContext + "/index.jsp";
        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            webAppURLLocal = webAppURL + "/webapps" + webAppContext + "/index.jsp";
        }

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "flowerName=rose");
        assertEquals(response.getData().trim(), "Color: \"red\"", "EL expression evaluation failed.");

        response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "flowerName=Sunflower");
        assertEquals(response.getData().trim(), "Color: \"NOT red\"", "EL expression evaluation failed.");
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
            dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                        backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + webAppContext;
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed");
    }


    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_ADMIN},
        };
    }

}
