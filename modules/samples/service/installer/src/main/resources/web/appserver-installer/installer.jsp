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
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.utils.multitenancy.MultitenantUtils" %>
<%@ page
        import="org.wso2.stratos.appserver.sample.installer.ui.SampleInstaller" %>
<%@ page
        import="org.wso2.stratos.appserver.sample.installer.ui.internal.SampleInstallerServiceComponent" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.wso2.carbon.utils.multitenancy.MultitenantConstants" %>

<%
    String type = request.getParameter("type");
    ResourceBundle resourceBundle =
            ResourceBundle.getBundle("org.wso2.stratos.appserver.sample.installer.ui.i18n.Resources",
                                     request.getLocale());
    int tenantId = getTenantId(request);
    if (type.equalsIgnoreCase("webapps")) {
        new SampleInstaller().installWebappSamples(tenantId);
        String msg = resourceBundle.getString("installed.webapp.samples");
        CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.INFO, request);
    } else if (type.equalsIgnoreCase("services")) {
        new SampleInstaller().installServiceSamples(tenantId);
        String msg = resourceBundle.getString("installed.service.samples");
        CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.INFO, request);
    } else if (type.equalsIgnoreCase("all")) {
        new SampleInstaller().installAllSamples(tenantId);
        String msg = resourceBundle.getString("installed.all.samples");
        CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.INFO, request);
    }
%>
<script type="text/javascript">
    location.href = 'index.jsp';
</script>
<%!
    private int getTenantId(HttpServletRequest request) {
        String tenantDomain = MultitenantUtils.getTenantDomain(request);
        ConfigurationContext tenantCfgCtx =
                TenantAxisUtils.getTenantConfigurationContext(tenantDomain,
                                                              SampleInstallerServiceComponent.getServerConfigCtx());
        int tenantId = MultitenantUtils.getTenantId(tenantCfgCtx);
        return tenantId;
    }
%>