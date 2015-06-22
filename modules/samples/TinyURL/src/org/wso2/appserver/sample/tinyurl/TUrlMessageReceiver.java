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

package org.wso2.appserver.sample.tinyurl;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

/**
 * MessageReceiver of the TUrl service
 */
public class TUrlMessageReceiver extends AbstractInOutSyncMessageReceiver {

    public final static String ADD_URL_LN = "addUrl";
    public final static String URL_LN = "url";
    public final static String GO_LN = "go";
    public final static String ID_LN = "id";

    /**
     * Axis2 will only call this method if it receives a request
     * that confirms to any of the defined operations
     * <p/>
     * We have two such operations:
     * - addUrl
     * - go
     * <p/>
     * addUrl's request will be as shown below:
     * <addUrl>
     * <url>http://test.org</url>
     * </addUrl>
     * <p/>
     * go's request will be as follows:
     * <go>
     * <id>path</path>
     * </go>
     */
    public void invokeBusinessLogic(MessageContext inMessage,
                                    MessageContext outMessage) throws AxisFault {

        //Get hold of the request SOAP Envelope
        SOAPEnvelope env = inMessage.getEnvelope();

        //Get the body element
        SOAPBody body = env.getBody();

        //Find the operation
        OMElement operationElem = body.getFirstElement();

        String response;

        if (operationElem != null && ADD_URL_LN.equals(operationElem.getLocalName())) {
            OMElement urlElem = operationElem.getFirstElement();
            if (urlElem != null && URL_LN.equals(urlElem.getLocalName())
                && urlElem.getText() != null
                && !"".equals(urlElem.getText())) {
                response = TUrl.addUrl(urlElem.getText(), outMessage);
            } else {
                response = TUrl.getErrorMessage(TUrl.ERR_INVALID_REQ);
            }
        } else if (operationElem != null && GO_LN.equals(operationElem.getLocalName())) {
            OMElement idElem = operationElem.getFirstElement();
            if (idElem != null && ID_LN.equals(idElem.getLocalName())
                && idElem.getText() != null
                && !"".equals(idElem.getText())) {
                response = TUrl.go(idElem.getText(), outMessage);
            } else {
                response = TUrl.getErrorMessage(TUrl.ERR_INVALID_REQ);
            }
        } else {
            response = TUrl.getErrorMessage(TUrl.ERR_INVALID_REQ);
        }

        //Create the response envelope
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope respEnv = fac.getDefaultEnvelope();

        //Create the response XML and add ito the response envelope
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(response.getBytes());
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(bais);
            StAXOMBuilder builder = new StAXOMBuilder(fac, reader);
            OMElement respElem = builder.getDocumentElement();
            respEnv.getBody().addChild(respElem);
        } catch (Exception e) {
            throw new AxisFault(TUrl.getErrorMessage(TUrl.ERR_INTERNAL), e);
        }

        //Set the response enveope in the response message context
        outMessage.setEnvelope(respEnv);

        //Set the response content type to text/html
        outMessage.setProperty(Constants.Configuration.CONTENT_TYPE, "text/html");
    }
}
