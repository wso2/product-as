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
package org.wso2.appserver.integration.tests.carbonappservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static org.testng.Assert.assertTrue;

/*
  This class can be used to upload .car application to the server and test deployed services
 */
public class WSAS1798CAppWarUndeployTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(WSAS1798CAppWarUndeployTest.class);
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
    }

    @Factory(dataProvider = "userModeProvider")
    public WSAS1798CAppWarUndeployTest(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_USER},
        };
    }

    @Test(groups = "wso2.as", description = "upload car file and verify deployment")
    public void carApplicationUpload() throws Exception {
        CarbonAppUploaderClient carbonAppClient =
                new CarbonAppUploaderClient(backendURL, sessionCookie);

        URL url = new URL("file://" + FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "car" + File.separator +
                "WarCApp_1.0.0.car");

        DataHandler dh = new DataHandler(url);
        carbonAppClient.uploadCarbonAppArtifact("WarCApp_1.0.0.car", dh);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, "appServer-valid-deploymant-1.0.0")
                , "Web Application Deployment failed");

        log.info("WarCApp_1.0.0.car uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "verify the deployed services list",
            dependsOnMethods = "carApplicationUpload")
    public void verifyAppList() throws Exception {
        ApplicationAdminClient applicationAdminClient =
                new ApplicationAdminClient(backendURL, sessionCookie);
        String[] applicationList = applicationAdminClient.listAllApplications();
        assertTrue(Arrays.asList(applicationList).contains("WarCApp_1.0.0"));
    }


    @Test(groups = "wso2.as", description = "Delete Composite Application",
            dependsOnMethods = "verifyAppList")
    public void carAppDelete() throws Exception {   // deletes the car application
        ApplicationAdminClient appAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
        appAdminClient.deleteApplication("WarCApp_1.0.0");

        Thread.sleep(30000);
        log.info("WarCApp_1.0.0 CApp deleted");
    }


    @Test(groups = "wso2.as", description = "Invoke web application",
            dependsOnMethods = "carAppDelete")
    public void testVerifyWebApp() throws Exception {

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, "appServer-valid-deploymant-1.0.0")
                , "Webapp appServer-valid-deploymant-1.0.0, was not successfully removed");
    }
}
