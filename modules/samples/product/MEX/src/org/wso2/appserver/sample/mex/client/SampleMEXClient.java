/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.appserver.sample.mex.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.dataretrieval.DRConstants;
import org.apache.axis2.dataretrieval.client.MexClient;
import org.wso2.mex.MexConstants;
import org.wso2.mex.om.Metadata;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

public class SampleMEXClient {

    public static void main(String[] args) throws Exception {
        System.out.println("Please enter the service URL:");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));
        String targetEPR = stdin.readLine();

        System.out.println("Choose type of Metadata");
        System.out.println("[1] XML Schema");
        System.out.println("[2] WSDL");
        System.out.println("[3] WS-Policy");

        String dialect = null;
        String identifier = null;

        try {
            int type = Integer.parseInt(stdin.readLine());
            
            switch (type) {
            case 1:
                dialect = MexConstants.SPEC.DIALECT_TYPE_SCHEMA;
                break;
            case 2:
                dialect = MexConstants.SPEC.DIALECT_TYPE_WSDL;
                break;
            case 3:
                dialect = MexConstants.SPEC.DIALECT_TYPE_POLICY;
                break;
            default:
                break;
            }
        } catch (NumberFormatException ex) {
        }
        
        if (dialect != null) {
            System.out.println("Identifier [Optional]");
            identifier = stdin.readLine();
            identifier = (identifier.length() == 0) ? null : identifier;
        }
        
        SampleMEXClient.fetchServiceMetadata(targetEPR, dialect, identifier);
    }

    public static void fetchServiceMetadata(String targetEPR) throws AxisFault {
        fetchServiceMetadata(targetEPR, null, null);
    }

    public static void fetchServiceMetadata(String targetEPR, String dialect,
            String identifier) throws AxisFault {

        MexClient serviceClient = SampleMexClientUtil
                .getServiceClient(targetEPR);

        OMElement request = serviceClient.setupGetMetadataRequest(dialect,
                identifier);
        OMElement result = serviceClient.sendReceive(request);

        Metadata metadata = new Metadata();
        metadata.fromOM(result);

        SampleMexClientUtil.showResults(result);
    }

    public static void fetchServiceWSDL(String targetEPR) throws AxisFault {
        MexClient serviceClient = SampleMexClientUtil
                .getServiceClient(targetEPR);

        OMElement request = serviceClient.setupGetMetadataRequest(
                DRConstants.SPEC.DIALECT_TYPE_WSDL, null);
        OMElement result = serviceClient.sendReceive(request);

        Metadata metadata = new Metadata();
        metadata.fromOM(result);

        SampleMexClientUtil.showResults(result);
    }

    public static void fetchServiceSchema(String targetEPR) throws AxisFault {
        MexClient mexClient = SampleMexClientUtil.getServiceClient(targetEPR);

        OMElement request = mexClient.setupGetMetadataRequest(
                DRConstants.SPEC.DIALECT_TYPE_SCHEMA, null);
        OMElement result = mexClient.sendReceive(request);

        Metadata metadata = new Metadata();
        metadata.fromOM(result);

        SampleMexClientUtil.showResults(result);
    }
}
