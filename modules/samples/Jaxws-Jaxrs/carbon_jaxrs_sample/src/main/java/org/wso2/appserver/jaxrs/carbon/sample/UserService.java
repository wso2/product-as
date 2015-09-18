package org.wso2.appserver.jaxrs.carbon.sample;
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

import org.wso2.appserver.jaxrs.carbon.sample.beans.User;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/userservice")
public class UserService {

    @GET
    @Path("/users/")
    public Response getUsers() throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        return Response.ok().entity(Arrays.toString(realm.getUserStoreManager().listUsers("*", 10))).build();
    }

    @GET
    @Path("/userroles/{username}")
    public Response getRolesOfUser(@PathParam("username") String username) throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        return Response.ok().entity(Arrays.toString(realm.getUserStoreManager().getRoleListOfUser(username))).build();
    }

    @GET
    @Path("/roles")
    public Response getRoles() throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        return Response.ok().entity(Arrays.toString(realm.getUserStoreManager().getRoleNames())).build();
    }

    @POST
    @Path("/user/add")
    @Consumes(MediaType.APPLICATION_XML)
    public Response addUser(User user) throws UserStoreException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = carbonContext.getUserRealm();
        realm.getUserStoreManager().addUser(user.getUserName(), user.getPassword(), null, null, user.getUserName());
        return Response.ok().entity(user).build();
    }
}
