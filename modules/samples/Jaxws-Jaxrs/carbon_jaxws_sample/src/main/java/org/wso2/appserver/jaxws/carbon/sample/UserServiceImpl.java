package org.wso2.appserver.jaxws.carbon.sample;
/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.wso2.appserver.jaxws.carbon.sample.beans.User;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;

import javax.jws.WebService;

@WebService(endpointInterface = "org.wso2.appserver.jaxws.carbon.sample.UserService",
        serviceName = "UserService")
public class UserServiceImpl implements UserService {

    @Override
    public String[] getUsers() throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        return realm.getUserStoreManager().listUsers("*", 10);
    }

    @Override
    public String[] getRoles() throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        return realm.getUserStoreManager().getRoleNames();
    }

    @Override
    public String[] getRolesOfUser(String userName) throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        if(realm.getUserStoreManager().isExistingUser(userName)){
            return realm.getUserStoreManager().getRoleListOfUser(userName);
        }
        return new String[0];
    }

    @Override
    public boolean addUser(User user) throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        realm.getUserStoreManager().addUser(user.getUserName(), user.getPassword(), null, null, user.getUserName());
        if (realm.getUserStoreManager().isExistingUser(user.getUserName())) {
            return true;
        }
        return false;
    }
}
