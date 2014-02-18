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

import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;

/**
 * DualChannelNonBlockingClient
 */
public class DualChannelNonBlockingClient {

    private static volatile boolean isComplete;

    public static void run(String epr) {
        try {
            ConfigurationContext configCtx = ConfigurationContextFactory
                    .createConfigurationContextFromFileSystem(null, null);
            ServiceClient serviceClient = new ServiceClient(configCtx, null);

            serviceClient.engageModule("addressing"); // IMPORTANT

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");

            options.setUseSeparateListener(true); // IMPORTANT
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP); // IMPORTANT

            serviceClient.setOptions(options);

            AxisCallback callback = new AxisCallback() {
                /**
                 * This is called when we receive a message.
                 */
                public void onMessage(MessageContext messageContext) {
                    System.out.println("Response: " + messageContext.getEnvelope()
                            .getBody().getFirstElement());
                    isComplete = true;
                }

                /**
                 * This gets called when a fault message is received.
                 */
                public void onFault(MessageContext messageContext) {
                    System.out.println("Fault: " +
                            messageContext.getEnvelope().getBody().getFirstElement());
                }

                /**
                 * This gets called ONLY when an internal processing exception occurs.
                 */
                public void onError(Exception e) {
                    System.out.println("onError");
                    e.printStackTrace();
                }

                /**
                 * This is called at the end of the MEP no matter what happens, quite like
                 * a finally block.
                 */
                public void onComplete() {
                    System.out.println("Completed request");
                    isComplete = true;
                }
            };

            // Non-blocking call
            serviceClient.sendReceiveNonBlocking(ClientUtils
                    .getEchoPayload("Hello, I'm DualChannelNonBlockingClient"), callback); // Notice the callback!
            System.out.println("Sent request. Waiting for response...");

            int i = 0;
            while (!isComplete) {
                Thread.sleep(500);
                i++;
                if (i > 20) {
                    throw new Exception("Response not received within 10s");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
