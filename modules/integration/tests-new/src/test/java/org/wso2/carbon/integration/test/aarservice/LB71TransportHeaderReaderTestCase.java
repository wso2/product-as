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
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class LB71TransportHeaderReaderTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(LB71TransportHeaderReaderTestCase.class);
    private static String serviceName = "TransportHeaderReaderService";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void cleaup() throws Exception {
        super.deleteService(serviceName);
    }

    @Test(groups = "wso2.as", description = "Upload aar service and verify deployment")
    public void arrServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                                               asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("TransportHeaderReaderService.aar",
                                               ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                                               File.separator + "AS" + File.separator + "aar" + File.separator +
                                               "TransportHeaderReaderService.aar", "");

        isServiceDeployed(serviceName);
        log.info("TransportHeaderReaderService.aar service uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "invoke aar service", dependsOnMethods = "arrServiceUpload",
          dataProvider = "caseProvider")
    public void invokeService(String caseProvider) throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/TransportHeaderReaderService";

        // Create an instance of org.apache.axis2.client.ServiceClient
        ServiceClient client = new ServiceClient();

        // Create an instance of org.apache.axis2.client.Options
        Options options = new Options();
        options.setTo(new EndpointReference(endpoint));
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        options.setTimeOutInMilliSeconds(45000);
        options.setAction("urn:" + "checkHeaders");
        client.setOptions(options);
        List list = new ArrayList();

        // Create an instance of org.apache.commons.httpclient.Header
        Header header = new Header();

        // Http header. Name : user, Value : admin
        header.setName(caseProvider);
        header.setValue("VALUE");

        list.add(header);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_HEADERS, list);

        client.setOptions(options);
        assertTrue(client.sendReceive(createPayLoad(caseProvider)).toString().contains("VALUE"));
    }

    public static OMElement createPayLoad(String customHeader) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://http.transport.as.wso2.org", "http");
        OMElement getOme = fac.createOMElement("checkHeaders", omNs);

        OMElement getOmeTwo = fac.createOMElement("headerName", omNs);
        getOmeTwo.setText(customHeader);

        getOme.addChild(getOmeTwo);
        return getOme;
    }

    @DataProvider(name = "caseProvider")
    public Object[][] createData1() {
        return new Object[][]{
                {"UPPERCASE"},
                {"lowercase"},
                {"CamelCase"},
        };
    }
}

