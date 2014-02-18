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
package org.wso2.carbon.integration.test.webapp.mgt;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Deploying faulty web application and verify whether application is in faulty application list
 */
public class WebApplicationFaultyDeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "appServer-invalied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-invalied-deploymant-1.0.0";
    WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
    }

    @Test(groups = "wso2.as", description = "Deploying faulty web application")
    public void testFaultyWebApplicationDeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                                          File.separator + "AS" + File.separator + "war" + File.separator + webAppFileName);

        assertFalse(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName)
                , "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "UnDeploying faulty web application",
          dependsOnMethods = "testFaultyWebApplicationDeployment")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteFaultyWebAppFile(webAppFileName);
        assertTrue(WebApplicationDeploymentUtil.isFaultyWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppName),
                   "Web Application unDeployment failed");
    }
}
