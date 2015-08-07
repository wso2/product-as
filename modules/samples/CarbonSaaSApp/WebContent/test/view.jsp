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

<%@ page import="org.wso2.appserver.samples.RegUtils" %>

<%@ page import="org.wso2.carbon.utils.multitenancy.MultitenantUtils" %>

<%@ page import="org.wso2.carbon.context.CarbonContext" %>
<%@ page import="org.wso2.carbon.context.PrivilegedCarbonContext" %>

<%@ page import="org.wso2.carbon.queuing.CarbonQueue" %>

<%@ page import="org.wso2.carbon.context.RegistryType" %>
<%@ page import="org.wso2.carbon.registry.core.RegistryConstants"%>
<%@ page import="org.wso2.carbon.registry.core.exceptions.ResourceNotFoundException"%>

<%@ page import="org.wso2.carbon.registry.api.Association" %>
<%@ page import="org.wso2.carbon.registry.api.Registry" %>
<%@ page import="org.wso2.carbon.registry.api.Collection" %>
<%@ page import="org.wso2.carbon.registry.api.Resource" %>
<%@ page import="org.wso2.carbon.registry.api.RegistryException" %>

<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>

<%@ page import="javax.cache.CacheManager"%>
<%@ page import="javax.cache.Caching"%>
<%@ page import="javax.cache.Cache"%>


<%
	// obtain the tenant context master object
CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext(); 

	// get the current domain and username from the CarbonContext 
int tenantId = cCtx.getTenantId();
String stDomain = cCtx.getTenantDomain();
String stUserName = cCtx.getUsername();

	// Obtain handle to the appropriate Tenant Registry   - default is local repository 
Registry registry = cCtx.getRegistry(RegistryType.LOCAL_REPOSITORY); 
String stRootPath = RegistryType.LOCAL_REPOSITORY.toString();

	// switch to user-defined registry tree
	// verbose method to obtain registry by comparing against custom registry type 
	String registryType = request.getParameter("registryType");
	if(registryType != null) { 
	 	registry = cCtx.getRegistry(RegistryType.valueOf(registryType));
	 	System.out.println("registry object:"+registry);
		stRootPath = registryType; 
	} 
	
	// registry resource attributes
//String stRegValue = null;      	// the resource value   [script, image, string, policy]
//String stLastModified = null;  	// when the resource was last modified
//String stCreated = null;		// when the resource was created
//String stResourceDesc = null;	// resource description
//String stMedia = null;			// resource mediat type
//String stPropertyValue = null;	// property index value
//String stPropertyName = null;	// property index name

// Registry Search Query Documentation
// http://docs.wso2.org/display/Governance411/Querying+for+Resources+By+Properties
String stRegSQL = "SELECT R.REG_PATH_ID, R.REG_NAME FROM REG_RESOURCE R, REG_PROPERTY PP, REG_RESOURCE_PROPERTY RP WHERE R.REG_PATH_ID=RP.REG_PATH_ID AND R.REG_NAME=RP.REG_RESOURCE_NAME AND RP.REG_PROPERTY_ID=PP.REG_ID AND PP.REG_NAME=?";

// default key for tenant Cache entry
String stCacheKey = null;
		
		// record counter [used to output message on records retrieved]
int iCount = 0;
%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>View SaaS Tenant Context Object(s)</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />	
</head>
<body>
<h1>Tenant View </h1>
<br/>
<form action="view.jsp">
		<table>
			<tr>	
        	<td>
        			<select name="registryType">
                    	<option><%= RegistryType.LOCAL_REPOSITORY.toString() %></option>
                    	<option><%= RegistryType.SYSTEM_CONFIGURATION.toString() %></option>
                    	<option><%= RegistryType.SYSTEM_GOVERNANCE.toString() %></option>
                    	<option><%= RegistryType.USER_CONFIGURATION.toString() %></option>
               			<option><%= RegistryType.USER_GOVERNANCE.toString() %></option>
        			</select>
     		</td>
     		<td>         
        			Registry path:<input name="ResourcePath"></input>
        	</td>
        	<td>
        			<input type="submit" value="Change Registry Context" />
        	</td>
        	</tr>
        </table>
        
</form>

<p>
	<table border=1>
		<tr><td colspan=2  align='center'><b>Root Context Objects</b></td></tr>
		<tr><td>current user name from session</td>			<td><%= session.getAttribute("adminName") %></td></tr>
		<tr><td>current user name from tenant context</td>	<td><%=stUserName%></td></tr>
		<tr><td colspan=2>&nbsp;</td></tr>

		<tr><td>current tenantID from context</td>	<td><%= tenantId %></td></tr>
		<tr><td colspan=2>&nbsp;</td></tr>

		<tr><td>tenantDomain from context</td>		<td><%= stDomain %></td></tr>
		<tr><td colspan=2>&nbsp;</td></tr>
	</table>
		
	<p>
	<table border=1>
		<tr><td colspan=2  align='center'><b>Tenant Registry</b></td></tr>	
		<tr><td>Path Root</td><td><%=stRootPath %></td></tr>	
			
		<tr><td colspan=2  align='center'>Registry Resource Objects</td></tr>
