/*
 *
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * /
 */

package org.wso2.appserver.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;

import javax.activation.DataHandler;
import java.rmi.RemoteException;

/**
 * This class is to add and get resources to the server.
 */
public class ResourceAdminServiceClient {

    private static final Log log = LogFactory.getLog(ResourceAdminServiceClient.class);
    private final String serviceName = "ResourceAdminService";
    private ResourceAdminServiceStub resourceAdminServiceStub;

    public ResourceAdminServiceClient(String serviceUrl, String sessionCookie) throws AxisFault {
        String endPoint = serviceUrl + serviceName;
        resourceAdminServiceStub = new ResourceAdminServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, resourceAdminServiceStub);
    }

    /**
     * This method is to add a resource to server
     *
     * @param destinationPath - Destination path to add resource
     * @param mediaType - media type of the resource
     * @param description - Description for the resource
     * @param dh - DataHandler
     * @return boolean - true : if resource added successfully else : false
     * @throws ResourceAdminServiceExceptionException - Error while adding resource
     * @throws java.rmi.RemoteException - Error while adding resource
     */
    public boolean addResource(String destinationPath, String mediaType,
                               String description, DataHandler dh)
            throws ResourceAdminServiceExceptionException, RemoteException {
        if (log.isDebugEnabled()) {
            log.debug("Destination Path :" + destinationPath);
            log.debug("Media Type :" + mediaType);
        }
        return resourceAdminServiceStub.addResource(destinationPath, mediaType, description, dh, null, null);
    }

    /**
     * This method to get the resource from server
     *
     * @param destinationPath - Destination path of the resource
     * @return ResourceData[] - resource data array
     * @throws ResourceAdminServiceExceptionException - Error while getting resource
     * @throws java.rmi.RemoteException - Error while getting resource
     */
    public ResourceData[] getResource(String destinationPath)
            throws ResourceAdminServiceExceptionException, RemoteException {
        ResourceData[] rs;
        rs = resourceAdminServiceStub.getResourceData(new String[]{destinationPath});
        return rs;
    }

}
