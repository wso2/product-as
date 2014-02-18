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

package org.wso2.carbon.integration.test.sampleservices.traderservice;

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
 * This class can be used to test trader sample scenarios.
 */
public class TraderTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(TraderTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void chadDelete() throws Exception {   // delete services
        deleteService("ExchangeClient");
        log.info("ExchangeClient service deleted");

        deleteService("ExchangeTrader");
        log.info("ExchangeTrader service deleted");

        deleteService("TraderClient");
        log.info("TraderClient service deleted");

        deleteService("TraderExchange");
        log.info("TraderExchange service deleted");
    }


    @Test(groups = "wso2.as", description = "Upload aar files and verify service deployments")
    public void servicesUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        // uploading ExchangeClient.aar
        aarServiceUploaderClient.uploadAARFile("ExchangeClient.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "ExchangeClient.aar", "");

        isServiceDeployed("ExchangeClient");
        log.info("ExchangeClient service uploaded and deployed successfully");

        // uploading ExchangeTrader.aar
        aarServiceUploaderClient.uploadAARFile("ExchangeTrader.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "ExchangeTrader.aar", "");

        isServiceDeployed("ExchangeTrader");
        log.info("ExchangeTrader service uploaded and deployed successfully");

        // uploading TraderClient.aar
        aarServiceUploaderClient.uploadAARFile("TraderClient.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "TraderClient.aar", "");

        isServiceDeployed("TraderClient");
        log.info("TraderClient service uploaded and deployed successfully");

        // uploading TraderExchange.aar
        aarServiceUploaderClient.uploadAARFile("TraderExchange.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "TraderExchange.aar", "");

        isServiceDeployed("TraderExchange");
        log.info("TraderExchange service uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Creating an account"
            , dependsOnMethods = "servicesUpload")
    public void createAccount() throws Exception {

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/TraderClient";
        OMElement response = axisServiceClient.sendReceive(createAccountPayLoad(), endpoint, "createAccount");
        log.info("Response : " + response);
        assertTrue(response.toString().contains("<ns1:createAccountResponse xmlns:" +
                "ns1=\"http://www.wso2.org/types\"><userid>TradeUser</userid>" +
                "</ns1:createAccountResponse>"));

    }

    @Test(groups = "wso2.as", description = "Depositing money in the exchange"
            , dependsOnMethods = "createAccount")
    public void deposit() throws Exception {

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/TraderClient";
        OMElement response = axisServiceClient.sendReceive(depositPayLoad(), endpoint, "deposit");
        log.info("Response : " + response);
        assertTrue(response.toString().contains("<ns1:depositResponse xmlns:" +
                "ns1=\"http://www.wso2.org/types\">" +
                "<depositStatus>Deposit Successful !!</depositStatus></ns1:depositResponse>"));

    }

    public static OMElement createAccountPayLoad() {   // payload for create account operation

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.wso2.org/types", "ns"); // target namespace
        OMElement getOme = fac.createOMElement("createAccountRequest", omNs);

        OMElement getOmeOne = fac.createOMElement("clientinfo", null);

        OMElement getOmeTwo = fac.createOMElement("name", null);
        OMElement getOmeThree = fac.createOMElement("ssn", null);
        OMElement getOmeFour = fac.createOMElement("password", null);

        getOmeTwo.setText("TradeUser");
        getOmeThree.setText("01");
        getOmeFour.setText("Password");

        getOmeOne.addChild(getOmeTwo);
        getOmeOne.addChild(getOmeThree);
        getOme.addChild(getOmeOne);
        getOme.addChild(getOmeFour);
        return getOme;

    }

    public static OMElement depositPayLoad() {   // payload for deposit operation

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.wso2.org/types", "ns"); // target namespace
        OMElement getOme = fac.createOMElement("depositRequest", omNs);

        OMElement getOmeOne = fac.createOMElement("useridr", null);
        OMElement getOmeTwo = fac.createOMElement("password", null);
        OMElement getOmeThree = fac.createOMElement("amount", null);

        getOmeOne.setText("TradeUser");
        getOmeTwo.setText("Password");
        getOmeThree.setText("10000");

        getOme.addChild(getOmeOne);
        getOme.addChild(getOmeTwo);
        getOme.addChild(getOmeThree);
        return getOme;

    }
}
