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

package org.wso2.appserver.integration.common.artifacts.lazyloading;

import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
/**
 * REST web service that provide the tenant status (TenantStatus and the web-app status(WebAppStatus).
 * TenantStatus is include whether the Tenant context is loaded or not.
 * WebAppStatus id include whether the Tenant context is loaded or not, web-app is started or not and the web-app is in
 * ghost status or not.
 */
public class LazyLoadingInfoService {
    public LazyLoadingInfoService() {
    }

    @Path("tenant-status/{tenantDomain}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Provide the  status of tenant.
     */
    public TenantStatus isTenantLoaded(@PathParam("tenantDomain") String tenantDomain) {
        return LazyLoadingInfoUtil.getTenantStatus(tenantDomain);
    }


    @Path("webapp-status/{tenantDomain}/{webAppName}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * provide the wep-app status.
     */
    public WebAppStatus isWebAppLoaded(@PathParam("tenantDomain") String tenantDomain,
                                       @PathParam("webAppName") String webAppName) {
        WebAppStatus webAppStatus;
        if (MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
            webAppStatus = LazyLoadingInfoUtil.getSuperTenantWebAppStatus(webAppName);
        } else {
            webAppStatus = LazyLoadingInfoUtil.getWebAppStatus(tenantDomain, webAppName);
        }
        return webAppStatus;
    }
    
    @Path("ping")
    @GET
    @Produces("text/plain")
    public String ping(){
        return "Hi!";
    }


}
