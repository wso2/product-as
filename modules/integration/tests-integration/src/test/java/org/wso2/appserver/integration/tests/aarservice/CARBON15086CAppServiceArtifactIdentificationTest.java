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
package org.wso2.appserver.integration.tests.aarservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ServiceAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClientUtils;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to upload .car application to the server and verify whether that service artifact got
 * deployed through CApp
 */
public class CARBON15086CAppServiceArtifactIdentificationTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CARBON15086CAppServiceArtifactIdentificationTest.class);
    private ServiceAdminClient serviceAdminClient;
    String service = "Calculator";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "upload car file and verify deployment")
    public void carApplicationUpload() throws Exception {
        serviceAdminClient =
                new ServiceAdminClient(backendURL, sessionCookie);
        CarbonAppUploaderClient carbonAppClient =
                new CarbonAppUploaderClient(backendURL, sessionCookie);
        URL url = new URL("file://" + FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator + "AS" + File.separator + "car" + File.separator +
                          "AxisCApp-1.0.0.car");
        DataHandler dataHandler = new DataHandler(url);
        carbonAppClient.uploadCarbonAppArtifact("AxisCApp-1.0.0.car", dataHandler);
        AxisServiceClientUtils.waitForServiceDeployment(backendURL +
                                                        "/Calculator");
        log.info("AxisCApp-1.0.0.car uploaded successfully");
        ServiceMetaData serviceMetaData = serviceAdminClient.getServicesData(service);
        log.info(service + " is CApp artifact? " + serviceMetaData.isCAppArtifactSpecified());
        assertTrue(serviceMetaData.isCAppArtifactSpecified(), "Service is not a CApp artifact, Test Failed");
    }
}
