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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<jsp:include page="../dialog/display_messages.jsp"/>

<carbon:breadcrumb
        label="sample.installer"
        resourceBundle="org.wso2.stratos.appserver.sample.installer.ui.i18n.Resources"
        topPage="true"
        request="<%=request%>"/>

<fmt:bundle basename="org.wso2.stratos.appserver.sample.installer.ui.i18n.Resources">
    <div id="middle">
        <h2><fmt:message key="sample.installer"/></h2>

        <div id="workArea">
            <div id="output" style="display:none;"></div>

            <ul>
                <li>
                    <a href="installer.jsp?type=all" class="icon-link-nofloat" style="background-image:url(images/all.gif);">
                        Install All Samples
                    </a>
                </li>
                <li>
                    <a href="installer.jsp?type=services" class="icon-link-nofloat" style="background-image:url(images/services.gif);">
                        Install Service Samples
                    </a>
                </li>
                <li>
                    <a href="installer.jsp?type=webapps" class="icon-link-nofloat" style="background-image:url(images/webapps.gif);">
                        Install Web Application Samples
                    </a>
                </li>
            </ul>
        </div>
    </div>
</fmt:bundle>