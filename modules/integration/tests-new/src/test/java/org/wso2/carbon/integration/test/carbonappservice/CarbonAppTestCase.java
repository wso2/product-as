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
package org.wso2.carbon.integration.test.carbonappservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.application.mgt.ApplicationAdminClient;
import org.wso2.carbon.automation.api.clients.application.mgt.CarbonAppUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClientUtils;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static org.testng.Assert.assertTrue;

/*
  This class can be used to upload .car application to the server and test deployed services
 */
public class CarbonAppTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CarbonAppTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void carAppDelete() throws Exception {   // deletes the car application and the service
        ApplicationAdminClient appAdminClient = new ApplicationAdminClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());
        appAdminClient.deleteApplication("AxisCApp_1.0.0");
        deleteService("Calculator");
        log.info("Calculator service deleted");
    }

    @Test(groups = "wso2.as", description = "upload car file and verify deployment")
    public void carApplicationUpload() throws Exception {
        CarbonAppUploaderClient carbonAppClient =
                new CarbonAppUploaderClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "car" + File.separator +
                "AxisCApp-1.0.0.car");

        DataHandler dh = new DataHandler(url);
        carbonAppClient.uploadCarbonAppArtifact("AxisCApp-1.0.0.car", dh);
        AxisServiceClientUtils.waitForServiceDeployment(asServer.getServiceUrl() +
                "/Calculator");
        log.info("AxisCApp-1.0.0.car uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "verify the deployed services list",
            dependsOnMethods = "carApplicationUpload")
    public void verifyAppList() throws Exception {
        ApplicationAdminClient applicationAdminClient =
                new ApplicationAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        String[] applicationList = applicationAdminClient.listAllApplications();
        assertTrue(Arrays.asList(applicationList).contains("AxisCApp_1.0.0"));
    }

    @Test(groups = "wso2.as", description = "invoke the service", dependsOnMethods = "verifyAppList")
    public void invokeService() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/Calculator";
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "add");
        log.info("Response : " + response);
        Assert.assertEquals("<ns:addResponse xmlns:ns=\"http://test.com\"><ns:return>500</ns:return>"
                + "</ns:addResponse>", "<ns:addResponse xmlns:ns=\"http://test.com\">" +
                "<ns:return>500</ns:return></ns:addResponse>");
    }

    public static OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://test.com", "ns");
        OMElement getOme = fac.createOMElement("add", omNs);

        OMElement getOmeTwo = fac.createOMElement("a", omNs);
        OMElement getOmeThree = fac.createOMElement("b", omNs);
        getOmeTwo.setText("100");
        getOmeThree.setText("400");

        getOme.addChild(getOmeTwo);
        getOme.addChild(getOmeThree);
        return getOme;
    }
}