<% 
iCount = 0;
String stResourcePath = request.getParameter("ResourcePath");
if (stResourcePath == null)
		stResourcePath = "";

List<Resource> lResource = RegUtils.getResourceList(registry, stResourcePath);

if (lResource != null)
for (Resource oResource : lResource) {  	// iterate over all the registry resource paths
	iCount++;  // increment registry resource counter 

 	String stPath = oResource.getPath(); 								// obtain the full resource path
	String stLastModified = oResource.getLastModified().toString();		// when the resource was last modified
	String stCreated = oResource.getCreatedTime().toString();			// when the resource was created
	String stResourceDesc = oResource.getDescription();					// resource description
	String stMedia = oResource.getMediaType();  						// resource media type

		// obtain the saved registry content as a string. will only work for text based content
 	Object oObjectR = oResource.getContent();							// resource content[script, image, string, policy]. could be binary
 	String stRegContent = "";
	if(oObjectR != null){
		if (oObjectR.getClass() == String.class )
			stRegContent = (String)oObjectR;
		else
			stRegContent = new String((byte[])oObjectR);
	}
%>
		<tr><td colspan=2  align='center'>Information stored for registry path <b><%=stPath %></b></td></tr>
		<tr><td>registry resource description:</td>		<td><%=stResourceDesc %></td></tr>
		<tr><td>registry resource type:</td>			<td><%=stMedia %></td></tr>
		<tr><td>registry content</td>					<td><%=stRegContent %></td></tr>
		<tr><td>registry resource created:</td>			<td><%=stCreated %></td></tr>
		<tr><td>registry resource updated:</td>			<td><%=stLastModified %></td></tr>
		<tr><td colspan=2  align='center'>Resource Properties</td></tr>
<%
 	// output properties associated with the resource
	Properties oProperties = oResource.getProperties();
	// Enumeration<?> eProp = (Enumeration<?>)oProperties.propertyNames();
    for (Object stPropName : oProperties.keySet()) {
		String stPropValue = oResource.getProperty((String)stPropName);		
%>
		<tr><td>registry resource property name:</td>	<td><%=stPropName %></td></tr>
		<tr><td>registry resource property value:</td>	<td><%=stPropValue %></td></tr>
<% 
	}   // end of loop through properties
	
%>
		<tr><td colspan=2  align='center'>Resource Associations</td></tr>
<% 
    Association[] arAssociation = registry.getAssociations(oResource.getPath(), "Documentation");
    for (Association oAssociation : arAssociation) {
    	String stAssociationType 		= oAssociation.getAssociationType();
    	String stAssociationDestPath 	= oAssociation.getDestinationPath();
    	String stAssociationSourcePath 	= oAssociation.getSourcePath();
 %>
 		<tr><td>Association Type</td><td><%=stAssociationType %></td></tr>
 		<tr><td>Association Destination Path</td>	<td><%=stAssociationDestPath %></td></tr>
 		<tr><td>Association Source Path</td>		<td><%=stAssociationSourcePath %></td></tr>
 <% 
	}  // end of loop through associations

%>
    <tr><td colspan=2>&nbsp;</td></tr>
<% 
}	// end of loop through resources
%>
	
<% 
		// output message on non-existing resources
if (iCount == 0) { %>
	<tr><td colspan=2 align='center'>No registry resource(s) found&nbsp;&nbsp;</td></tr>
	<% }
	else {	%>
		<tr><td colspan=2 align='center'>Registry entry count=<%=iCount %></td></tr>
	<%	}   %>
</table>

<p/>
<table border=1>	
	<tr><td colspan=2><b>Output Tenant Cache Items</b></td></tr>
	<tr><td>Cache Key</td>	<td>Cache Value</td></tr>
	
	<%
	String stCacheValue = null;
	CacheManager cacheManager =   Caching.getCacheManagerFactory().getCacheManager("tsampleCacheManager");
    Cache<String,String> oCache = cacheManager.getCache("sampleCache");	
 	
    // obtain iterator holding keys for cache entries
 	Iterator<?> iCacheKeys = (Iterator<?>)oCache.keys();
	iCount = 0;  						// count number of cache name-value pairs found
	while (iCacheKeys.hasNext() )  {
		stCacheKey = (String)iCacheKeys.next();  // get the next cache key
		stCacheValue = null;  			// reset value string 
		iCount++;  					 	// increment the cache entry counter 
										// get Cache.entry value by key
		Object oObjectC = oCache.get(stCacheKey);
		if (oObjectC != null) {
			if (oObjectC.getClass() == String.class )
				stCacheValue = (String)oObjectC;
			else {
				stCacheValue = (String)oObjectC.toString();
			}   // end  if-else class == string
		}  // end of if oObjectC != null
	%>
	<tr><td><%=stCacheKey%></td>	<td><%=stCacheValue %></td></tr>
	
	<% 
	} // end while loop across cache items	

	// output non-existent cache entries
	if (iCount == 0) { %>
		<tr><td colspan=2 align='center'>No cache name-value pairs found. </td></tr>
	<% 	}
	else {	%>
		<tr><td colspan=2 align='center'>Cache Object Count:&nbsp;<%=iCount %></td></tr>
	<% 	}	%>
