/*
* Copyright 2004,2013 The Apache Software Foundation.
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
package org.wso2.appserver.integration.tests.webapp.mgt.tmp;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Undeployment of faulty apps doesn't remove the relavant unpacked directory
 *
 * https://wso2.org/jira/browse/WSAS-1716
 *
 */
public class WSAS1716FaultyWebAppUndeployTestCase extends ASIntegrationTest {

    private final String webAppFileName = "WSAS1716FaultyWebApp.war";
    private final String webAppName = "WSAS1716FaultyWebApp";
    private WebAppAdminClient webAppAdminClient;
    private String carbonHome;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "deploy faulty webApp")
    public void testDeployFaultyWebApp() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppFileName);
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        String pathToWebApp = carbonHome + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;

        //delete the war file via file system
        FileManager.deleteFile(pathToWebApp + webAppFileName);
        assertTrue(WebAppDeploymentUtil.isFaultyWebApplicationUnDeployed`(
                backendURL, sessionCookie, webAppName), "Web Application unDeployment failed");

        assertFalse(new File(pathToWebApp + webAppName).exists(),
                "unpacked webapp file has not been deleted during undeployment - " +
                        pathToWebApp + webAppName);
        assertFalse(new File(pathToWebApp + webAppFileName).exists(),
                "webapp war file has not been deleted during undeployment " +
                        pathToWebApp + webAppFileName);

    }
}
