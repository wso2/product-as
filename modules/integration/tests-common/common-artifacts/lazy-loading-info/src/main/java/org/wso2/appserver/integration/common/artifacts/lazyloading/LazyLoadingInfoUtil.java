/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.integration.common.artifacts.lazyloading;


import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.webapp.mgt.WebApplication;
import org.wso2.carbon.webapp.mgt.WebApplicationsHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class that contains the service implementation of LazyLoadingInfoService and supportive methods.
 */
public class LazyLoadingInfoUtil {
    private static final Log log = LogFactory.getLog(LazyLoadingInfoUtil.class);


    /**
     * Get the server configuration context.
     *
     * @return configuration context of the server.
     */
    private static ConfigurationContext getServerConfigurationContext() {
        ConfigurationContextService configurationContext =
                (ConfigurationContextService) PrivilegedCarbonContext.getThreadLocalCarbonContext().
                        getOSGiService(ConfigurationContextService.class, null);
        return configurationContext.getServerConfigContext();
    }


    /**
     * Get the configuration contexts of all loaded tenants
     *
     * @return Map that contains the  configuration contexts
     */
    private static Map<String, ConfigurationContext> getTenantConfigServerContexts() {
        return TenantAxisUtils.getTenantConfigurationContexts(getServerConfigurationContext());
    }


    /**
     * Get the configuration context of given tenant.
     *
     * @param tenantDomain tenant domain name.
     * @return ConfigurationContext of given tenant
     */
    private static ConfigurationContext getTenantConfigurationServerContext(String tenantDomain) {
        return getTenantConfigServerContexts().get(tenantDomain);
    }

    /**
     * Check  the given tenant is loaded.
     *
     * @param tenantDomain Domain name of the tenant
     * @return TenantStatus with current status  information about the tenant.
     */
    protected static TenantStatus getTenantStatus(String tenantDomain) {
        boolean isTenantContextLoaded = false;
        Map<String, ConfigurationContext> tenantConfigServerContexts = getTenantConfigServerContexts();
        if (tenantConfigServerContexts != null) {
            isTenantContextLoaded = tenantConfigServerContexts.containsKey(tenantDomain);
            log.info("Tenant " + tenantDomain + " loaded :" + isTenantContextLoaded);
        }
        return new TenantStatus(isTenantContextLoaded);
    }

    /**
     * Check  the given web-app of given tenant is loaded.
     *
     * @param tenantDomain Tenant name
     * @param webAppName   Web-app Name
     * @return WebAppStatus  with current status  information about the Web app.
     */
    protected static WebAppStatus getWebAppStatus(String tenantDomain, String webAppName) {
        WebAppStatus webAppStatus = new WebAppStatus();
        ConfigurationContext tenantConfigurationServerContext = getTenantConfigurationServerContext(tenantDomain);
        if (tenantConfigurationServerContext != null) {
            webAppStatus.setTenantStatus(new TenantStatus(true));
            log.info("Tenant " + tenantDomain + " configuration context is loaded.");
            WebApplicationsHolder webApplicationsHolder = (WebApplicationsHolder) ((HashMap)
                    tenantConfigurationServerContext.getLocalProperty("carbon.webapps.holderlist")).get("webapps");
            Map<String, WebApplication> startedWebAppMap = webApplicationsHolder.getStartedWebapps();
            if (startedWebAppMap != null) {

                WebApplication webApplication = startedWebAppMap.get(webAppName);
                if (webApplication != null) {
                    webAppStatus.setWebAppStarted(true);
                    log.info("Tenant " + tenantDomain + " Web-app: " + webAppName + " is available in configuration context.");
                    boolean isWebAppGhost = Boolean.parseBoolean((String) webApplication.getProperty("GhostWebApp"));
                    log.info("Tenant " + tenantDomain + " Web-app: " + webAppName + " is in Ghost deployment status :" +
                            isWebAppGhost);
                    webAppStatus.setWebAppGhost(isWebAppGhost);
                } else {
                    log.info("Given web-app:" + webAppName + " for tenant:" + tenantDomain + " not found in started state");
                    webAppStatus.setWebAppStarted(false);
                }

            } else {
                log.info("Tenant " + tenantDomain + " has no started web-apps.");
                webAppStatus.setWebAppStarted(false);
            }

        } else {
            log.info("Tenant " + tenantDomain + " configuration context is not loaded.");
            webAppStatus.setTenantStatus(new TenantStatus(false));
        }

        return webAppStatus;

    }


    /**
     * Check  the given web-app of super tenant is loaded.
     *
     * @param webAppName Web-app Name
     * @return WebAppStatus  with current status  information about the Web app.
     */

    protected static WebAppStatus getSuperTenantWebAppStatus(String webAppName) {

        WebAppStatus webAppStatus = new WebAppStatus();
        webAppStatus.setTenantStatus(new TenantStatus(true)); // Super tenant always loaded;
        ConfigurationContext serverConfigurationContext = getServerConfigurationContext();

        WebApplicationsHolder webApplicationsHolder = (WebApplicationsHolder) ((HashMap)
                serverConfigurationContext.getLocalProperty("carbon.webapps.holderlist")).get("webapps");
        Map<String, WebApplication> startedWebAppMap = webApplicationsHolder.getStartedWebapps();
        if (startedWebAppMap != null) {
            WebApplication webApplication = startedWebAppMap.get(webAppName);
            if (webApplication != null) {
                webAppStatus.setWebAppStarted(true);
                log.info("Super Tenant  Web-app: " + webAppName + " is available in configuration context.");
                boolean isWebAppGhost = Boolean.parseBoolean((String) webApplication.getProperty("GhostWebApp"));
                log.info("Super Tenant Web-app: " + webAppName + " is in Ghost deployment status :" + isWebAppGhost);
                webAppStatus.setWebAppGhost(isWebAppGhost);
            } else {
                log.info("Given web-app:" + webAppName + " for super tenant  not found in started state");
            }

        } else {
            log.info("Super Tenant has no started web-apps.");
        }
        return webAppStatus;
    }


}
