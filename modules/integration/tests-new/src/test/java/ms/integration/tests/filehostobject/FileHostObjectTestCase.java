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
package ms.integration.tests.filehostobject;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
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
 * This class uploads FileTestJs.zip verify deployment and invokes the fileTest service
 */
public class FileHostObjectTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(FileHostObjectTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "js" + File.separator +
                "FileTestJs.zip");
        DataHandler dh = new DataHandler(url);   // creation of data handler
        MashupFileUploaderClient mashupFileUploaderClient = new
                MashupFileUploaderClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        mashupFileUploaderClient.uploadMashUpFile("FileTestJs.zip", dh);
    }

    @AfterClass(alwaysRun = true)
    public void serviceDelete() throws Exception {
        deleteService("admin/fileTest");   // deleting fileTest from the services list
        log.info("fileTest service deleted");
    }

    @Test(groups = {"wso2.as"}, description = "Test a sample request and a response for file host" +
            " object")
    public void testFile() throws RemoteException, XMLStreamException {
        boolean serDeployedStatus = isServiceDeployed("admin/fileTest");
        assertTrue(serDeployedStatus, "fileTest Service deployment failure ");
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayload(),
                asServer.getServiceUrl() + "/admin/fileTest", "testFile");
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response.toString().trim(),
                "<ws:testFileResponse xmlns:ws=\"http://services.mashup.wso2.org/fileTest?"
                        + "xsd\"><return>Successfully created, opened, written, moved, read and "
                        + "deleted a file.</return></ws:testFileResponse>");
    }

    private OMElement createPayload() throws XMLStreamException {  // creation of request
        String request = "<p:testFile xmlns:p=\"http://www.wso2.org/types\">" +
                "<name>maninda</name></p:testFile>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}

