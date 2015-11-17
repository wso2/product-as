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
<%@ page import="org.wso2.carbon.context.CarbonContext" %>
<%@ page import="org.wso2.carbon.user.api.UserRealm" %>
<%@ page import="org.wso2.carbon.user.api.UserStoreException" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Arrays"%>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String role = request.getParameter("role");
    boolean status = false;

    try {
        CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
        UserRealm realm = context.getUserRealm();
        if (role == null) {
            if (username != null && username.trim().length() > 0) {
                status = realm.getUserStoreManager().authenticate(username, password);
            }
        } else {
            if (username != null && username.trim().length() > 0) {
                status = Arrays.asList(realm.getUserStoreManager().getRoleListOfUser(username)).contains(role);
            }
        }
    } catch (UserStoreException e) {
        e.printStackTrace();
    }

    if (status) {
        session.setAttribute("logged-in", "true");
        session.setAttribute("username", username);
        if (role != null) {
            session.setAttribute("logged-in-with-role", "true");
        }
        response.sendRedirect("index.jsp");
    } else {
        session.invalidate();
        response.sendRedirect("index.jsp?failed=true");
    }
%>