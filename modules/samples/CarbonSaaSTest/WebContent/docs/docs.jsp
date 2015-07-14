<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>WELCOME To the Documentation Page for SaaS Multi-Tenant Demo WebApp</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/styles.css" />	
</head>
<body>

<table>
<tr><td><b>Key Resources</b>
</td></tr>
<tr><td>
	<A href="http://github.com/cobiacomm">Project on GitHub</A>
</td></tr>
<tr><td>
	<A href="http://blog.cobia.net/cobiacomm">Blog Post</A>
</td></tr>
<tr><td>
	<A href="http://github.com/cobiacomm">Installation Instructions</A>
</td></tr>
<tr><td>
	<A href="./db-setup.sql">Database Setup SQL Script</A>
</td></tr>
<tr><td>
	<A href="http://docs.wso2.org/display/Governance411/Querying+for+Resources+By+Properties">Registry Search Query Documentation</A>
</td></tr>
<tr><td>
	<A href="https://github.com/wso2/kernel/blob/master/core/javax.cache/src/main/java/javax/cache/Cache.java">Cache.java</A>
<tr><td>
	<A href="https://github.com/wso2/kernel/blob/master/core/org.wso2.carbon.user.api/src/main/java/org/wso2/carbon/user/api/UserStoreManager.java">User Store Manager</A>
</td></tr>
<tr><td>
	<A href="https://github.com/wso2/kernel/blob/master/core/org.wso2.carbon.registry.core/src/main/java/org/wso2/carbon/registry/core/Collection.java">Resource.java</A>
</td></tr>
</table>
<P/>
<table>
<tr><td><b>Key Carbon Multi-Tenant Framework Classes</b></td></tr>
<tr><td>import="org.wso2.carbon.context.CarbonContext" </td></tr>
<tr><td>import="org.wso2.carbon.user.api.UserRealm" </td></tr>
<tr><td>import="org.wso2.carbon.user.api.UserStoreException" </td></tr>
<tr><td>import="org.wso2.carbon.context.PrivilegedCarbonContext" </td></tr>
<tr><td>import="org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils" </td></tr>
<tr><td>import="org.wso2.carbon.user.api.UserRealmService" </td></tr>
<tr><td>import="org.wso2.carbon.user.api.TenantManager" </td></tr>
<tr><td>import="org.wso2.carbon.core.services.util.CarbonAuthenticationUtil"</td></tr> 

<tr><td>import="org.wso2.carbon.context.CarbonContext" </td></tr>
<tr><td>import="org.wso2.carbon.context.PrivilegedCarbonContext" </td></tr>
<tr><td>import="org.wso2.carbon.registry.api.Registry" </td></tr>
<tr><td>import="org.wso2.carbon.registry.api.Resource" </td></tr>
<tr><td>import="org.wso2.carbon.registry.api.RegistryException" </td></tr>
<tr><td>import="org.wso2.carbon.context.RegistryType" </td></tr>
</table>

	<h2>Available Navigation Actions</h2>  
	<ul>
		<li><a href="/context/context.jsp">Set Tenant Specific Context Values</a></li>
		<li><a href="/test/view.jsp">View Current Tenant Identity and Context Values</a></li>
		<li><a href="/usermgt/usermgt.jsp">Manage Tenant's User Realm</a></li>
		<li><a href="/database/database.jsp">Managed Tenant Specific Database</a></li>
		<li><a href="/logout.jsp">Logout</a></li>
	</ul>
</body>
</html>