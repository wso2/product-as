/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.tests.webapp.mgt;

import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.File;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Deploying faulty web application and verify whether application is in faulty application list
 */
public class WebApplicationFaultyDeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "appServer-invalied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-invalied-deploymant-1.0.0";
    private final String hostName = "localhost";
    WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    @Test(groups = "wso2.as", description = "Deploying faulty web application")
    public void testFaultyWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                                          File.separator + "AS" + File.separator + "war" + File.separator + webAppFileName);

        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "UnDeploying faulty web application",
          dependsOnMethods = "testFaultyWebApplicationDeployment")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteFaultyWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isFaultyWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                   "Web Application unDeployment failed");
    }
}
