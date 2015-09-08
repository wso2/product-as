package org.wso2.appserver.samples;/*
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

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.TenantManager;
import org.wso2.carbon.user.api.UserRealmService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init method");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        System.out.println("Login filter pre");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        System.out.println("Login filter post");
        /*String tenantDomain = MultitenantUtils.getTenantDomain(((HttpServletRequest) servletRequest).getRemoteUser());
        request.getSession(false).setAttribute(MultitenantConstants.TENANT_DOMAIN, tenantDomain);
        UserRealmService realmService =
                (UserRealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                                                          .getOSGiService(UserRealmService.class, null);
        TenantManager tenantManager = realmService.getTenantManager();
        try {
            request.getSession(false)
                   .setAttribute(MultitenantConstants.TENANT_ID, tenantManager.getTenantId(tenantDomain));
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
        request.getSession(false).setAttribute("adminName", ((HttpServletRequest) servletRequest).getRemoteUser());*/
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        System.out.println("destroy method");
    }
}
