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
package org.wso2.appserver.integration.tests.commodityquoteservice;

import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClientUtils;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.testng.Assert.assertEquals;

/*
This class uploads CommodityQuoteService to the server , verify deployment and invokes the service
 */
public class CommodityQuoteTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(CommodityQuoteTestCase.class);
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
    }

    @Factory(dataProvider = "userModeProvider")
    public CommodityQuoteTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.SUPER_TENANT_USER},
        };
    }


    @AfterClass(alwaysRun = true)
    public void comQuoServiceDelete() throws Exception {
        deleteService("CommodityQuote");
        log.info("CommodityQuote service deleted");
    }

    @Test(groups = "wso2.as", description = "upload CommodityQuoteService.aar file and verify" +
            " deployment")
    public void comQuoSerUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(backendURL, sessionCookie);
        aarServiceUploaderClient.uploadAARFile("CommodityQuoteService.aar",
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "CommodityQuoteService.aar", "");
        AxisServiceClientUtils.waitForServiceDeployment(getServiceUrl("CommodityQuote"));
        log.info("CommodityQuoteService.aar service uploaded successfully");
    }

    @Test(groups = {"wso2.as"}, description = "invoke the service",
            dependsOnMethods = "comQuoSerUpload")
    public void testGetQuoteRequest() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getContextUrls().getServiceUrl() + "/CommodityQuote";
        OMElement result = axisServiceClient.sendReceive(createPayload(), endpoint, "getQuoteRequest");
        OMElement name = result.getFirstElement().getFirstChildWithName(new QName("name"));
        OMElement symbol = result.getFirstElement().getFirstChildWithName(new QName("symbol"));
        assertEquals("<name>Manganese</name>", name.toString().trim());
        assertEquals("<symbol>mn</symbol>", symbol.toString().trim());
    }

    private OMElement createPayload() throws Exception {
        String request = "<ns1:getQuoteRequest xmlns:ns1=\"http://www.wso2.org/types\">" +
                "<symbol>mn</symbol></ns1:getQuoteRequest>";
        return new StAXOMBuilder(new ByteArrayInputStream(request.getBytes())).getDocumentElement();
    }
}
