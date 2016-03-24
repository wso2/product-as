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

package org.wso2.appserver.integration.tests.webapp.mgt;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

import static org.testng.Assert.*;

/**
 * This tests the context reloadable support in AS
 * Tests written for both servlets and jsps.
 */
public class WebApplicationContextReloadTestCase extends ASIntegrationTest {

    private String webAppName = "context-reload-test-webapp";
    private String webAppDeploymentDir;
    private static int WEBAPP_DEPLOYMENT_DELAY = 90 * 1000;

    @SetEnvironment(executionEnvironments = {
            ExecutionEnvironment.STANDALONE }) @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppDeploymentDir = System.getProperty(ServerConstants.CARBON_HOME) + File.separator
                + "repository" + File.separator + "deployment" + File.separator + "server"
                + File.separator + "webapps" + File.separator;
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Deploying exploded web application" +
            " file to deployment directory")
    public void testWebApplicationExplodedDeployment() throws Exception {
        String source = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppName + ".war";

        ArchiveExtractor archiveExtractor = new ArchiveExtractor();
        archiveExtractor.extractFile(source, webAppDeploymentDir + File.separator + webAppName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");


    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Invoke web application used spring",
            dependsOnMethods = "testWebApplicationExplodedDeployment")
    public void testContextReloadServlets() throws Exception {
        String webAppURLLocal = webAppURL + "/" + webAppName + "/servlet";
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getResponseCode(), 200);
        String content1 = response.getData().trim();

        testWebApplicationExplodedDeployment();
        Thread.sleep(WEBAPP_DEPLOYMENT_DELAY);
        response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getResponseCode(), 200);
        String content2 = response.getData().trim();

        assertNotEquals(content1, content2);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Invoke web application used spring",
            dependsOnMethods = "testWebApplicationExplodedDeployment")
    public void testContextReloadingJSPs() throws Exception {
        String webAppURLLocal = webAppURL + "/" + webAppName + "/index.jsp";
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getResponseCode(), 200);
        int time1 = Integer.parseInt(response.getData().trim());

        testWebApplicationExplodedDeployment();
        Thread.sleep(WEBAPP_DEPLOYMENT_DELAY);
        response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getResponseCode(), 200);
        int time2 = Integer.parseInt(response.getData().trim());

        assertNotEquals(time1, time2);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        FileUtils.deleteDirectory(new File(webAppDeploymentDir + File.separator + webAppName));
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

}
