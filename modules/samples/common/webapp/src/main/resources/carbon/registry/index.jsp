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
<%@ page import="org.wso2.carbon.context.CarbonContext" %>
<%@ page import="org.wso2.carbon.context.RegistryType" %>
<%@ page import="org.wso2.carbon.registry.api.Registry" %>
<%@ page import="org.wso2.carbon.registry.api.Resource" %>

<h2>WSO2 Carbon Registry Usage Demo</h2>

<hr/>
<p>

<h3>Add New Resource</h3>
<p>
<form action="index.jsp" method="POST">
    <table border="0">
        <tr>
            <td>Registry Type</td>
            <td>
                <select name="registryType">
                    <option selected="true"><%= RegistryType.SYSTEM_CONFIGURATION.toString() %></option>
                    <option><%= RegistryType.SYSTEM_GOVERNANCE.toString() %></option>
                </select>
            </td>
        </tr>
        <tr>
            <td>Resource Path</td>
            <td><input type="text" name="resourcePath" value="foo/bar"/></td>
        </tr>
        <tr>
            <td>Value</td>
            <td><input type="text" name="value" value="WSO2 Carbon"/></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="Add" name="add"></td>
        </tr>
    </table>
</form>
</p>
<hr/>
<p>

<h3>View Resource</h3>
<p>
<form action="index.jsp" method="POST">
    <table border="0">
        <tr>
            <td>Registry Type</td>
            <td>
                <select name="registryType">
                    <option selected="true"><%= RegistryType.SYSTEM_CONFIGURATION.toString() %></option>
                    <option><%= RegistryType.SYSTEM_GOVERNANCE.toString() %></option>
                </select>
            </td>
        </tr>
        <tr>
            <td>Resource Path</td>
            <td><input type="text" name="resourcePath" value="foo/bar"/></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="View" name="view"></td>
        </tr>
    </table>
</form>
</p>
<hr/>

<%
    // Obtain the reference to the registry from the CarbonContext
    CarbonContext cCtx = CarbonContext.getCurrentContext();

    Registry registry = cCtx.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
    String registryType = request.getParameter("registryType");
    if(registryType != null) {
       registry = cCtx.getRegistry(RegistryType.valueOf(registryType));
    }

    if (request.getParameter("add") != null) {
        Resource resource = registry.newResource();
        resource.setContent(request.getParameter("value"));
        String resourcePath = request.getParameter("resourcePath");
        registry.put(resourcePath, resource);
%>
<p>
    Added resource: <%= resourcePath %>
</p>
<%
    } else if (request.getParameter("view") != null) {
        String resourcePath = request.getParameter("resourcePath");
        if (registry.resourceExists(resourcePath)) {
            Resource resource = registry.get(resourcePath);
            String content = new String((byte[]) resource.getContent());
            response.addHeader("resource-content", content);
%>
            <p>
                Resource at in Registry <%= registryType%> path <%= resourcePath%> : <%= content %>
            </p>
<%
        } else {
%>
            <p>
                Resource at path <%= resourcePath%> does not exist in Registry <%= registryType%>!
            </p>
<%
        }
    }
%>
