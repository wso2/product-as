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
package org.wso2.carbon.integration.test.sampleservices.helloworldwebappservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used for HelloWorldWebApp sample testing scenarios
 */
public class HelloWorldWebAppTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(HelloWorldWebAppTestCase.class);
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());
    }

    @AfterClass(alwaysRun = true)
    public void webAppDelete() throws Exception {   // delete web app virtual.war

        webAppAdminClient.deleteWebAppFile("HelloWorldWebapp.war");
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                "HelloWorldWebapp.war"), "HelloWorldWebapp unDeployment failed");
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void webAppsDeploymentTest() throws Exception {

        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "HelloWorldWebapp.war");

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                "HelloWorldWebapp"), "HelloWorldWebapp Deployment failed");
        log.info("HelloWorldWebapp.war uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Invoke web application service",
            dependsOnMethods = "webAppsDeploymentTest")
    public void invokeService() throws Exception {
        String webAppURL = asServer.getWebAppURL() + "/HelloWorldWebapp/";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        log.info("Response " + response);
        Assert.assertTrue(response.getData().contains("<html><head><title>Hello World" +
                "</title></head><body>Hello 1!</body></html>"));
    }
}
