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
package org.wso2.carbon.integration.test.sampleservices.jibxservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to jibx sample scenarios.
 */
public class JIBXServiceTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(JIBXServiceTestCase.class);
    private static AxisServiceClient axisServiceClient = new AxisServiceClient();
    private static String endpoint;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        endpoint = asServer.getServiceUrl() + "/LibraryService";
    }

    @AfterClass(alwaysRun = true)
    public void LibraryServiceDelete() throws Exception {   // deletes LibraryService.aar
        deleteService("LibraryService");
        log.info("LibraryService.aar deleted successfully");
    }

    @Test(groups = "wso2.as", description = "Upload LibraryService.aar service and verify deployment")
    public void LibraryServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("LibraryService.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "LibraryService.aar", "");

        isServiceDeployed("LibraryService");
        log.info("LibraryService.aar uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Adding a book to library"
            , dependsOnMethods = "LibraryServiceUpload")
    public void addBookToLibrary() throws Exception {

        OMElement response = axisServiceClient.sendReceive(addBookPayLoad(), endpoint, "addBook");
        log.info("Response : " + response);
	assertTrue(response != null, "response is null");
        assertTrue(response.toString().contains("<success>true</success>"), "<success>true</success> is missing");
    
	/* response - "<addBookResponse " +
                "xmlns=\"http://jibx.appserver.wso2.org/library/types\"><success>true</success>" +
                "</addBookResponse>"));
	*/
    }

    @Test(groups = "wso2.as", description = "Getting the book details"
            , dependsOnMethods = "addBookToLibrary")
    public void getBookDetails() throws Exception {

        OMElement response = axisServiceClient.sendReceive(getBookPayLoad(), endpoint, "getBook");
        log.info("Response : " + response);
        assertTrue(response.toString().contains("getBookResponse"), "message body doesn't contain getBookResponse");
        assertTrue(response.toString().contains("<author>JIBX Service Sample Demo</author>"), "<author>JIBX Service Sample Demo</author> is missing");
        
	/* response
	"<getBookResponse xmlns=\"http://jibx.appserver.wso2.org/" +
                "library/types\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<book type=\"Novel\" isbn=\"123456\"><author>JIBX Service Sample Demo</author>" +
                "<title /></book></getBookResponse>"));
	*/
    }

    private static OMElement addBookPayLoad() {   // payload for addBook operation

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://jibx.appserver.wso2.org/library/types", "ns");
        OMElement getOme = fac.createOMElement("addBook", omNs);

        OMElement getOmeType = fac.createOMElement("type", omNs);
        OMElement getOmeIsbn = fac.createOMElement("isbn", omNs);
        OMElement getOmeAuthor = fac.createOMElement("author", omNs);
        OMElement getOmeTitle = fac.createOMElement("title", omNs);

        getOmeType.setText("Novel");  // book name
        getOmeIsbn.setText("123456"); // isbn number
        getOmeAuthor.setText("wso2 author"); // author
        getOmeAuthor.setText("JIBX Service Sample Demo");  // book title

        getOme.addChild(getOmeType);
        getOme.addChild(getOmeIsbn);
        getOme.addChild(getOmeAuthor);
        getOme.addChild(getOmeTitle);
        return getOme;
    }

    private static OMElement getBookPayLoad() {   // payload for getBook operation

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://jibx.appserver.wso2.org/library/types", "ns");
        OMElement getOme = fac.createOMElement("getBook", omNs);

        OMElement getOmeIsbn = fac.createOMElement("isbn", omNs);
        getOmeIsbn.setText("123456");        // isbn number

        getOme.addChild(getOmeIsbn);
        return getOme;
    }
}
