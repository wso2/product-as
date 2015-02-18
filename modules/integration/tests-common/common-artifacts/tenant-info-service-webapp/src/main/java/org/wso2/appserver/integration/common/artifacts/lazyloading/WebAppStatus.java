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

/**
 * Bean class to store web-app information
 */
public class WebAppStatus {

    private TenantStatus tenantStatus;
    private boolean webAppStarted;
    private boolean webAppFullyLoaded;

    /**
     * No argument constructor.
     */
    public WebAppStatus() {
    }


    /**
     * Constructor  with the parameter tenantStatus, webAppStarted, webAppFullyLoaded.
     *
     * @param tenantStatus      status of the tenant.
     * @param webAppStarted     true if web-app is started or else false.
     * @param webAppFullyLoaded true if web-app is fully loaded or else false.
     */
    public WebAppStatus(TenantStatus tenantStatus, boolean webAppStarted, boolean webAppFullyLoaded) {
        this.tenantStatus = tenantStatus;
        this.webAppStarted = webAppStarted;
        this.webAppFullyLoaded = webAppFullyLoaded;
    }

    /**
     * Set the information of tenant context is loaded or not.
     *
     * @param tenantStatus current status of the tenant.
     */
    public void setTenantStatus(TenantStatus tenantStatus) {
        this.tenantStatus = tenantStatus;
    }

    /**
     * Set the information of web-app is started or not.
     *
     * @param webAppStarted true if web-app is started or else false.
     */
    public void setWebAppStarted(boolean webAppStarted) {
        this.webAppStarted = webAppStarted;
    }

    /**
     * Set the information of web-app is fully loaded or not.
     *
     * @param webAppFullyLoaded true if web-app is fully loaded or else false.
     */
    public void setWebAppFullyLoaded(boolean webAppFullyLoaded) {
        this.webAppFullyLoaded = webAppFullyLoaded;
    }


    /**
     * Get the information of tenant context is loaded or not.
     *
     * @return TenantStatus information of tenant context
     */
    public TenantStatus getTenantStatus() {
        return tenantStatus;
    }

    /**
     * Get the  web-app  information about wen-app is started or not.
     *
     * @return boolean true if web app is started else false.
     */
    public boolean isWebAppStarted() {
        return webAppStarted;
    }

    /**
     * Get the information about web-app fully loaded or not.
     *
     * @return boolean true if web-app is  fully loaded or else false.
     */
    public boolean isWebAppFullyLoaded() {
        return webAppFullyLoaded;
    }
}
