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

<%@ page import="org.wso2.carbon.context.RegistryType"%>
<%@ page import="org.wso2.carbon.context.CarbonContext" %>
<%@ page import="org.wso2.carbon.registry.api.Registry" %>
<%@ page import="org.wso2.carbon.registry.api.Resource" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Set Tenant Specific Values in Registry Values</title>
</head>
<body>

	<%
		CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext();
		int tenantId = cCtx.getTenantId();
		String tenantDomain = cCtx.getTenantDomain();

		//the root collection for SaaSTest registry entries . appended to registry root 

		//String stSaveResource = request.getParameter("regResourceName");
		String stRegType = request.getParameter("registryType");
		String stResourceFullName = "/SaaSTest/" + request.getParameter("regResourceName");
		String stResourceMediaType = request.getParameter("regMediaType");
		String stResourceDesc = request.getParameter("regDesc");
		String stRegContent = request.getParameter("regContent");
		String stResourcePropertyKey = request.getParameter("propertyKey");
		String stResourcePropertyValue = request.getParameter("propertyValue");

		String resourcePath = null;
		
		//set values not set, initialize default values
		if (resourcePath == null) {
			resourcePath = "/resource_fullPathFromRoot/defaultName";
		}
		if (stResourceMediaType == null) {
			stResourceMediaType = "application/json";
		}
		if (stResourceDesc == null) {
			stResourceDesc = "default resource description";
		}
		if (stRegContent == null) {
			stRegContent = "{tenantID: " + Integer.toString(tenantId) + "}";
		}
		if (stResourcePropertyKey == null) {
			stResourcePropertyKey = "SaaSTestPropertyKey";
		}
		if (stResourcePropertyValue == null) {
			stResourcePropertyValue = tenantDomain;
		}

		String action = request.getParameter("action");
		if (action != null && action.equals("add")) {
		    try {
			    // registry trees as types
			    // RegistryType.SYSTEM_CONFIGURATION,  RegistryType.SYSTEM_GOVERNANCE, USER_CONFIGURATION, USER_GOVERNANCE, LOCAL_REPOSITORY
			    Registry registry = cCtx.getRegistry(RegistryType.valueOf(stRegType));
			    // create a new resource instance
			    Resource resource = registry.newResource();
			    resource.setMediaType(stResourceMediaType);
			    resource.setDescription(stResourceDesc);
			    resource.setContent(stRegContent);
			
			    // Resource Properties are used as query parameters
			    resource.setProperty(stResourcePropertyKey,stResourcePropertyValue);
			
			    // store the resource in the registry at resourcePath
			    registry.put(stResourceFullName, resource);
		
	%>

	Saved resource at <%= stResourceFullName %>

	<%
	
		    } catch ( org.wso2.carbon.registry.core.exceptions.ResourceNotFoundException e) {
			    e.printStackTrace();
	%>
		Error: Failure to save resource at <%=stResourceFullName %>
	<% 
		    }
        }
	%>
	<form action="registry.jsp" method="POST">
		<div class="instructions">Specify context values</div>
		<table>
			<tr>
				<td colspan=2 align='center'><b>Registry Resource Object</b></td>
			</tr>
			<tr>
				<td>Registry Type</td>
				<td><select name="registryType">
						<option><%= RegistryType.SYSTEM_CONFIGURATION.toString() %></option>
						<option><%= RegistryType.SYSTEM_GOVERNANCE.toString() %></option>
						<option><%= RegistryType.USER_CONFIGURATION.toString() %></option>
						<option><%= RegistryType.USER_GOVERNANCE.toString() %></option>
						<option selected="true"><%= RegistryType.LOCAL_REPOSITORY.toString() %></option>
				</select></td>
			</tr>
			<tr>
				<td>Resource Object Name</td>
				<td><input type="text" name="regResourceName"
					value="<%=resourcePath %>" /></td>
			</tr>
			<tr>
				<td>Registry resource media type</td>
				<td><input type="text" name="regMediaType"
					value="<%=stResourceMediaType %>" /></td>
			</tr>
			<tr>
				<td>Registry resource description</td>
				<td><input type="text" name="regDesc"
					value="<%=stResourceDesc %>" /></td>
			</tr>

			<tr>
				<td>Registry content</td>
				<td><input type="text" name="regContent"
					value="<%= stRegContent %>" /></td>
			</tr>

			<tr>
				<td>Registry Property Lookup key</td>
				<td><input type="text" name="propertyKey"
					value="<%= stResourcePropertyKey %>" /></td>
			</tr>
			<tr>
				<td>Registry Property Lookup value</td>
				<td><input type="text" name="propertyValue"
					value="<%=stResourcePropertyValue %>" /></td>
			</tr>

			<tr>
				<td>&nbsp;</td>
				<td><input type="hidden" name="action" value="add"><input type="submit" value="Save Context" /></td>
			</tr>
		</table>
	</form>
 <h2>Available Actions</h2> 
	<ul>
		<li><a href="./cache.jsp">Set Tenant Specific Cache Values</a></li>
		<li><a href="./registry.jsp">Set Tenant Specific Registry Values</a></li>
		<li><a href="../test/view.jsp">View Current Tenant Identity and Context Values</a></li>
		<li><a href="../usermgt/usermgt.jsp">Manage Tenant's User Realm</a></li>
		<li><a href="../database/database.jsp">Managed Tenant Specific Database</a></li>
		<li><a href="../logout.jsp">Logout</a></li>
	</ul>

</body>
</html>