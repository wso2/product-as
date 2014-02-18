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

import org.apache.axis2.rpc.client.RPCServiceClient;

import javax.xml.namespace.QName;
import java.net.URL;

/**
 * RPCClient
 */
public class RPCClient {
    public static void run(String epr) {
        try {
            RPCServiceClient rpcClient = new RPCServiceClient(null, // ConfigurationContext
                                                              new URL(ClientUtils.eprToWSDLUrl(epr)), // WSDL URL
                                                              null, // Service name
                                                              null // service port name
            );

            // Blocking call
            QName operation = new QName("http://service.clientapi.sample.appserver.wso2.org", "echo");
            Object[] objects = rpcClient.invokeBlocking(operation,
                                                        new Object[]{"Hello, I'm RPCClient"},
                                                        new Class[]{String.class});
            System.out.println("Result: " + objects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
