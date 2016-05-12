/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.appserver.integration.tests.webapp.mgt.config;

import org.testng.Assert;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebAppDescriptorTestBase extends ASIntegrationTest {
    public static final String PASS = "Pass";
    public static final String FAIL = "Fail";
    public static final String SAMPLE_APP_LOCATION = Paths
            .get(FrameworkPathUtil.getSystemResourceLocation() + "artifacts", "AS", "war", "webAppDescriptorTesting")
            .toString();
    protected final String webAppFileName;
    protected final String webAppName;
    protected final String webAppLocalURL;
    protected final String hostName = "localhost";
    protected String sampleAppDirectory;
    protected WebAppAdminClient webAppAdminClient;

    public WebAppDescriptorTestBase(WebAppMode webAppMode) {
        webAppName = webAppMode.getWebAppName().split(".war")[0];
        webAppFileName = webAppMode.getWebAppName();
        webAppLocalURL = "/" + webAppName;

    }

    protected void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    protected void webApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(Paths.get(sampleAppDirectory, webAppFileName).toString());

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                webAppName + " web Application Deployment failed");
    }

    protected void testForEnvironment(boolean shouldPass, String environment) throws AutomationFrameworkException {
        String webAppURLLocal = webAppURL + webAppLocalURL + "/verifyEnvironment" + "?environment=" + environment;

        String result = runAndGetResultAsString(webAppURLLocal);

        assertEquals(shouldPass ? PASS : FAIL, result, environment + " test has failed");

    }

    protected String runAndGetResultAsString(String webAppURL) throws AutomationFrameworkException {
        HttpClientUtil client = new HttpClientUtil();
        return client.get(webAppURL).toString().replace("<status>", "").replace("</status>", "");
    }

    protected void deleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + webAppLocalURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);

        Assert.assertEquals(response.getResponseCode(), 302,
                "Response code mismatch. Client request " + "got a response even after web app is undeployed");
    }
}
