/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to upload .car application to the server and verify whether that web application artifact got
 * deployed through CApp
 */
public class CARBON15086CAppWebappArtifactIdentificationTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON15086CAppWebappArtifactIdentificationTest.class);
    private WebAppAdminClient webAppAdminClient;
    String webapp = "appServer-valid-deploymant-1.0.0";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "upload car file and verify deployment")
    public void carApplicationUpload() throws Exception {
        webAppAdminClient = new WebAppAdminClient(backendURL,
                                                  sessionCookie);
        CarbonAppUploaderClient carbonAppClient =
                new CarbonAppUploaderClient(backendURL, sessionCookie);
        URL url = new URL("file://" + FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator + "AS" + File.separator + "car" + File.separator +
                          "WarCApp_1.0.0.car");
        DataHandler dataHandler = new DataHandler(url);
        carbonAppClient.uploadCarbonAppArtifact("WarCApp_1.0.0.car", dataHandler);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webapp)
                , "Web Application Deployment failed");
        log.info("WarCApp_1.0.0.car uploaded successfully");
        WebappMetadata webappMetadata = webAppAdminClient.getWebAppInfo(webapp);
        log.info(webapp + " is CApp artifact ? " + webappMetadata.isCAppArtifactSpecified());
        assertTrue(webappMetadata.isCAppArtifactSpecified(), "Web Application is not a CApp artifact, Test Failed");
    }
}
