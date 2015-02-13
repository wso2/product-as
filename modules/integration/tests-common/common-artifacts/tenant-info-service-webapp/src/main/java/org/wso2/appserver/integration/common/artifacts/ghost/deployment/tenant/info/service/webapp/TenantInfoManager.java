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

package org.wso2.appserver.integration.common.artifacts.ghost.deployment.tenant.info.service.webapp;


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

public class TenantInfoManager {
    private static final Log log = LogFactory.getLog(TenantInfoManager.class);




    private static ConfigurationContext getServerConfigurationContext(){
        ConfigurationContextService configurationContext = (ConfigurationContextService)
                PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(ConfigurationContextService.class,
                        null);
        return  configurationContext.getServerConfigContext();
    }








    /**
     * Get the configuration contexts of all loaded tenants
     *
     * @return Map<String, ConfigurationContext>  the contains the  configuration contexts
     */
    private static Map<String, ConfigurationContext> getTenantsConfigContext() {
        Map<String, ConfigurationContext> tenantConfigContextsServer =
                TenantAxisUtils.getTenantConfigurationContexts(getServerConfigurationContext());
        return tenantConfigContextsServer;

    }


    /**
     * Get the configuration context of given tenant.
     *
     * @param tenantName tenant domain name.
     * @return ConfigurationContext of given tenant
     */
    private static ConfigurationContext getTenantConfigurationContext(String tenantName) {
        ConfigurationContext tenantConfigurationContext = getTenantsConfigContext().get(tenantName);
        return tenantConfigurationContext;
    }

    /**
     * Check  the given tenant is loaded.
     *
     * @param tenantName
     * @return true if tenants configuration context can be found, if not false.
     */
    protected static boolean isTenantLoaded(String tenantName) {
        boolean isTenantLoaded = false;

        Map<String, ConfigurationContext> tenantConfigContextsServer = getTenantsConfigContext();
        if (tenantConfigContextsServer != null) {
            isTenantLoaded = tenantConfigContextsServer.containsKey(tenantName);
            log.info("Tenant " + tenantName + " loaded :" + isTenantLoaded);
        }
        return isTenantLoaded;
    }

    /**
     * Check  the given web-app of given tenant is loaded.
     *
     * @param tenantName Tenant name
     * @param webAppName Web-app Name
     * @return true if  given web-apps deployment status is not Ghost. false if given web-apps
     * deployment status is not Ghost or given web app is not available in configuration context
     * or given tenant is not loaded.
     */
    protected static boolean isWebAppLoaded(String tenantName, String webAppName) {
        boolean isWebAppLoaded = false;
        ConfigurationContext tenantConfigurationContext = getTenantConfigurationContext(tenantName);
        if (tenantConfigurationContext != null) {
            log.info("Tenant " + tenantName + " configuration context is loaded.");
            WebApplicationsHolder webApplicationsHolder = (WebApplicationsHolder) ((HashMap)
                    tenantConfigurationContext.getLocalProperty("carbon.webapps.holderlist")).get("webapps");
            Map<String, WebApplication> startedWebAppMap = webApplicationsHolder.getStartedWebapps();
            if (startedWebAppMap != null) {
                WebApplication webApplication = startedWebAppMap.get(webAppName);
                if (webApplication != null) {
                    log.info("Tenant " + tenantName + " Web-app: " + webAppName +
                            " is available in configuration context.");
                    boolean isWebAppGhost = Boolean.parseBoolean((String) webApplication.getProperty("GhostWebApp"));
                    log.info("Tenant " + tenantName + " Web-app: " + webAppName + " is in Ghost deployment status :" +
                            isWebAppGhost);
                    isWebAppLoaded = !isWebAppGhost;
                } else {
                    log.info("Given web-app:" + webAppName + " for tenant:" + tenantName + " not found in started state");
                }

            } else {
                log.info("Tenant " + tenantName + " has no started web-apps.");
            }

        } else {
            log.info("Tenant " + tenantName + " configuration context is not loaded.");
        }

        return isWebAppLoaded;

    }


    /**
     *
     *  Check  the given web-app of supper tenant is loaded.
     *
     * @param webAppName Web-app Name
     * @return true if  given web-apps deployment status is not Ghost. false if given web-apps
     * deployment status is not Ghost or given web app is not available in configuration context
     */
    protected static boolean isSuperTenantWebAppLoaded( String webAppName) {
        boolean isWebAppLoaded = false;

        ConfigurationContext    serverConfigurationContext=   getServerConfigurationContext();

        WebApplicationsHolder webApplicationsHolder = (WebApplicationsHolder) ((HashMap)
                serverConfigurationContext.getLocalProperty("carbon.webapps.holderlist")).get("webapps");
        Map<String, WebApplication> startedWebAppMap = webApplicationsHolder.getStartedWebapps();
        if (startedWebAppMap != null) {
            WebApplication webApplication = startedWebAppMap.get(webAppName);
            if (webApplication != null) {
                log.info("Super Tenant  Web-app: " + webAppName +
                        " is available in configuration context.");
                boolean isWebAppGhost = Boolean.parseBoolean((String) webApplication.getProperty("GhostWebApp"));
                log.info("Super Tenant Web-app: " + webAppName + " is in Ghost deployment status :" +
                        isWebAppGhost);
                isWebAppLoaded = !isWebAppGhost;
            } else {
                log.info("Given web-app:" + webAppName + " for super tenant  not found in started state");
            }

        } else {
            log.info("Super Tenant has no started web-apps.");
        }
        return  isWebAppLoaded;
    }



}
