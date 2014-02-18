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
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;

/**
 * FireAndForgetClient : Just Fires and Forgets. Even returned exceptions will be ignored..
 */
public class FireAndForgetClient {

    public static void run(String epr) {
        try {
            ServiceClient serviceClient = new ServiceClient();

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");
            serviceClient.setOptions(options);

            System.out.println("Invoking Fire and Forget without Exceptions..");
            serviceClient.fireAndForget(ClientUtils
                    .getEchoPayload("Hello, I'm FireAndForgetClient"));
            System.out.println("Invoking Fire and Forget with Exceptions. But the " +
                    "client will neglect the return exception..");
            serviceClient.fireAndForget(ClientUtils.getEchoPayload("exception"));
        } catch (Exception e) {
            System.out.println("----------------- EXCEPTION -----------------");
            e.printStackTrace();
        }
    }

}
