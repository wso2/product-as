/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package ms.integration.tests.systemhostobject;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.mashup.MashupFileUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.*;

/**
 * This class uploads Concatenation.zip verify deployment and invokes the systemTest service
 */
public class SystemHostObjectTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(SystemHostObjectTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        URL urlSystemTest = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "js" + File.separator +
                "Concatenation.zip");
        DataHandler dh = new DataHandler(urlSystemTest);   // creation of data handler
        MashupFileUploaderClient mashupFileUploaderClient = new
                MashupFileUploaderClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        mashupFileUploaderClient.uploadMashUpFile("Concatenation.zip", dh);
    }

    @AfterClass(alwaysRun = true)
    public void serviceDelete() throws Exception {
        deleteService("admin/systemTest");   // deleting systemTest from the services list
        log.info("systemTest service deleted");
    }

    @Test(groups = {"wso2.as"}, description = "Test including an external JavaScript file")
    public void testIncludeJsFile() throws RemoteException, XMLStreamException {
        boolean serDeployedStatus = isServiceDeployed("admin/systemTest");
        assertTrue(serDeployedStatus, "systemTest Service deployment failure ");
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayloadOne(),
                asServer.getServiceUrl() + "/admin/systemTest", "includeJsFile");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:includeJsFileResponse xmlns:ws=\"http://services.mashup.wso2.org/systemTe"
                        + "st?xsd\"><return xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:js=\"h"
                        + "ttp://www.wso2.org/ns/jstype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema"
                        + "-instance\" js:type=\"string\" xsi:type=\"xs:string\">Successfully concaten"
                        + "ated.</return></ws:includeJsFileResponse>");
    }

    @Test(groups = {"wso2.as"}, description = "Test the Local Host Name",
            dependsOnMethods = "testIncludeJsFile")
    public void testLocalHostName() throws AxisFault, XMLStreamException {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayloadTwo(),
                asServer.getServiceUrl() + "/admin/systemTest", "testLocalHostName");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:testLocalHostNameResponse xmlns:ws=\"http://services.mashup.wso2.org/syst"
                        + "emTest?xsd\"><return xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:js"
                        + "=\"http://www.wso2.org/ns/jstype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSc"
                        + "hema-instance\" js:type=\"string\" xsi:type=\"xs:string\">Successfully got "
                        + "localHostName</return></ws:testLocalHostNameResponse>");
    }

    @Test(groups = {"wso2.as"}, description = "Test logging a sample string",
            dependsOnMethods = "testLocalHostName")
    public void testLogAString() throws AxisFault, XMLStreamException {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayloadThree(),
                asServer.getServiceUrl() + "/admin/systemTest", "logAString");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:logAStringResponse xmlns:ws=\"http://services.mashup.wso2.org/systemTest?"
                        + "xsd\"><return xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:js=\"http"
                        + "://www.wso2.org/ns/jstype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-in"
                        + "stance\" js:type=\"string\" xsi:type=\"xs:string\">Successfully logged a St"
                        + "ring.</return></ws:logAStringResponse>");
    }

    @Test(groups = {"wso2.as"}, description = "Test waiting some time",
            dependsOnMethods = "testLogAString")
    public void testWaitSomeTime() throws AxisFault, XMLStreamException {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayloadFour(),
                asServer.getServiceUrl() + "/admin/systemTest", "waitSomeTime");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:waitSomeTimeResponse xmlns:ws=\"http://services.mashup.wso2.org/systemTes"
                        + "t?xsd\"><return xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:js=\"ht"
                        + "tp://www.wso2.org/ns/jstype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-"
                        + "instance\" js:type=\"string\" xsi:type=\"xs:string\">Successfully waited</r"
                        + "eturn></ws:waitSomeTimeResponse>");
    }

    //creation of request
    private OMElement createPayloadOne() throws XMLStreamException {
        String request = "<p:includeJsFile xmlns:p=\"http://www.wso2.org/types\"></p:includeJsFile>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createPayloadTwo() throws XMLStreamException {
        String request = "<p:testLocalHostName xmlns:p=\"http://www.wso2.org/types\">" +
                "</p:testLocalHostName>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createPayloadThree() throws XMLStreamException {
        String request = "<p:logAString xmlns:p=\"http://www.wso2.org/types\"></p:logAString>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createPayloadFour() throws XMLStreamException {
        String request = "<p:waitSomeTime xmlns:p=\"http://www.wso2.org/types\"></p:waitSomeTime>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}
