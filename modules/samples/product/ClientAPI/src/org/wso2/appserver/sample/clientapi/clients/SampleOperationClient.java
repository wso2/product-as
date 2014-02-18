/*                                                                             
 * Copyright 2004,2005 The Apache Software Foundation.                         
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");             
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,           
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */
package org.wso2.appserver.sample.clientapi.clients;

import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;

/**
 * OperationClient
 */
public class SampleOperationClient {

    public static void run(String epr) {
        try {
            ServiceClient serviceClient = new ServiceClient();
            OperationClient operationClient = serviceClient
                    .createClient(ServiceClient.ANON_OUT_IN_OP);

            MessageContext outMsgCtx = new MessageContext();

            Options options = outMsgCtx.getOptions();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");
            serviceClient.setOptions(options);

            outMsgCtx.setEnvelope(getEnvelope());
            System.out.println("Request: " + outMsgCtx.getEnvelope());

            operationClient.addMessageContext(outMsgCtx);

            operationClient.execute(true); // true - blocking, false - nonblocking (use with opClient.setCallback)

            MessageContext inMsgCtx = operationClient
                    .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            System.out.println("Response: "+ inMsgCtx.getEnvelope());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SOAPEnvelope getEnvelope() throws XMLStreamException {

        SOAPFactory soap11Fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope soapEnvelope = soap11Fac.getDefaultEnvelope();

        // Add SOAP body content
        soapEnvelope.getBody().addChild(ClientUtils.getEchoPayload("Hello, I'm Operation Client"));

        // Add a sample header
        String header = "<ns:CustomerKey xmlns:ns=\"http://wso2.com/wsas/customer\">" +
                "Key#1234</ns:CustomerKey>";
        OMElement headerEle =
                new StAXOMBuilder(new ByteArrayInputStream(header.getBytes())).getDocumentElement();
        soapEnvelope.getHeader().addChild(headerEle);

        return soapEnvelope;
    }
}
