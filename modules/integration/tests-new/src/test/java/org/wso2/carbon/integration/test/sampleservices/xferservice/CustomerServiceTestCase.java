/*
* Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.integration.test.sampleservices.xferservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.api.clients.module.mgt.ModuleAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;
import org.wso2.xfer.WSTransferConstants;

import javax.xml.namespace.QName;
import java.io.File;

import static org.testng.Assert.assertTrue;

/**
* This class can be used for testing purposes of Xfer sample scenario.
*/
public class CustomerServiceTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CustomerServiceTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void customerServiceDelete() throws Exception {   // deletes CustomerService.aar
        deleteService("CustomerService");
        log.info("CustomerService deleted");
    }

    @Test(groups = "wso2.as", description = "Upload CustomerService.aar service and verify deployment")
    public void customerServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("CustomerService.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "CustomerService.aar", "");

        assertTrue(isServiceDeployed("CustomerService"));
        log.info("CustomerService.aar service uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Invoke Service - CustomerService",
            dependsOnMethods = "customerServiceUpload")
    public void invokeService() throws Exception {

        boolean moduleExists = false;  // checking the availability of wso2xfer-4.1.0 module for the service

        ModuleAdminServiceClient moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        ModuleMetaData[] moduleMetaData = moduleAdminServiceClient.listModulesForService("CustomerService");
        for (int x = 0; x <= moduleMetaData.length; x++) {
            if (moduleMetaData[x].getModulename().contains("wso2xfer")) {
                moduleExists = true;
                //engaging the module to the service
                moduleAdminServiceClient.engageModule(moduleMetaData[x].getModuleId(), "CustomerService");
                break;
            }
        }

        assertTrue(moduleExists, "module engagement failure due to the unavailability of wso2xfer module " +
                "at service level context");

        String endpoint = asServer.getServiceUrl() + "/CustomerService";

        ServiceClient serviceClient = new ServiceClient();
        OperationClient opClient = serviceClient
                .createClient(ServiceClient.ANON_OUT_IN_OP);

        Options options = opClient.getOptions();
        options.setAction(WSTransferConstants.ACTION_URI_CREATE);

        EndpointReference epr = new EndpointReference(endpoint);
        options.setTo(epr);

        MessageContext msgCtx = new MessageContext();
        opClient.addMessageContext(msgCtx);

        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
        SOAPEnvelope env = factory.getDefaultEnvelope();

        Customer customer = new Customer();

        customer.setId("1");
        customer.setFirst("First Wso2");
        customer.setLast("Last Wso2");
        customer.setAddress("123, My Street");
        customer.setCity("My City");
        customer.setState("My State");
        customer.setZip("5432");

        env.getBody().addChild(toOM(customer));
        msgCtx.setEnvelope(env);

        log.info("Creating a new Customer");

        opClient.execute(true);

        opClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
        options = opClient.getOptions();

        options.setTo(epr);
        options.setAction(WSTransferConstants.ACTION_URI_GET);

        env = factory.getDefaultEnvelope();

        msgCtx = new MessageContext();
        opClient.addMessageContext(msgCtx);

        OMElement customerIdHeader = factory.createOMElement(
                Customer.Q_ELEM_CUSTOMER_ID.getLocalPart(),
                Customer.Q_ELEM_CUSTOMER_ID.getNamespaceURI(), "xxx");
        customerIdHeader.setText("1");

        env.getHeader().addChild(customerIdHeader);
        msgCtx.setEnvelope(env);

        log.info("Retrieving the Customer with CustomId - 1");
        opClient.execute(true);

        MessageContext inMsgCtx = opClient
                .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        OMElement element = inMsgCtx.getEnvelope().getBody().getFirstElement();
        Customer customerInfo = fromOM(element);

        // validating the response element
        log.info("Customer Id " +customerInfo.getId());
        assertTrue(customerInfo.getId().equals("1"),"Customer Id Mismatch.");
        log.info("Customer First Name " +customerInfo.getFirst());
        assertTrue(customerInfo.getFirst().equals("First Wso2"),"Customer First Name Mismatch.");
        log.info("Customer Last Name " +customerInfo.getLast());
        assertTrue(customerInfo.getLast().equals("Last Wso2"),"Customer Last Name Mismatch.");
        log.info("Customer Address " +customerInfo.getAddress() );
        assertTrue(customerInfo.getAddress().equals("123, My Street"),"Customer Address Mismatch.");
        log.info("Customer City " +customerInfo.getCity());
        assertTrue(customerInfo.getCity().equals("My City"),"Customer City Mismatch.");
        log.info("Customer State " +customerInfo.getState());
        assertTrue(customerInfo.getState().equals("My State"),"Customer State Mismatch.");
        log.info("Customer Zip " +customerInfo.getZip());
        assertTrue(customerInfo.getZip().equals("5432"),"Customer Zip Code Mismatch.");
    }

    private static Customer fromOM(OMElement element) {
        Customer customer = new Customer();

        OMElement child;

        child = element.getFirstChildWithName(new QName(Customer.NS_URI, "id"));
        customer.setId(child.getText());

        child = element.getFirstChildWithName(new QName(Customer.NS_URI,
                "first"));
        customer.setFirst(child.getText());

        child = element
                .getFirstChildWithName(new QName(Customer.NS_URI, "last"));
        customer.setLast(child.getText());

        child = element.getFirstChildWithName(new QName(Customer.NS_URI,
                "address"));
        customer.setAddress(child.getText());

        child = element
                .getFirstChildWithName(new QName(Customer.NS_URI, "city"));
        customer.setCity(child.getText());

        child = element.getFirstChildWithName(new QName(Customer.NS_URI,
                "state"));
        customer.setState(child.getText());

        child = element
                .getFirstChildWithName(new QName(Customer.NS_URI, "zip"));
        customer.setZip(child.getText());

        return customer;
    }

    private static OMElement toOM(Customer customer) {

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement customerElement = factory.createOMElement("Customer",
                Customer.NS_URI, "xxx");

        OMElement e;

        e = factory.createOMElement("id", Customer.NS_URI, "xxx");
        e.setText(customer.getId());
        customerElement.addChild(e);

        e = factory.createOMElement("first", Customer.NS_URI, "xxx");
        e.setText(customer.getFirst());
        customerElement.addChild(e);

        e = factory.createOMElement("last", Customer.NS_URI, "xxx");
        e.setText(customer.getLast());
        customerElement.addChild(e);

        e = factory.createOMElement("address", Customer.NS_URI, "xxx");
        e.setText(customer.getAddress());
        customerElement.addChild(e);

        e = factory.createOMElement("city", Customer.NS_URI, "xxx");
        e.setText(customer.getCity());
        customerElement.addChild(e);

        e = factory.createOMElement("state", Customer.NS_URI, "xxx");
        e.setText(customer.getState());
        customerElement.addChild(e);

        e = factory.createOMElement("zip", Customer.NS_URI, "xxx");
        e.setText(customer.getZip());
        customerElement.addChild(e);

        return customerElement;
    }
}
