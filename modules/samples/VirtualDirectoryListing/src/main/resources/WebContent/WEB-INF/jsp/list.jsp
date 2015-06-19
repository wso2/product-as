<!--
~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
<%@ page import="java.io.File" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@page contentType="text/html" %>
<table class="styledLeft" id="listingT">
    <thead>
        <th><h1>Directory Listing..</h1></th>
    </thead>
    <tbody>

    <%
        String subPath = (String) request.getAttribute("subPath");
        String contextPath = request.getContextPath();
        String virtualDirectoryPath = (String) request.getAttribute("virtualDirectoryPath");
        String filePath = virtualDirectoryPath + subPath;
        String relativePath;

        if(!"".equals(subPath)){
            relativePath = contextPath + "/" + subPath;
            String parent = relativePath.substring(0, relativePath.lastIndexOf("/"));
            %>
            <tr>
                <td><h3><a href="<%=parent%>">ParentDirectory</a></h3></td>
            </tr>
            <%
        } else {
            relativePath = contextPath;
        }

        File file = new java.io.File(filePath);
        String[] list = file.list();
        List<String> filesList = Arrays.asList(list);
        Collections.sort(filesList);
        for (int i = 0; i < list.length; i++) {
            %>
            <tr>
                <td><a href="<%=relativePath%>/<%=list[i]%>"><%=list[i]%></a></td>
            </tr>
            <%
        }
    %>
    </tbody>
</table>
