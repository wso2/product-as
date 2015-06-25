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

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.Calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * When you deploy a webapp, it gets unpacked in the appBase.
 * If you delete that unpacked webapp, it should get re-created by unpacking the
 * webapp.
 *
 */
public class UnpackedWebappRegenerateTestCase extends ASIntegrationTest {

    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;
    private String webAppDeploymentDir;
    private static final int WEBAPP_DEPLOYMENT_DELAY = 90 * 1000;

    private static final Log log = LogFactory.getLog(UnpackedWebappRegenerateTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);
        webAppDeploymentDir = System.getProperty(ServerConstants.CARBON_HOME) + File.separator
                + "repository" + File.separator + "deployment" + File.separator + "server"
                + File.separator + "webapps" + File.separator;


    }

    @Test(groups = "wso2.as", description = "Deploying web application")
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

    public void testInvokeWebApp() throws Exception {
        String webAppURLLocal = webAppURL + "/appServer-valied-deploymant-1.0.0";
        HttpClientUtil client = new HttpClientUtil();
        OMElement omElement = client.get(webAppURLLocal);
        assertEquals(omElement.toString(), "<status>success</status>", "Web app invocation failed.");
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
            Thread.sleep(45000); //fix: WSAS-1991
        } catch (InterruptedException ignored) {
        }
        FileUtils.deleteDirectory(unpackedDirectory);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if (webAppAdminClient.getWebApplist(webAppName).contains(webAppFileName)) {
            webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        }
    }
}
