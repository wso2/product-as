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
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.dataretrieval.DRConstants;
import org.apache.axis2.dataretrieval.client.MexClient;
import org.wso2.mex.MexException;
import org.wso2.mex.om.Location;
import org.wso2.mex.om.Metadata;
import org.wso2.mex.om.MetadataReference;
import org.wso2.mex.om.MetadataSection;

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

public class SampleMexClientUtil {
    
    public static void showResults(OMElement result) throws MexException {
        Metadata metadata = new Metadata();
        metadata.fromOM(result);
        
        MetadataSection[] metadatSections = metadata.getMetadatSections();
        
        if (metadatSections == null || metadatSections.length == 0) {
            System.out.println("No MetadataSection is available");
            
        } else  {
            MetadataSection metadataSection;
            for (int i = 0; i < metadatSections.length; i++) {
                metadataSection = metadatSections[i];
                System.out.println("################");
                System.out.println("MetadataSection:");
                System.out.println("################");
                
                String dialect = metadataSection.getDialect();
                if (dialect != null) {
                    System.out.println("Dialect : " + dialect);
                }
                String identifier = metadataSection.getIdentifier();
                if (identifier != null) {
                    System.out.println("Identifier : " + identifier);
                }
                
                OMNode inlineData = metadataSection.getInlineData();
                if (inlineData != null) {
                    System.out.println("InlineData : \n" + inlineData.toString());
                    System.out.println("################");
                    continue;
                }
                
                Location location = metadataSection.getLocation();
                if (location != null) {
                    System.out.println("Location : \n" + location.getURI());
                    System.out.println("################");
                    continue;
                }
                
                MetadataReference metadataReference = metadataSection.getMetadataReference();
                if (metadataReference != null) {
                    System.out.println("MetadataSection : \n" + metadataReference.getEPRElement());
                    System.out.println("################");
                }       
            }
        }
    }

    public static MexClient getServiceClient(String targetEPR) throws AxisFault {
        MexClient serviceClient = new MexClient();

        Options options = serviceClient.getOptions();
        options.setTo(new EndpointReference(targetEPR));
        options.setAction(DRConstants.SPEC.Actions.GET_METADATA_REQUEST);

        options.setExceptionToBeThrownOnSOAPFault(true);

        return serviceClient;
    }

}
