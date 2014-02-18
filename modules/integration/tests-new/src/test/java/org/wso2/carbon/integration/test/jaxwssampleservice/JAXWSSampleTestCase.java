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
package org.wso2.carbon.integration.test.jaxwssampleservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.JAXWSWebappAdminClient;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClientUtils;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/*
This class uploads java_first_jaxws.war and handlers_jaxws.war to the server , verify deployment and invokes the service
Important : -  Refer JIRA issue WSAS-1196. Hence omitted test cases related to java_first_jaxws.war scenarios.
 */
public class JAXWSSampleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JAXWSSampleTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void webApplicationDelete() throws Exception {
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());
        //  webAppAdminClient.deleteWebAppFile("java_first_jaxws.war");   // Known issue WSAS-1196
        webAppAdminClient.deleteWebAppFile("handlers_jaxws.war");
        log.info("handlers_jaxws.war deleted successfully");
    }

    @Test(groups = "wso2.as", description = "upload war file and verify deployment")
    public void webApplicationUpload() throws Exception {
        asServer = super.asServer;
        JAXWSWebappAdminClient jaxwsWebappAdminClient =
                new JAXWSWebappAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        String location = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "jaxws" + File.separator;

        jaxwsWebappAdminClient.uploadWebapp(location + "handlers_jaxws.war", "handlers_jaxws.war");
        AxisServiceClientUtils.waitForServiceDeployment(asServer.getWebAppURL() +
                "/handlers_jaxws/services/HandlerServicePort");   // verify deployment
        log.info("handlers_jaxws.war file uploaded successfully");

        /*  jaxwsWebappAdminClient.uploadWebapp(location +"java_first_jaxws.war", "java_first_jaxws.war");
         AxisServiceClientUtils.waitForServiceDeployment(asServer.getWebAppURL() +
   "/java_first_jaxws/services/hello_world");   // verify deployment
        log.info("java_first_jaxws.war file uploaded successfully");        */        // Known issue WSAS-1196
    }

    @Test(groups = "wso2.as", description = "invoke service - addNumbers",
            dependsOnMethods = "webApplicationUpload")
    public void serviceRequestForHandlers() throws Exception {
        String endpoint = asServer.getWebAppURL() + "/handlers_jaxws/services/HandlerServicePort";
        OMElement response = AxisServiceClientUtils.sendRequest(createPayLoadForHandler().toString(),
                new EndpointReference(endpoint));
        assertNotNull(response, "Result cannot be null");
        assertEquals(("<addNumbersResponse xmlns=\"http://apache.org/handlers/types\">" +
                "<return>28</return></addNumbersResponse>"),
                response.toString().trim());
    }

    public static OMElement createPayLoadForHandler() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://apache.org/handlers/types", "ns");
        OMElement getOme = fac.createOMElement("addNumbers", omNs);

        OMElement getOmeTwo = fac.createOMElement("arg0", omNs);
        OMElement getOmeThree = fac.createOMElement("arg1", omNs);
        getOmeTwo.setText("25");
        getOmeThree.setText("3");

        getOme.addChild(getOmeTwo);
        getOme.addChild(getOmeThree);
        return getOme;
    }

    @Test(groups = "wso2.as", description = "invoke service - sayHi",
            dependsOnMethods = "webApplicationUpload", enabled = false)
    public void serviceRequestOne() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getWebAppURL() + "/java_first_jaxws/services/hello_world";
        String request = "<ns2:sayHi xmlns:ns2=\"http://server.hw.demo/\">" +
                "<arg0>World</arg0></ns2:sayHi>";

        OMElement response = axisServiceClient.sendReceive(createPayload(request), endpoint,
                "sayHi");
        assertNotNull(response, "Result cannot be null");
        assertEquals(("<ns2:sayHiResponse xmlns:ns2=\"http://server.hw.demo/\">" +
                "<return>Hello World</return></ns2:sayHiResponse>"),
                response.toString().trim());
    }

    @Test(groups = "wso2.as", description = "invoke service - sayHiToUser",
            dependsOnMethods = "serviceRequestOne", enabled = false)
    public void serviceRequestTwo() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getWebAppURL() + "/java_first_jaxws/services/hello_world";
        String request = "<ns2:sayHiToUser xmlns:ns2=\"http://server.hw.demo/\">" +
                "<arg0><name>World</name></arg0></ns2:sayHiToUser>";

        OMElement response = axisServiceClient.sendReceive(createPayload(request), endpoint,
                "sayHiToUser");
        assertEquals(("<ns2:sayHiToUserResponse xmlns:ns2=\"http://server.hw.demo/\">" +
                "<return>Hello World</return></ns2:sayHiToUserResponse>"),
                response.toString().trim());
    }

    @Test(groups = "wso2.as", description = "invoke service - sayHi",
            dependsOnMethods = "serviceRequestTwo", enabled = false)
    public void serviceRequestThree() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getWebAppURL() + "/java_first_jaxws/services/hello_world";
        String request = "<ns2:sayHiToUser xmlns:ns2=\"http://server.hw.demo/\">" +
                "<arg0><name>Galaxy</name></arg0></ns2:sayHiToUser>";

        OMElement response = axisServiceClient.sendReceive(createPayload(request), endpoint,
                "sayHiToUser");
        assertEquals(("<ns2:sayHiToUserResponse xmlns:ns2=\"http://server.hw.demo/\">" +
                "<return>Hello Galaxy</return></ns2:sayHiToUserResponse>"),
                response.toString().trim());
    }

    @Test(groups = "wso2.as", description = "invoke service - sayHi",
            dependsOnMethods = "serviceRequestThree", enabled = false)
    public void serviceRequestFour() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getWebAppURL() + "/java_first_jaxws/services/hello_world";
        String request = "<ns2:getUsers xmlns:ns2=\"http://server.hw.demo/\"/>";
        OMElement response = axisServiceClient.sendReceive(createPayload(request), endpoint,
                "getUsers");
        assertNotNull(response, "Result cannot be null");
        assertEquals(("<ns2:getUsersResponse xmlns:ns2=\"http://server.hw.demo/\">" +
                "<return><entry><id>1</id><user><name>World</name></user></entry><entry>" +
                "<id>2</id><user><name>Galaxy</name></user></entry></return>" +
                "</ns2:getUsersResponse>"),
                response.toString().trim());
    }

    private OMElement createPayload(String request) throws XMLStreamException {
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}
