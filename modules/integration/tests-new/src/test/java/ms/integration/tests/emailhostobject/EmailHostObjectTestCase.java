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
package ms.integration.tests.emailhostobject;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.mashup.MashupFileUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.mashup.jsservices.stub.fileupload.ExceptionException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.*;

/**
 * This class uploads EmailTestJs.zip verify deployment and invokes the emailTest service
 */
public class EmailHostObjectTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(EmailHostObjectTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void upLoadJsFile() throws Exception {
        super.init(0);
        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "js" + File.separator +
                "EmailTestJs.zip");
        DataHandler dh = new DataHandler(url);   // creation of data handler ..
        MashupFileUploaderClient mashupFileUploaderClient = new
                MashupFileUploaderClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        mashupFileUploaderClient.uploadMashUpFile("EmailTestJs.zip", dh);
    }

    @AfterClass(alwaysRun = true)
    public void serviceDelete() throws Exception {
        deleteService("admin/emailTest");   // deleting scrapperTest from the services list
        log.info("emailTest service deleted");
    }

    @Test(groups = {"wso2.as"}, description = "Test a sample request and a response for" +
            " E-mail host object")
    public void testEmail() throws RemoteException, XMLStreamException, ExceptionException, MalformedURLException {
        boolean serDeployedStatus = isServiceDeployed("admin/emailTest");
        assertTrue(serDeployedStatus, "Service deployment failure ");
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(null,
                asServer.getServiceUrl() + "/admin/emailTest", "sendEmail");
        log.info("Response :" + response);
        assertNotNull(response, "Response cannot be null");
        /*assertEquals(response.toString().trim(),
                "<ws:sendEmailResponse xmlns:ws=\"http://services.mashup.wso2."
                        + "org/emailTest?xsd\"><return>Successfully sent an e-mail.</return>"
                        + "</ws:sendEmailResponse>",
                "Error occurred while sending the e-mail.");*/
        assertTrue(response.toString().contains("Successfully sent an e-mail"));
    }

}
