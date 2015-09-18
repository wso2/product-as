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

import org.wso2.appserver.jaxrs.carbon.sample.beans.RegistryResource;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/registryservice")
public class RegistryService {

    @POST
    @Path("/resource/add")
    public Response addResource(RegistryResource regResource) throws UserStoreException, RegistryException {
        CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext();
        Registry registry = cCtx.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
        Resource resource = registry.newResource();
        resource.setContent(regResource.getValue());
        registry.put(regResource.getPath(), resource);
        return Response.ok().entity(regResource).build();
    }

    @GET
    @Path("/resource/get")
    public Response getResource(@QueryParam("resourcePath") String resourcePath) throws RegistryException {
        CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext();
        Registry registry = cCtx.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
        if (registry.resourceExists(resourcePath)) {
            return Response.ok().entity(new String((byte[]) registry.get(resourcePath).getContent())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Resource doesn't exist").build();
    }
}
