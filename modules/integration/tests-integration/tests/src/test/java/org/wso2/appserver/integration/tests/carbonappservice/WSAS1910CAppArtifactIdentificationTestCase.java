/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.integration.tests.carbonappservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.ServiceAdminClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClientUtils;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;

public class WSAS1910CAppArtifactIdentificationTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(WSAS1910CAppArtifactIdentificationTestCase.class);
    private TestUserMode userMode;
    private CarbonAppUploaderClient carbonAppClient;
    private static final String axis2CApp = "AxisCApp";
    private static final String warCApp = "WarCApp";
    private static final String appVersion = "1.0.0";
    private ServiceAdminClient serviceAdminClient;
    private WebAppAdminClient webAppAdminClient;
    private ApplicationAdminClient applicationAdminClient;

    @Factory(dataProvider = "userModeProvider")
    public WSAS1910CAppArtifactIdentificationTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_USER},
                new TestUserMode[]{TestUserMode.TENANT_USER}
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        carbonAppClient = new CarbonAppUploaderClient(backendURL, sessionCookie);
        serviceAdminClient = new ServiceAdminClient(backendURL, sessionCookie);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        applicationAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
    }

    @Test(groups = "wso2.as",
            description = "Upload CApp which contains an axis2 service and check if setCAppArtifact is true")
    public void verifyIndicatorAxis2ServiceCApp() throws Exception {
        URL urlAxisCApp =
                new URL("file://" + FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                        "AS" + File.separator + "car" + File.separator + axis2CApp + "-" + appVersion + ".car");
        DataHandler dataHandler = new DataHandler(urlAxisCApp);
        carbonAppClient.uploadCarbonAppArtifact(axis2CApp + "-" + appVersion + ".car", dataHandler);
        AxisServiceClientUtils.waitForServiceDeployment(asServer.getContextUrls().getServiceUrl() + "/Calculator");
        ServiceMetaData serviceMetaData = serviceAdminClient.getServicesData("Calculator");
        assertTrue(serviceMetaData.getCAppArtifact(), "The Axis2 Serice is not indicated as a CApp artifact.");
    }

    @Test(groups = "wso2.as",
            description = "Upload CApp which contains an axis2 service and check if setCAppArtifact is true")
    public void verifyIndicatorWebappCApp() throws Exception {
        URL urlAxisCApp =
                new URL("file://" + FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                        "AS" + File.separator + "car" + File.separator + warCApp + "_" + appVersion + ".car");
        DataHandler dataHandler = new DataHandler(urlAxisCApp);
        carbonAppClient.uploadCarbonAppArtifact(warCApp + "_" + appVersion + ".car", dataHandler);

        assertTrue(WebAppDeploymentUtil
                           .isWebApplicationDeployed(backendURL, sessionCookie, "appServer-valid-deploymant-1.0.0"));
        WebappMetadata webappMetadata = webAppAdminClient.getWebAppInfo("appServer-valid-deploymant-1.0.0");
        assertTrue(webappMetadata.getCAppArtifact(), "The Webapp is not incdicated as a CApp artifact");
    }

    @AfterClass(alwaysRun = true)
    public void removeCApp() throws Exception {
        applicationAdminClient.deleteApplication(axis2CApp + "_" + appVersion);
        applicationAdminClient.deleteApplication(warCApp + "_" + appVersion);
        log.info("Niranjan");
    }
}
