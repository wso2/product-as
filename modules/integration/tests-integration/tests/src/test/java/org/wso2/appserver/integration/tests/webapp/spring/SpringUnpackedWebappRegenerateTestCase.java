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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppMode;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.Calendar;

import static org.testng.Assert.assertTrue;

public class SpringUnpackedWebappRegenerateTestCase extends ASIntegrationTest {

    private WebAppMode webAppMode;
    private WebAppAdminClient webAppAdminClient;
    private String webAppDeploymentDir;
    private static final int WEBAPP_DEPLOYMENT_DELAY = 90 * 1000;
    private static final Log log = LogFactory.getLog(SpringUnpackedWebappRegenerateTestCase.class);

    @Factory(dataProvider = "webAppModeProvider")
    public SpringUnpackedWebappRegenerateTestCase(WebAppMode webAppMode) {
        this.webAppMode = webAppMode;
    }

    @DataProvider
    private static WebAppMode[][] webAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] {new WebAppMode("spring3-restful-simple-service", TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode("spring4-restful-simple-service", TestUserMode.SUPER_TENANT_ADMIN)},
        };
    }

    @BeforeClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);
        webAppDeploymentDir =
                System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.as", description = "Deploying web application", enabled = false)
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                                        webAppMode.getWebAppName() + ".war");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppMode.getWebAppName())
                , "Web Application Deployment failed");

        File unpackedWebappFile = new File(webAppDeploymentDir + webAppMode.getWebAppName());
        assertTrue(unpackedWebappFile.exists(), "Webapp was not unpacked.");

        deleteDirectory(unpackedWebappFile);
        assertTrue(isUnpackedDirCreated(unpackedWebappFile),
                   "Unpack directory has not been re-created within the time frame");

        testInvokeWebApp();
    }

    private void testInvokeWebApp() throws Exception {
        String endpointURL = "/student";
        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        String expectedMsg = "{\"status\":\"success\"}";
        assertTrue(expectedMsg.equalsIgnoreCase(response.getData()));
    }

    private boolean isUnpackedDirCreated(File unpackedWebappDirectory) throws Exception {
        log.info("waiting " + WEBAPP_DEPLOYMENT_DELAY + " millis for unpacked directory creation - " +
                 webAppMode.getWebAppName() + ".war");
        Calendar startTime = Calendar.getInstance();
        while ( (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) < WEBAPP_DEPLOYMENT_DELAY) {
            if (unpackedWebappDirectory.exists()) {
                log.info(webAppMode.getWebAppName() + ".war: Unpack directory has been re-created.");
                return true;
            }
            Thread.sleep(ASIntegrationConstants.WEBAPP_WAIT_PERIOD);
        }
        log.error(webAppMode.getWebAppName() + ".war: Unpack directory has not been re-created within the time frame - "
                  + WEBAPP_DEPLOYMENT_DELAY);
        return false;
    }

    private void deleteDirectory(File unpackedDirectory) throws Exception {
        try {
            Thread.sleep(90000); //Todo: WSAS-1991 : Unpacked directory doesn't get re-created if it is deleted immediately after deployment
        } catch (InterruptedException ignored) {
        }
        FileManipulator.deleteDir(unpackedDirectory);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE })
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        String hostName = "localhost";
        if (webAppAdminClient.getWebApplist(webAppMode.getWebAppName()).contains(webAppMode.getWebAppName() + ".war")) {
            webAppAdminClient.deleteWebAppFile(webAppMode.getWebAppName() + ".war", hostName);
        }
    }

}
