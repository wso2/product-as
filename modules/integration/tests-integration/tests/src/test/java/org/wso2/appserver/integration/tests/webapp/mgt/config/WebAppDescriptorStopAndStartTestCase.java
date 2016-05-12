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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.WebAppMode;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;

/**
 * This class is to test if the classloading configuration happens correctly after stopping and starting webapp
 */
public class WebAppDescriptorStopAndStartTestCase extends WebAppDescriptorTestBase {
    private static final String WEB_APPLICATION = "appServer-cxf-cl-app-1.0.0.war";

    @Factory(dataProvider = "webAppModeProvider")
    public WebAppDescriptorStopAndStartTestCase(WebAppMode webAppMode) {
        super(webAppMode);
    }

    @DataProvider(name = "webAppModeProvider")
    private static WebAppMode[][] WebAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] { new WebAppMode(WEB_APPLICATION, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(WEB_APPLICATION, TestUserMode.TENANT_USER) } };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        sampleAppDirectory = Paths.get(SAMPLE_APP_LOCATION ,"stop-start").toString();
        super.init();
    }

    @Test(groups = "wso2.as",
            description = "Deploying web application")
    public void webApplicationDeploymentTest() throws Exception {
        webApplicationDeployment();
    }

    @Test(groups = "wso2.as",
            description = "Invoke web application before stopping",
            dependsOnMethods = "webApplicationDeploymentTest")
    public void testInvokeBeforeStop() throws AutomationFrameworkException {
        testForEnvironment(true,"Tomcat");
        testForEnvironment(true,"Carbon");
        testForEnvironment(true,"CXF");
        testForEnvironment(true,"Spring");
    }

    @Test(groups = "wso2.as",
            description = "Stop web application",
            dependsOnMethods = "webApplicationDeploymentTest")
    public void testWebApplicationStop() throws IOException {
        webAppAdminClient.stopWebApp(webAppFileName, hostName);
        assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Stopped", "Stop State mismatched");
        String webAppURLLocal = webAppURL + "/" + webAppName;
        Assert.assertEquals(getPage(webAppURLLocal).getResponseCode(), 302,
                "Response code mismatch. Client request " + "got a response even after web app is stopped");
    }

    @Test(groups = "wso2.as",
            description = "Start web application",
            dependsOnMethods = "testWebApplicationStop")
    public void testWebApplicationStart() throws RemoteException {
        webAppAdminClient.startWebApp(webAppFileName, hostName);
        Assert.assertEquals(webAppAdminClient.getWebAppInfo(webAppName).getState(), "Started",
                "Start State mismatched");
    }

    private HttpResponse getPage(String webAppUrl) throws IOException {
        return HttpRequestUtil.sendGetRequest(webAppUrl, null);
    }

    @Test(groups = "wso2.as",
            description = "Invoke web application after stopping and starting",
            dependsOnMethods = "testWebApplicationStart")
    public void testInvokeWebApp() throws AutomationFrameworkException {
        testForEnvironment(true, "Tomcat");
        testForEnvironment(true, "Carbon");
        testForEnvironment(true, "CXF");
        testForEnvironment(true, "Spring");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        deleteWebApplication();
    }
}
