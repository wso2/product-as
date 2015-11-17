<!--
 ~ Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@page import="org.wso2.carbon.user.api.UserRealm" %>
<%@page import="org.wso2.carbon.context.CarbonContext" %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.wso2.carbon.user.api.Permission"%>
<html>
<head></head>
<body>
<h2>WSO2 Carbon Role Manage Usage Demo</h2>

<%
    CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
    UserRealm realm = context.getUserRealm();
    String username = request.getParameter("username");

    if (request.getParameter("add") != null || request.getParameter("remove") != null) {
        String role = request.getParameter("role");
        if (username != null && username.trim().length() > 0) {
            if (realm.getUserStoreManager().isExistingUser(username)
                      && realm.getUserStoreManager().isExistingRole(role)) {
                List<String> rolesList =
                         new ArrayList<String>(Arrays.asList(realm.getUserStoreManager().getRoleListOfUser(username)));
                if (request.getParameter("add") != null) {
                    if (!rolesList.contains(role)) {
                        realm.getUserStoreManager().updateRoleListOfUser(username, new String[]{}, new String[]{role});
                    } else {
%> <p><b>The user <%=username%> already have <%=role%> </b></p> <%
                    }
                } else {
                    if (rolesList.contains(role)) {
                        realm.getUserStoreManager().updateRoleListOfUser(username, new String[]{role}, new String[]{});
                    } else {
%> <p><b>The user <%=username%> does not have <%=role%> </b></p> <%
                    }
                }
            } else {
%> <p><b>The user <%=username%> or <%=role%> does not exist</b></p> <%
            }
        }
    } else if (request.getParameter("view") != null) {
        String[] rolesList = realm.getUserStoreManager().getRoleListOfUser(username);
%><b>Roles of <%=username%></b><ul><%
        for (String role : rolesList) {
%><li><%=role%></li><%
        }
    }
%>
</ul>

<h3>Add/Remove Role</h3>
<p>
<form action="index.jsp" method="POST">
    <table>
        <tr>
            <td>Username</td>
            <td><input type="text" name="username"/></td>
        </tr>
        <tr>
            <td>Role</td>
            <td><input type="text" name="role"/></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <input type="submit" value="Add" name="add"/>
                <input type="submit" value="Remove" name="remove"/>
            </td>
        </tr>
    </table>
</form>
</p>
</hr>

<h3>Get User Roles</h3>
<p>
<form action="index.jsp" method="POST">
    <table>
        <tr>
            <td>Username</td>
            <td><input type="text" name="username"/></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <input type="submit" value="View" name="view"/>
            </td>
        </tr>
    </table>
</form>
</p>
</hr>


<h3>The Role list</h3>
<ol>
<%
    String[] roles = realm.getUserStoreManager().getRoleNames();
    for (String role : roles) {
%><li><%=role%></li><%
    }
%>
</ol>
</body>
</html>
