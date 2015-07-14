<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.wso2.carbon.user.api.UserRealm"%>
<%@page import="org.wso2.carbon.context.CarbonContext"%>
<%@page import="org.wso2.carbon.user.api.UserRealmService" %>
<%@page import="org.wso2.carbon.context.PrivilegedCarbonContext" %>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Tenant User Realm Management Page - Store and Access</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/styles.css" />	
	
</head>
<body>

<%
String stAction = request.getParameter("action");
String stUser = null;

if (stAction != null) {
	 stUser = request.getParameter("userId");	
%>
	Deleted id: <%=stUser %><br/>
<% 
}
%>
<%
CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext(); 
String stDomain = cCtx.getTenantDomain();
UserRealmService realmService = (UserRealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(UserRealmService.class);			
int tenantID = (Integer) session.getAttribute("tenantID");

String username = request.getParameter("username");
String pass = request.getParameter("password");
String[] user_role = new String[1];
user_role[0] = request.getParameter("role");

if (stUser != null) {
	realmService.getTenantUserRealm(tenantID).getUserStoreManager().deleteUser(stUser);
}

%>
<h1>Tenant Delegated user Management </h1>

	<%
		if (username != null && username.trim().length() > 0) {
			if (!realmService.getTenantUserRealm(tenantID).getUserStoreManager().isExistingUser(username)) {
				realmService.getTenantUserRealm(tenantID).getUserStoreManager().addUser(username, pass, user_role, null, null);
			} 
			else {
				%>
					<p><b>The user <%=username%>@<%=stDomain%> already exists</b></p>
				<%
				}
		}
	%>

	
	<form action="usermgt.jsp">
	<div class="instructions">Add a user to the User Realm</div>
		<table>
			<tr>
				<td>Username</td>
				<td><input type="text" name="username" /></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="text" name="password" /></td>
			</tr>
			<tr><td>
				<% 
				String[] aRoles = realmService.getTenantUserRealm(tenantID).getUserStoreManager().getRoleNames();
				%>
				
				Associate Role</td>
				<td>
				<select name='role'>
				<%
				for (String oRole : aRoles) {
				%>
					<option value="<%=oRole%>"><%=oRole%></option>
				<%
				}
				%>
				</select>
				</td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value="Add" /></td>
			</tr>
		</table>
	</form>
<hr>
	<p><b>The user list for tenant domain @<%=stDomain%></b></p>
		<table border=1>
		<%
    	realmService = (UserRealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(UserRealmService.class);			
		String[] aUserNames = realmService.getTenantUserRealm(tenantID).getUserStoreManager().listUsers("*", 100);
		for (String stName : aUserNames) {
			String[] aUserRoles = realmService.getTenantUserRealm(tenantID).getUserStoreManager().getRoleListOfUser(stName);
		%>
			<tr>
				<td><%=stName%>@<%=stDomain%></td>
				<td><b>Roles:</b>	 
					<% 
					if (aUserRoles != null)
						for (String stRole : aUserRoles) {   
					%>
							<%=stRole%>,
					<%   }  %>
				</td>
				<td><a href="./usermgt.jsp?userId=<%=stName%>&action=delete">Delete</a></td>
			</tr>
		<% 
		}
		%>
		</table>
		
		
		<h2>Available Navigation Actions</h2>
	<ul>
		<li><a href="../context/context.jsp">Set Context Values</a></li>
		<li><a href="../test/view.jsp">View current tenant identity and context values</a></li>
		<li><a href="../database/database.jsp">Managed tenant specific database</a></li>
		<li><a href="../logout.jsp">Logout current user</a></li>
	</ul>
<hr>
		
</body>
</html>
