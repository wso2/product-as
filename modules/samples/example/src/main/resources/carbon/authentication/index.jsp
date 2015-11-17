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
<h2>WSO2 Carbon Authentication Demo</h2>

<p>
    This demo shows how to integrate your Web application with the Carbon authentication mechanism.
</p>
<%
    Object failed = request.getParameter("failed");
    if (failed != null) {
        response.addHeader("logged-in", "false");
%>
<p><font color="red">Login Failed!</font></p>
<%
    }
%>
<%
    if (request.getParameter("logout") == null && session.getAttribute("logged-in") != null) {
        String userName = (String) session.getAttribute("username");
        response.addHeader("logged-in", "true");
        response.addHeader("username", userName);
        response.addHeader("logged-in-with-role", (String) session.getAttribute("logged-in-with-role"));
%>
<p>
    Welcome <%= userName %>&nbsp;<a href="index.jsp?logout=true">Logout</a>
</p>
<%
        return;
    } else {
        if (request.getParameter("logout") != null) {
            session.invalidate();
            response.addHeader("logged-out", "true");
        }
%>
<form action="login.jsp" name="loginFrm" method="POST">
    <table>
        <tr>
            <td>Username</td>
            <td><input type="text" name="username"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="password" name="password"/></td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="Login"/></td>
        </tr>
    </table>
</form>
<%
    }
%>