<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="org.wso2.carbon.utils.multitenancy.MultitenantUtils" %>
<%@ page import="org.wso2.appserver.samples.TenantUtils" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>WELCOME To the SaaS Multi-Tenant Demo WebApp</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />	
</head>
<body>
<center>
<h1>SaaS Multi-Tenancy Powered by</h1> 
<img src="<%= request.getContextPath() %>/images/carbon_logo_h42.gif" alt="image"/></center>
<P>

<a href="./docs/docs.jsp"  target="_blank">Reference: SaaS Test App Configuration and Manual</a>

	<%
	String fq_username = request.getRemoteUser();
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
	}
	%>
	<P>
	<div class="instructions">
	You are logged in as <b><%=fq_username%></b> in domain <b><%=tenantDomain%></b> with tenant ID - <b><%=tenantID %></b>
	</div>
	<br/>
	
	<P>
	
	 <h2>Available Actions</h2> 
	<ul>
		<li><a href="./context/cache.jsp">Set Tenant Specific Cache Values</a></li>
		<li><a href="./context/registry.jsp">Set Tenant Specific Registry Values</a></li>
		<li><a href="./test/view.jsp">View Current Tenant Identity and Context Values</a></li>
		<li><a href="./usermgt/usermgt.jsp">Manage Tenant's User Realm</a></li>
		<li><a href="./database/database.jsp">Managed Tenant Specific Database</a></li>
		<li><a href="./logout.jsp">Logout</a></li>
	</ul>
</body>
</html>