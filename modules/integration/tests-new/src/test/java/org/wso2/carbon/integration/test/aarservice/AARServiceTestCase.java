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

package org.wso2.carbon.integration.test.aarservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/*
  This class can be used to upload an aar service to the server , invocation of the service and delete the uploaded aar
 */
public class AARServiceTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(AARServiceTestCase.class);
    private static String axis2Service = "Axis2Service";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "Upload aar service and verify deployment")
    public void arrServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("Axis2Service.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "Axis2Service.aar", "");

        isServiceDeployed(axis2Service);
        log.info("Axis2Service.aar service uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "invoke aar service", dependsOnMethods = "arrServiceUpload")
    public void invokeService() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/Axis2Service";
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "echoInt");
        log.info("Response : " + response);
        assertTrue(response.toString().contains("<ns:return>25</ns:return>"));

    }

    public static OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://service.carbon.wso2.org", "ns");
        OMElement getOme = fac.createOMElement("echoInt", omNs);

        OMElement getOmeTwo = fac.createOMElement("x", omNs);
        getOmeTwo.setText("25");

        getOme.addChild(getOmeTwo);
        return getOme;
    }
}
