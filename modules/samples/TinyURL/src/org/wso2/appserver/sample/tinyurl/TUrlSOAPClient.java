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
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.ServiceClient;

/**
 * SOAP Client to add urls to the TUrl service.
 */
public class TUrlSOAPClient {

    /**
     * Hard coded endpoint address
     */
    public final static String EPR = "http://localhost:9763/services/TUrl";

    public static String addUrl(String url) throws Exception {

        //Create a service client
        ServiceClient client = new ServiceClient();

        //Set the endpoint address
        client.getOptions().setTo(new EndpointReference(EPR));

        //Make the reqest and get the response
        OMElement resp = client.sendReceive(getPayload(url));

        //Extract the URL and return
        return extractUrl(resp);
    }

    private static OMElement getPayload(String url) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement addUrlElem = fac.createOMElement("addUrl", null);
        OMElement urlElem = fac.createOMElement("url", null);
        urlElem.setText(url);
        addUrlElem.addChild(urlElem);

        return addUrlElem;
    }

    private static String extractUrl(OMElement resp) {
        return resp.getFirstElement().getText().trim();
    }

}
