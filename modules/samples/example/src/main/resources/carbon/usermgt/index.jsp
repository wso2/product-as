<%@page import="org.wso2.carbon.user.api.UserRealm" %>
<%@page import="org.wso2.carbon.context.CarbonContext" %>
<html>
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
<head></head>
<body>
<h2>WSO2 Carbon User Manage Usage Demo</h2>

<%
    if (request.getParameter("add") != null) {
        String username = request.getParameter("username");
        String pass = request.getParameter("password");
        if (username != null && username.trim().length() > 0) {
            CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
            UserRealm realm = context.getUserRealm();
            if (!realm.getUserStoreManager().isExistingUser(username)) {
                realm.getUserStoreManager().addUser(username, pass, null, null, null);
            } else {
%> <p><b>The user <%=username%> already exists</b></p> <%
            }
        }
    } else if (request.getParameter("delete") != null) {
        String username = request.getParameter("username");
        if (username != null && !username.trim().isEmpty()) {
            CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
            UserRealm realm = context.getUserRealm();
            if (realm.getUserStoreManager().isExistingUser(username)) {
                realm.getUserStoreManager().deleteUser(username);
            } else {
%> <p><b>The user <%=username%> does not exists</b></p> <%
            }
        }
    }
%>

<h3>Add User</h3>
<p>
<form action="index.jsp" method="POST">
    <table>
        <tr>
            <td>Username</td>
            <td><input type="text" name="username"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="text" name="password"/></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="Add" name="add"/></td>
        </tr>
    </table>
</form>
</p>
</hr>

<h3>Delete User</h3>
<p>
<form action="index.jsp" method="POST">
    <table>
        <tr>
            <td>Username</td>
            <td><input type="text" name="username"/></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="Delete" name="delete"/></td>
        </tr>
    </table>
</form>
</p>
</hr>

<h3>The user list</h3>
<ol>
<%
    CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
    UserRealm realm = context.getUserRealm();
    String[] names = realm.getUserStoreManager().listUsers("*", 100);
    for (String name : names) {
%><li><%=name%></li><%
    }
%>
</ol>
</body>
</html>
