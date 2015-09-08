<!--
~ Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
~
~ WSO2 Inc. licenses this file to you under the Apache License,
~ Version 2.0 (the "License"); you may not use this file except
~ in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing,
~ software distributed under the License is distributed on an
~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
~ KIND, either express or implied. See the License for the
~ specific language governing permissions and limitations
~ under the License.
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="org.wso2.carbon.utils.multitenancy.MultitenantUtils" %>
<%@ page import="org.wso2.appserver.samples.TenantUtils" %>

<%
	String fq_username = request.getRemoteUser();
	System.out.println("username :"+ fq_username);
	System.out.println("tenantId-before- "+ session.getAttribute("tenantID"));
	String tenantDomain = null;
	int tenantID = 0;
	if (fq_username != null)  {
		 // lookup the tenant domain associated with the user
		 tenantDomain = MultitenantUtils.getTenantDomain(fq_username);

		 // given the tenant domain, brain the tenant ID
		 tenantID = TenantUtils.getTID(tenantDomain);

		// set tenant information in session for later comparison
		session.setAttribute("tenantID", tenantID);
		session.setAttribute("tenantDomain", tenantDomain);
		session.setAttribute("adminName", fq_username);
		System.out.println("tenantId-after- "+ session.getAttribute("tenantID"));
	}
	%>
