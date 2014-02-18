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

package org.wso2.carbon.integration.test.jarservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.jarservices.JARServiceUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/*
  This class can be used to upload a jar service to the server , invocation of the service and delete the uploaded jar
 */
public class JARServiceTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JARServiceTestCase.class);
    private static final String jarService = "JarService";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "Upload jar service and verify deployment")
    public void jarServiceUpload() throws Exception {
        JARServiceUploaderClient jarServiceUploaderClient =
                new JARServiceUploaderClient(asServer.getBackEndUrl(),
                        asServer.getSessionCookie());
        List<DataHandler> jarList = new ArrayList<DataHandler>();
        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "jar" + File.separator +
                "artifact1" + File.separator + "JarService.jar");
        DataHandler dh = new DataHandler(url);
        jarList.add(dh);

        jarServiceUploaderClient.uploadJARServiceFile("", jarList, dh);

        isServiceDeployed(jarService);
        log.info("JarService.jar uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "invoke jar service", dependsOnMethods = "jarServiceUpload")
    public void serviceInvoke() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/JarService1";
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "echoInt");
        log.info("Response : " + response);
        assertTrue(response.toString().contains("<ns:return>100</ns:return>"));
    }

    @AfterClass(alwaysRun = true)
    public void jarServiceDelete() throws Exception {
        deleteService(jarService);
        log.info("JarService.jar deleted successfully");

    }

    public static OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://service.carbon.wso2.org", "ns");
        OMElement getOme = fac.createOMElement("echoInt", omNs);

        OMElement getOmeTwo = fac.createOMElement("x", omNs);
        getOmeTwo.setText("100");

        getOme.addChild(getOmeTwo);
        return getOme;
    }

}