</table>
	
	<p>
	<table border=1>
			<tr><td colspan=2 align='center'><b>Dump Session Object</b></td></tr>	
			<tr><td align='center'><b>Key</b></td>	<td align='center'><b>Value</b></td></tr>
		<%
		 iCount = 0;
         Enumeration<String> keys=session.getAttributeNames();
                while(keys.hasMoreElements())   {
                    String name=(String)keys.nextElement();
                    String value=""+session.getAttribute(name);
                    iCount++;
  		%>
  					<tr>
  						<td><%=name%></td>	<td><%=value%></td>
				  	</tr>
		<%     	 }   // end of while loop    
		if (iCount == 0) { %>
			<tr><td colspan=2 align='center'>No session objects found</td></tr>
		<%
		}    // end of iCount check for no session objects
		%>			  	
	</table>
	
<p/>
	<table border=1>
		<tr><td colspan=2 align='center'><b>Dump request headers</b></td></tr>
		<tr><td align='center'>Header Key Name</td><td align='center'>Header Value</td></tr>
			<% 	
			Enumeration<String> headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					String headerValue = request.getHeader(headerName);		
			%>		
					<tr>
						<td><%=headerName%></td>	<td><%=headerValue%></td>
					</tr>
		<%			}   %>
	</table>


<table>
<tr><td>Q Position</td><td>Queue Object</td></tr>
<%
/*
CarbonQueue<String> oQueue = (CarbonQueue<String>)cCtx.getQueue("testQ");
int iQPosition = 1;
if (oQueue != null)
	while(oQueue.isEmpty() == false) {
		String stQValue = oQueue.pop();	
*/
		%>
		<tr><td><% /* =iQPosition */ %></td><td><% /* =stQValue */%></td></tr>
		<% 
/*	} */
%>
</table>

//TODO: get JNDI context
// Context getJNDIContext(Hashtable properties)
// Context getJNDIContext()

<%
/*
only available in registry distribution, and not in application server distribution ?

org.wso2.carbon.governance.*

org.wso2.carbon.governance.api.generic.GenericArtifactManager
org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact

 page import="org.wso2.carbon.governance.api.generic.GenericArtifactManager"
 page import="org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact"
 
 page import="javax.xml.namespace.QName"

 page import="org.wso2.carbon.governance.api.generic.GenericArtifactManager" 
 page import="org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact" 
 page import="org.wso2.carbon.governance.api.util.GovernanceUtils"

 page import="org.wso2.carbon.registry.core.pagination.PaginationContext" 
 page import="org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient" 

*/
%>


<% 
//alternative registry search

// alternative registry search

/*
Registry oRegistry = cCtx.getRegistry(RegistryType.LOCAL_REPOSITORY); 
String stSearchPath = RegistryType.LOCAL_REPOSITORY.toString();

// Should be load the governance artifact. 
GovernanceUtils.loadGovernanceArtifacts((Registry) oRegistry); 
addServices(gov); //Initialize the pagination context.

// add services
GenericArtifactManager artifactManager = new GenericArtifactManager(oRegistry, "service");
GenericArtifact artifact = artifactManager.newGovernanceArtifact(new QName("ns", "FlightService" + i));
artifactManager.addGenericArtifact(artifact);


//Top five services, sortBy name , and sort order descending. 
PaginationContext.init(0, 5, "DES", "overview_name", 100); 

WSRegistrySearchClient wsRegistrySearchClient =
new WSRegistrySearchClient(serverURL, username, password,
configContext); 

//This should be execute to initialize the AttributeSearchService. wsRegistrySearchClient.init(); 
//Initialize the GenericArtifactManager 
GenericArtifactManager artifactManager =new GenericArtifactManager(gov, "service"); 
Map<String, List<String>> listMap = new HashMap<String, List<String>>(); //Create the search attribute map

*/
%>



<h2>Available Navigation Actions</h2>
	<ul>
		<li><a href="../context/context.jsp">Set Context Values</a></li>
		<li><a href="../database/database.jsp">Managed tenant specific database</a></li>
		<li><a href="../usermgt/usermgt.jsp">Manage Tenant's User Realm</a></li>
		<li><a href="../logout.jsp">Logout current user</a></li>
	</ul>

</body>
</html>

