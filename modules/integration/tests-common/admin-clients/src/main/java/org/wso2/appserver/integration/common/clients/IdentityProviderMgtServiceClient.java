/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
*/

package org.wso2.appserver.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.identity.application.common.model.idp.xsd.IdentityProvider;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceIdentityApplicationManagementExceptionException;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceStub;

import java.rmi.RemoteException;

/**
 * This is the IdP Management Service Client
 */
public class IdentityProviderMgtServiceClient {

    private final String serviceName = "IdentityProviderMgtService";
    private IdentityProviderMgtServiceStub idPMgtStub;
    private String endPoint;

    /**
     * Constructor to authenticate with the backend URL and the session cookie
     * @param sessionCookie HttpSession cookie
     * @param backEndUrl Backend Carbon server URL
     * @throws org.apache.axis2.AxisFault
     */
    public IdentityProviderMgtServiceClient(String backEndUrl, String sessionCookie)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        idPMgtStub = new IdentityProviderMgtServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, idPMgtStub);
    }

    /**
     * Constructor for basic authentication
     * Authenticate with backend URL and Username Password
     * @param userName Username of the login user
     * @param password Password of the login user
     * @param backEndUrl Backend Carbon server URL
     * @throws org.apache.axis2.AxisFault
     */
    public IdentityProviderMgtServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        idPMgtStub = new IdentityProviderMgtServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, idPMgtStub);
    }

    /**
     * Adds an Identity Provider
     * @param identityProvider IdentityProvider object
     * @throws RemoteException
     * @throws IdentityProviderMgtServiceIdentityApplicationManagementExceptionException
     */
    public void addIdP(IdentityProvider identityProvider)
            throws RemoteException, IdentityProviderMgtServiceIdentityApplicationManagementExceptionException {
        idPMgtStub.addIdP(identityProvider);

    }

    /**
     * Deletes an Identity Provider
     * @param idPName Name of the IdP to be deleted
     * @throws RemoteException
     * @throws IdentityProviderMgtServiceIdentityApplicationManagementExceptionException
     */
    public void deleteIdP(String idPName)
            throws RemoteException, IdentityProviderMgtServiceIdentityApplicationManagementExceptionException {
        idPMgtStub.deleteIdP(idPName);
    }

}
