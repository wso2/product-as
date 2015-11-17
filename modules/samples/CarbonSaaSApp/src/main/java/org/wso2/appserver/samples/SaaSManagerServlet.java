package org.wso2.appserver.samples;
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

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SaaSManagerServlet extends HttpServlet {

    static final long serialVersionUID = 1;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String fq_username = request.getRemoteUser();
        int tenantID = 0;

        try {
            // convert tenant domain into tenant id
            tenantID = TenantUtils.getTID(MultitenantUtils.getTenantDomain(fq_username));
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(fq_username);
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
        // forward the tenant identifier
        request.setAttribute("tenantID", tenantID);
    }
}
