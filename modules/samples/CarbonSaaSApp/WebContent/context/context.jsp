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

<%@ page import="org.wso2.carbon.context.CarbonContext" %>
<%@ page import="org.wso2.carbon.context.PrivilegedCarbonContext" %>

<%@ page import="org.wso2.carbon.user.api.UserRealm" %>
<%@ page import="org.wso2.carbon.user.api.UserStoreException" %>
<%@ page import="org.wso2.carbon.user.api.UserRealmService" %>
<%@ page import="org.wso2.carbon.user.api.TenantManager" %>

<%@ page import="org.wso2.carbon.registry.api.Registry" %>
<%@ page import="org.wso2.carbon.registry.api.Resource" %>
<%@ page import="org.wso2.carbon.registry.api.RegistryException" %>
<%@ page import="org.wso2.carbon.context.RegistryType" %>

<%@ page import="org.wso2.carbon.queuing.CarbonQueue" %>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Set Tenant Values in Context Object</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/styles.css" />	
</head>
<body>

<%

    CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext();
    int tenantId = cCtx.getTenantId() ;
    String tenantDomain = cCtx.getTenantDomain() ;

    // the root collection for SaaSTest registry entries . appended to registry root
    String stRegistryStore = "/SaaSTest/";
    String stResourceFullName = stRegistryStore + request.getParameter("regResourceName");
    String stSaveResource = request.getParameter("regResourceName");

	// default assignments for keys and values
    String stRegType = null;
    String resourcePath = null;
    String stRegContent = null;
    String stResourcePropertyKey = null;
    String stResourcePropertyValue = tenantDomain;
    String stResourceDesc = null;
    String stResourceMediaType = null;
    String stCacheKey = null;
    String stCacheValue =  null;

	// get the user defined keys and user defined values to store in the context

	
	stResourcePropertyKey = request.getParameter("propertyKey");
	stResourcePropertyValue = request.getParameter("propertyValue");
	
	stResourceMediaType = request.getParameter("regMediaType");
	stResourceDesc = request.getParameter("regDesc");

	stRegContent = 	request.getParameter("regContent");
	stRegType = request.getParameter("registryType");
	if (stRegType == null)
			 stRegType = RegistryType.LOCAL_REPOSITORY.toString();

	stCacheKey = request.getParameter("cachekey");
	if (stCacheKey != null) {
		stCacheKey = stCacheKey.trim();
		if (stCacheKey.length() == 0) {
			stCacheKey = null;
	    }
	}
	
	stCacheValue = request.getParameter("cachevalue");
	if (stCacheValue != null) {
		stCacheValue = stCacheValue.trim();
		if (stCacheValue.length() == 0) {
			stCacheValue = null;
		}
	}

	
if (stSaveResource != null) {
	try {
		// registry trees as types
		// RegistryType.SYSTEM_CONFIGURATION,  RegistryType.SYSTEM_GOVERNANCE, USER_CONFIGURATION, USER_GOVERNANCE, LOCAL_REPOSITORY
		Registry registry = cCtx.getRegistry(RegistryType.valueOf(stRegType)); 
		 
		// create a new resource instance
		Resource resource = registry.newResource();
			
		// initialize resource description, content, and media type
		resource.setDescription(stResourceDesc);
		resource.setMediaType(stResourceMediaType);
		resource.setContent(stRegContent); 
		
		// Resource Properties are used as query parameters 
		resource.setProperty(stResourcePropertyKey,stResourcePropertyValue);  
		
		// store the resource in the registry at resourcePath
		registry.put(stResourceFullName, resource);
		%>
		Saved resource at <%=stResourceFullName %>
		<% 
	}
	catch ( org.wso2.carbon.registry.core.exceptions.ResourceNotFoundException e) {
		e.printStackTrace();
		%>
		Error: Failure to save resource at <%=stResourceFullName %>
		<% 
	}
} // end if save action

if (stCacheKey != null) {	
    // set Cache value in tenant context
    cCtx = CarbonContext.getThreadLocalCarbonContext();
    //cCtx.getCache().put(stCacheKey, stCacheValue);
	%>
	<%="<br>Saved cache pair "+stCacheKey+"="+stCacheValue %>
<%
}
%>


