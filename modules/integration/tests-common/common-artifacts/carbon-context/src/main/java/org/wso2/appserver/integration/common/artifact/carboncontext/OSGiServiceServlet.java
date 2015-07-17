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

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.tomcat.api.CarbonTomcatService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OSGiServiceServlet extends HttpServlet {
	private static final String CARBON_CONTEXT = "cc";
	private static final String PRIVILEGE_CARBON_CONTEXT = "pcc";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	private void doProcess(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getParameter("action");
		Object osGiService = null;
		if (CARBON_CONTEXT.equalsIgnoreCase(action)) {
			osGiService = CarbonContext.getThreadLocalCarbonContext().getOSGiService(CarbonTomcatService.class, null);
		} else if (PRIVILEGE_CARBON_CONTEXT.equalsIgnoreCase(action)) {
			osGiService = PrivilegedCarbonContext.getThreadLocalCarbonContext()
			                                     .getOSGiService(CarbonTomcatService.class, null);
		}
		if (osGiService != null && osGiService instanceof CarbonTomcatService) {
			CarbonTomcatService carbonTomcatService = (CarbonTomcatService) osGiService;
			response.addHeader("tomcat-service-name", carbonTomcatService.getTomcat().getService().getName());
		}
	}
}
