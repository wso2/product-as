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

package org.wso2.appserver.integration.tests.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.Calendar;

import static org.testng.Assert.assertTrue;

public class Spring3UnpackedWebappRegenerateTestCase extends ASIntegrationTest {

    private final String webAppName = "spring3-restful-simple-service";
    private final String webAppFileName = "spring3-restful-simple-service.war";
    private WebAppAdminClient webAppAdminClient;
    private String webAppDeploymentDir;
    private static final int WEBAPP_DEPLOYMENT_DELAY = 90 * 1000;
    private static final Log log = LogFactory.getLog(Spring3UnpackedWebappRegenerateTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);
        webAppDeploymentDir =
                System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;
    }

    @Test(groups = "wso2.as", description = "Deploying web application", enabled = false)
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                                        "artifacts" + File.separator + "AS" + File.separator + "war"
                                        + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");

        File unpackedWebappFile = new File(webAppDeploymentDir + webAppName);
        assertTrue(unpackedWebappFile.exists(), "Webapp was not unpacked.");

        deleteDirectory(unpackedWebappFile);
        assertTrue(isUnpackedDirCreated(unpackedWebappFile),
                   "Unpack directory has not been re-created within the time frame");

        testInvokeWebApp();
    }

    private void testInvokeWebApp() throws Exception {
        String endpointURL = "/student";
        String endpoint = webAppURL + "/" + webAppName + endpointURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        String expectedMsg = "{\"status\":\"success\"}";
        assertTrue(expectedMsg.equalsIgnoreCase(response.getData()));
    }

    private boolean isUnpackedDirCreated(File unpackedWebappDirectory) throws Exception {
        log.info("waiting " + WEBAPP_DEPLOYMENT_DELAY + " millis for unpacked directory creation - " + webAppFileName);
        Calendar startTime = Calendar.getInstance();
        while ( (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) < WEBAPP_DEPLOYMENT_DELAY) {
            if (unpackedWebappDirectory.exists()) {
                log.info(webAppFileName + " Unpack directory has been re-created.");
                return true;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {

            }
        }
        log.error(webAppFileName + " Unpack directory has not been re-created within the time frame - "
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

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        String hostName = "localhost";
        if (webAppAdminClient.getWebApplist(webAppName).contains(webAppFileName)) {
            webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        }
    }

}
