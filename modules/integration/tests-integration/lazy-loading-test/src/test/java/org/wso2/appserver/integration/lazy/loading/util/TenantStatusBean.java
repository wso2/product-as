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

package org.wso2.appserver.integration.lazy.loading.util;


/**
 * Bean class to store tenant information.
 */

public class TenantStatusBean {

    private boolean tenantContextLoaded;

    /**
     * No argument constructor.
     */
    public TenantStatusBean() {
    }

    /**
     * Constructor  with the parameter  tenantContextLoaded
     *
     * @param tenantContextLoaded true if tenant context is loaded or else false
     */
    public TenantStatusBean(boolean tenantContextLoaded) {
        this.tenantContextLoaded = tenantContextLoaded;

    }

    /**
     * get the information of tenant context is loaded or not.
     *
     * @return true if tenant context is loaded or else return false.
     */
    public boolean isTenantContextLoaded() {

        return tenantContextLoaded;
    }

    /**
     * Set the information of tenant context is loaded or not.
     *
     * @param isTenantContextLoaded true if tenant context is loaded or else false.
     */
    public void setTenantContextLoaded(boolean isTenantContextLoaded) {
        this.tenantContextLoaded = isTenantContextLoaded;
    }

}
