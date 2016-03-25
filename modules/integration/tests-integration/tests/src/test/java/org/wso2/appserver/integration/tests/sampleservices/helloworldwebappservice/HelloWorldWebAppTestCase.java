/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.appserver.integration.tests.sampleservices.helloworldwebappservice;

import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used for HelloWorldWebApp sample testing scenarios
 */
public class HelloWorldWebAppTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(HelloWorldWebAppTestCase.class);
    private WebAppAdminClient webAppAdminClient;
    private final String hostName = "localhost";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,
                sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void webAppDelete() throws Exception {   // delete web app virtual.war

        webAppAdminClient.deleteWebAppFile("HelloWorldWebapp.war", hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie,
                "HelloWorldWebapp.war"), "HelloWorldWebapp unDeployment failed");
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void webAppsDeploymentTest() throws Exception {

        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation()
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "HelloWorldWebapp.war");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie,
                "HelloWorldWebapp"), "HelloWorldWebapp Deployment failed");
        log.info("HelloWorldWebapp.war uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Invoke web application service",
            dependsOnMethods = "webAppsDeploymentTest")
    public void invokeService() throws Exception {
        String webAppURL1 = webAppURL + "/HelloWorldWebapp/";
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURL1, null);
        log.info("Response " + response);
        Assert.assertTrue(response.getData().contains("<html><head><title>Hello World" +
                "</title></head><body>Hello 1!</body></html>"));
    }
}
