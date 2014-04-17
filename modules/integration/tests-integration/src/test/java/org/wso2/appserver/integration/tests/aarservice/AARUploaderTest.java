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

package org.wso2.appserver.integration.tests.aarservice;

import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.aarservices.stub.ServiceUploaderStub;
import org.wso2.carbon.aarservices.stub.types.carbon.AARServiceData;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;

import javax.activation.DataHandler;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertTrue;

public class AARUploaderTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(AARUploaderTest.class);
    private static String axis2Service = "Axis2Service";
    private AARServiceUploaderClient aarServiceUploaderClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        aarServiceUploaderClient =
                new AARServiceUploaderClient(backendURL, sessionCookie);

    }

    @Test(groups = "wso2.as", description = "Upload aar service and verify deployment")
    public void arrServiceUpload() throws Exception {
        String filePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                          File.separator + "AS" + File.separator + "aar" + File.separator +
                          "Axis2Service.aar";
        aarServiceUploaderClient.uploadAARFile("Axis2Service.aar", filePath, "");
        isServiceDeployed(axis2Service);
        log.info("Axis2Service.aar service uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "invoke aar service", dependsOnMethods = "arrServiceUpload")
    public void invokeService() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getContextUrls().getServiceUrl() + "/Axis2Service";
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

    private DataHandler createDataHandler(String filePath) throws MalformedURLException {
        URL url = null;
        try {
            url = new URL("file://" + filePath);
        } catch (MalformedURLException e) {
            log.error("File path URL is invalid" + e);
            throw new MalformedURLException("File path URL is invalid" + e);
        }
        DataHandler dh = new DataHandler(url);
        return dh;
    }
}
