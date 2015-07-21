package org.wso2.appserver.integration.common.artifact.carboncontext;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TenantServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(TenantServlet.class);
    private static final String RESOLVE_TENANT_ID = "resolveTenantId";
    private static final String RESOLVE_TENANT_DOMAIN = "resolveTenantDomain";

    private static final String GET_REGISTRY = "getRegistry";
    private static final String SET_APPLICATION_NAME = "setAppName";
    private static final String SET_USERNAME = "setUsername";
    private static final String UNLOAD_TENANT = "unloadTenant";
    private static final String ACCESS_REGISTRY_WITHOUTOUT_LOAD = "accessRegistryWithoutLoad";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    private void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        switch (action) {
            case RESOLVE_TENANT_ID:
                initializeTenantInfo(request, response, action);
                break;
            case RESOLVE_TENANT_DOMAIN:
                initializeTenantInfo(request, response, action);
                break;
            case GET_REGISTRY:
                getRegistry(request, response);
                break;
            case SET_APPLICATION_NAME:
                setApplicationName(request, response);
                break;
            case SET_USERNAME:
                setUsername(request, response);
                break;
            case UNLOAD_TENANT:
                unloadTenant(request, response);
                break;
            case ACCESS_REGISTRY_WITHOUTOUT_LOAD:
                accessRegistryWithoutLoad(request, response);
                break;
        }
    }

    private void accessRegistryWithoutLoad(HttpServletRequest request, HttpServletResponse response) {
        String tenantId = request.getParameter("tenantId");
        if (tenantId != null && !tenantId.isEmpty()) {
            try {
                PrivilegedCarbonContext.startTenantFlow();
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(Integer.parseInt(tenantId), true);
                Registry registry = PrivilegedCarbonContext.getThreadLocalCarbonContext()
                                                           .getRegistry(RegistryType.USER_CONFIGURATION);
                Resource resource = registry.newResource();
                resource.setContent("TestResource".getBytes());
                registry.put("TestResourcePath", resource);
            } catch (RegistryException e) {
                if (log.isDebugEnabled()) {
                    log.error(e.getMessage(), e);
                }
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        } else {
            log.info("tenantId should be initialized");
        }
    }

    private void unloadTenant(HttpServletRequest request, HttpServletResponse response) {
        String tenantId = request.getParameter("tenantId");
        if (tenantId != null && !tenantId.isEmpty()) {
            PrivilegedCarbonContext.unloadTenant(Integer.parseInt(tenantId));
        } else {
            log.info("tenantId should be initialized");
        }
    }

    private void setApplicationName(HttpServletRequest request, HttpServletResponse response) {
        String appName = request.getParameter("appName");
        if (appName != null && !appName.isEmpty()) {
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setApplicationName(appName);
            response.addHeader("set-app-name",
                               PrivilegedCarbonContext.getThreadLocalCarbonContext().getApplicationName());
        } else {
            log.info("appName need to be initialized");
        }
    }

    private void setUsername(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        if (username != null && !username.isEmpty()) {
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(username);
            response.addHeader("set-username", PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
        } else {
            log.info("username should be initialized");
        }
    }

    private void getRegistry(HttpServletRequest request, HttpServletResponse response) {
        CarbonContext.getThreadLocalCarbonContext().getRegistry(RegistryType.SYSTEM_GOVERNANCE);
        String regKey = request.getParameter("regKey");
        String regValue = request.getParameter("regValue");
        PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        if (regKey != null && !regKey.isEmpty() & regValue != null) {
            try {
                Registry registry = privilegedCarbonContext.getRegistry(RegistryType.LOCAL_REPOSITORY);
                Resource resource = registry.newResource();
                resource.setContent(regValue.getBytes());
                registry.put(regKey, resource);

                Resource retrievedValue = registry.get(regKey);
                response.addHeader("retrieved-registry-value", new String((byte[]) retrievedValue.getContent()));
            } catch (RegistryException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage(), e);
                }
            }
        } else {
            log.info("regKey and regValue need to be initialized");
        }
    }

    private void initializeTenantInfo(HttpServletRequest request, HttpServletResponse response, String action) {
        try {
            PrivilegedCarbonContext.startTenantFlow();

            String tenantId = request.getParameter("tenantId");
            String tenantDomain = request.getParameter("tenantDomain");
            String setWithResolve = request.getParameter("setWithResolve");
            String getWithResolve = request.getParameter("getWithResolve");

            PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();

            boolean isSetWithResolve = false;
            boolean isGetWithResolve = false;

            if (setWithResolve != null) {
                isSetWithResolve = Boolean.valueOf(setWithResolve);
            }
            if (getWithResolve != null) {
                isGetWithResolve = Boolean.valueOf(getWithResolve);
            }

            if (RESOLVE_TENANT_ID.equals(action) && tenantDomain != null) {
                privilegedCarbonContext.setTenantDomain(tenantDomain, isSetWithResolve);
                response.addHeader("resolved-tenantId",
                                   String.valueOf(privilegedCarbonContext.getTenantId(isGetWithResolve)));
            } else if (RESOLVE_TENANT_DOMAIN.equals(action) && tenantId != null) {
                privilegedCarbonContext.setTenantId(Integer.parseInt(tenantId), isSetWithResolve);
                response.addHeader("resolved-tenantDomain", privilegedCarbonContext.getTenantDomain(isGetWithResolve));
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }
}
