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
package org.wso2.appserver.sample.xfer.client;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.wso2.appserver.sample.util.CustomerUtil;
import org.wso2.appserver.sample.xfer.Customer;
import org.wso2.xfer.WSTransferConstants;

public class CustomerServiceClient {

    private static final String PARAM_ENDPOINT = "-e";
    private static final String PARAM_HELP = "-help";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://localhost:9763/services/CustomerService";

        if (args.length > 0) {
            if (PARAM_HELP.equals(args[0])) {
                printUsage();
                System.exit(0);
            } else if (PARAM_ENDPOINT.equals(args[0]) && args.length > 1) {
                endpoint = args[1];
            }
        }

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
        customer.setFirst("Roy");
        customer.setLast("Hill");
        customer.setAddress("321, Main Street");
        customer.setCity("Manhattan Beach");
        customer.setState("CA");
        customer.setZip("9226");

        env.getBody().addChild(CustomerUtil.toOM(customer));
        msgCtx.setEnvelope(env);

        System.out.println("Creating a nex Customer");
        CustomerUtil.printCustomerInfo(customer);
        opClient.execute(true);

        opClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
        options = opClient.getOptions();

        options.setTo(epr);
        options.setAction(WSTransferConstants.ACTION_URI_GET);

        env = factory.getDefaultEnvelope();

        //////////////////////////////////////////////////////////////////////////
        
        msgCtx = new MessageContext();
        opClient.addMessageContext(msgCtx);
        
        OMElement customerIdHeader = factory.createOMElement(
                Customer.Q_ELEM_CUSTOMER_ID.getLocalPart(),
                Customer.Q_ELEM_CUSTOMER_ID.getNamespaceURI(), "xxx");
        customerIdHeader.setText("1");

        env.getHeader().addChild(customerIdHeader);
        msgCtx.setEnvelope(env);

        System.out.println("Retriving the Customer with CustomId - 1" );
        opClient.execute(true);

        MessageContext inMsgCtx = opClient
                .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        OMElement element = inMsgCtx.getEnvelope().getBody().getFirstElement();
        Customer customer2 = CustomerUtil.fromOM(element);
        CustomerUtil.printCustomerInfo(customer2);
    }

    private static void printUsage() {
        System.out.println("\n============================= HELP =============================\n");
        System.out.println("Following optional parameters can be used" +
                " when running the client\n");
        System.out.println("\t" + PARAM_ENDPOINT + "\t: Endpoint URL of the service ");
        System.out.println("\t" + PARAM_HELP + "\t: For Help \n");
    }

}
