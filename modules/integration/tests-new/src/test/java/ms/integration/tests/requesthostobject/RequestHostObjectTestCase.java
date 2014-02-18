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
package ms.integration.tests.requesthostobject;

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
 * This class uploads RequestTestJs.zip verify deployment and invokes the requestTest service
 */
public class RequestHostObjectTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(RequestHostObjectTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "js" + File.separator +
                "RequestTestJs.zip");
        DataHandler dh = new DataHandler(url);   // creation of data handler
        MashupFileUploaderClient mashupFileUploaderClient = new
                MashupFileUploaderClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        mashupFileUploaderClient.uploadMashUpFile("RequestTestJs.zip", dh);
    }

    @AfterClass(alwaysRun = true)
    public void serviceDelete() throws Exception {
        deleteService("admin/requestTest");   // deleting requestTest from the services list
        log.info("requestTest service deleted");
    }

    @Test(groups = {"wso2.as"}, description = "Test Remote IP of the Request Host Object")
    public void testTestRemoteIp() throws RemoteException, XMLStreamException {
        boolean serDeployedStatus = isServiceDeployed("admin/requestTest");
        assertTrue(serDeployedStatus, "requestTest Service deployment failure");
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayloadRemoteIp(),
                asServer.getServiceUrl() + "/admin/requestTest", "testRemoteIp");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:testRemoteIpResponse xmlns:ws=\"http://services.mashup.wso2.org/request"
                        + "Test?xsd\"><return xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
                        "xmlns:js" + "=\"http://www.wso2.org/ns/jstype\" xmlns:xsi=\"" +
                        "http://www.w3.org/2001/XMLS" + "chema-instance\" js:type=\"string\" " +
                        "xsi:type=\"xs:string\">Remote IP is not"
                        + " empty or null</return></ws:testRemoteIpResponse>"
        );
    }

    @Test(groups = {"wso2.as"}, description = "Test invoked URL of the Request Host Object",
            dependsOnMethods = "testTestRemoteIp")
    public void testTestInvokedUrl() throws AxisFault, XMLStreamException {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayloadInvokedUrl(),
                asServer.getServiceUrl() + "/admin/requestTest", "testInvokedUrl");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:testInvokedUrlResponse xmlns:ws=\"http://services.mashup.wso2.org/reque"
                        + "stTest?xsd\"><return xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmln"
                        + "s:js=\"http://www.wso2.org/ns/jstype\" xmlns:xsi=\"http://www.w3.org/200"
                        + "1/XMLSchema-instance\" js:type=\"string\" xsi:type=\"xs:string\">" +
                        "URL is not" + " empty or null</return></ws:testInvokedUrlResponse>"
        );
    }

    private OMElement createPayloadRemoteIp() throws XMLStreamException { // for testTestRemoteIp()
        String request = "<p:testRemoteIp xmlns:p=\"http://www.wso2.org/types\">" +
                "<name>wso2</name></p:testRemoteIp>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }

    private OMElement createPayloadInvokedUrl() throws XMLStreamException { // for testTestInvokedUrl()
        String request = "<p:testInvokedUrl xmlns:p=\"http://www.wso2.org/types\">" +
                "<name>wso2</name></p:testInvokedUrl>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}
