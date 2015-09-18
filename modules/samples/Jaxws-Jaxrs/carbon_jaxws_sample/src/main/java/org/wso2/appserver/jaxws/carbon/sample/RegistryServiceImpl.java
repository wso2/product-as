package org.wso2.appserver.jaxws.carbon.sample;/*
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

import org.wso2.appserver.jaxws.carbon.sample.beans.RegistryResource;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;
import org.wso2.carbon.user.api.UserStoreException;

public class RegistryServiceImpl implements RegistryService {

    @Override
    public boolean addResource(RegistryResource registryResource) throws UserStoreException, RegistryException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        Registry registry = carbonContext.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
        Resource resource = registry.newResource();
        resource.setContent(registryResource.getValue());
        registry.put(registryResource.getPath(), resource);
        if (registry.resourceExists(registryResource.getPath())) {
            return true;
        }
        return false;
    }

    @Override
    public RegistryResource getResource(String path) throws UserStoreException, RegistryException {
        CarbonContext carbonContext = CarbonContext.getThreadLocalCarbonContext();
        Registry registry = carbonContext.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
        if (registry.resourceExists(path)) {
            Resource resource = registry.get(path);
            RegistryResource registryResource =
                    new RegistryResource(resource.getPath(), new String((byte[]) resource.getContent()));
            return registryResource;
        }
        return null;
    }
}