<% 
// set values not set, initialize default values
if (stRegType == null)
    stRegType = RegistryType.LOCAL_REPOSITORY.toString();
if (resourcePath == null)
    resourcePath = "/resource_fullPathFromRoot/defaultName";
if (stRegContent == null)
    stRegContent = "{tenantID: "+ Integer.toString(tenantId) + "}";
if (stResourcePropertyKey == null)
    stResourcePropertyKey = "SaaSTestPropertyKey";
if (stResourcePropertyValue == null)
    stResourcePropertyValue = tenantDomain;
if (stResourceDesc == null)
    stResourceDesc = "default resource description";
if (stResourceMediaType == null)
    stResourceMediaType = "application/json";
if (stCacheKey == null)
    stCacheKey = "tenantCache";
if (stCacheValue == null)
    stCacheValue =  tenantDomain + ":" + tenantId;
%>

<h1>Initialize Tenant Specific Objects</h1>
<form action="context.jsp">
	<div class="instructions">Specify context values</div>
		<table>
			<tr><td colspan=2 align='center'><b>Registry Resource Object</b></td></tr>
			<tr>
			    <td>Registry Type</td>
            <td>
                <select name="registryType">
                    <option ><%= RegistryType.SYSTEM_CONFIGURATION.toString() %></option>
                    <option><%= RegistryType.SYSTEM_GOVERNANCE.toString() %></option>
                    <option><%= RegistryType.USER_CONFIGURATION.toString() %></option>
               		<option><%= RegistryType.USER_GOVERNANCE.toString() %></option>
                    <option selected="true"><%= RegistryType.LOCAL_REPOSITORY.toString() %></option>
                </select>
            </td>
			</tr>
			<tr>
				<td>Resource Object Name</td>
				<td><input type="text" name="regResourceName" value="<%=resourcePath %>"/></td>
			</tr>
			<tr>
				<td>Registry resource media type</td>
				<td><input type="text" name="regMediaType" value="<%=stResourceMediaType %>"/></td>
			</tr>
			<tr>
				<td>Registry resource description</td>
				<td><input type="text" name="regDesc" value="<%=stResourceDesc %>"/></td>
			</tr>
			
			<tr>
				<td>Registry content</td>
				<td><input type="text" name="regContent" value="<%=stRegContent %>"/></td>
			</tr>
			
			<tr>
				<td>Registry Property Lookup key</td>
				<td><input type="text" name="propertyKey" value="<%=stResourcePropertyKey %>"/></td>
			</tr>
			<tr>
				<td>Registry Property Lookup value</td>
				<td><input type="text" name="propertyValue" value="<%=stResourcePropertyValue %>"/></td>
			</tr>

			<tr><td colspan=2>&nbsp;</td></tr>

			<tr><td colspan=2  align='center'><b>Specify Cache Entry Object</b></td></tr>
			<tr>
				<td>Cache key</td>
				<td><input type="text" name="cachekey" /></td>
			</tr>
			<tr>
				<td>Cache value</td>
				<td><input type="text" name="cachevalue" /></td>
			</tr>

			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value="Save Context" /></td>
			</tr>
		</table>	
	</form>

<%
/* 
CarbonQueue<String> oQueue = (CarbonQueue<String>)cCtx.getQueue("testQ");
if (oQueue != null) {
	oQueue.push("queue tenant domain and id");
	oQueue.push(tenantDomain);
	oQueue.push(Integer.toString(tenantId));
}
*/

// Context getJNDIContext(Hashtable properties)
// Context getJNDIContext()
%>

<p>	
	<div class="instructions">
		After you save the keys and values in the context objects, <a href="../test/view.jsp">View current tenant identity and context values</a>
	</div>
	<br/>
		 <h2>Available Actions ---- <%=tenantId%>></h2> 
	<ul>
		<li><a href="../test/view.jsp">View current tenant identity and context values</a></li>
		<li><a href="../database/database.jsp">Managed tenant specific database</a></li>
		<li><a href="../usermgt/usermgt.jsp">Manage Tenant's User Realm</a></li>
		<li><a href="../logout.jsp">Logout current user</a></li>
	</ul>
</body>
</html>


