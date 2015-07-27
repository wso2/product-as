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
import org.wso2.carbon.identity.user.profile.stub.UserProfileMgtServiceStub;
import org.wso2.carbon.identity.user.profile.stub.UserProfileMgtServiceUserProfileExceptionException;
import org.wso2.carbon.identity.user.profile.stub.types.AssociatedAccountDTO;
import org.wso2.carbon.identity.user.profile.stub.types.UserProfileAdmin;
import org.wso2.carbon.identity.user.profile.stub.types.UserProfileDTO;

import java.rmi.RemoteException;

/**
 * This is the User Profile Management Service Client
 */
public class UserProfileMgtServiceClient {

    private final String serviceName = "UserProfileMgtService";
    private UserProfileMgtServiceStub userProfileMgtServiceStub;
    private String endPoint;

    /**
     * Constructor with session cookie
     * @param backEndUrl Backend Carbon server URL
     * @param sessionCookie HttpSession cookie
     * @throws AxisFault
     */
    public UserProfileMgtServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        userProfileMgtServiceStub = new UserProfileMgtServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, userProfileMgtServiceStub);
    }

    /**
     * Constructor with basic authentication
     * @param backEndUrl Backend Carbon server URL
     * @param userName User name
     * @param password Password
     * @throws AxisFault
     */
    public UserProfileMgtServiceClient(String backEndUrl, String userName, String password) throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        userProfileMgtServiceStub = new UserProfileMgtServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, userProfileMgtServiceStub);
    }

    /**
     * Delete the user profile given the following parameters
     * @param userName user name of which the profile is created for
     * @param profileName profile name
     * @throws RemoteException
     * @throws org.wso2.carbon.identity.user.profile.stub.UserProfileMgtServiceUserProfileExceptionException
     */
    public void deleteUserProfile(String userName, String profileName)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        userProfileMgtServiceStub.deleteUserProfile(userName, profileName);
    }

    /**
     * Set a profile for the given user
     * @param userName user name of which the profile is creating for
     * @param profile UserProfileDTO object
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public void setUserProfile(String userName, UserProfileDTO profile)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        userProfileMgtServiceStub.setUserProfile(userName, profile);
    }

    /**
     * Get multiple user profiles for a given user name
     * @param userName User name of which the profiles are retrieved of
     * @return UserProfileDTO[] object array
     * @throws RemoteException
     */
    public UserProfileDTO[] getUserProfiles(String userName)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.getUserProfiles(userName);
    }

    /**
     * Retrieve a given user Profile under a given user name
     * @param userName User name of which the profile is created for
     * @param profileName Profile name
     * @return UserProfileDTO object
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public UserProfileDTO getUserProfile(String userName, String profileName)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.getUserProfile(userName, profileName);
    }

    /**
     * Check whether the profile adding is enabled/disabled
     * @return true/false
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public boolean isAddProfileEnabled() throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.isAddProfileEnabled();
    }

    /**
     * Check whether the profile adding is enabled/disabled for a given domain
     * @param domain e.g.: carbon.super/wso2.com
     * @return true/false
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public boolean isAddProfileEnabledForDomain(String domain)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.isAddProfileEnabledForDomain(domain);
    }

    /**
     * Check whether the User store is read only
     * @return true/false
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public boolean isReadOnlyUserStore() throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.isReadOnlyUserStore();
    }

    /**
     * Get the user profile admin instance data structure
     * @return UserProfileAdmin object
     * @throws RemoteException
     */
    public UserProfileAdmin getInstance() throws RemoteException {
        return userProfileMgtServiceStub.getInstance();
    }

    /**
     * Get the user profile fields data structure
     * @return UserProfileDTO object
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public UserProfileDTO getProfileFieldsForInternalStore()
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.getProfileFieldsForInternalStore();
    }

    /**
     * Add an Associated ID for the logged in user under a given Identity Provider
     * @param idpID Identity provider ID
     * @param associatedID Associated ID for the logged in user
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public void addAssociatedID(String idpID, String associatedID)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        userProfileMgtServiceStub.associateID(idpID, associatedID);
    }

    /**
     * Get the associated IDs
     * @return AssociatedAccountDTO[] object array
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public AssociatedAccountDTO[] getAssociatedIDs()
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.getAssociatedIDs();
    }

    /**
     * Get the name associated with the Association Account
     * e.g.: Association created -> testIDP <-> testAdmin
     *       If the logged in user is admin, the return value is admin,
     *       who is the logged in user
     * @param idpID Identity provider ID
     * @param associatedID Associated ID for the logged in user
     * @return logged in user of which the association belongs to
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public String getNameAssociatedWith(String idpID, String associatedID)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtServiceStub.getNameAssociatedWith(idpID, associatedID);
    }

    /**
     * Remove the given Associated ID
     * @param idpID Identity provider ID
     * @param associatedID Associated ID for the logged in user
     * @throws RemoteException
     * @throws UserProfileMgtServiceUserProfileExceptionException
     */
    public void removeAssociateID(String idpID, String associatedID)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        userProfileMgtServiceStub.removeAssociateID(idpID, associatedID);
    }

}
