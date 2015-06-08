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

package org.wso2.appserver.integration.lazy.loading.tenants;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.lazy.loading.LazyLoadingBaseTest;
import org.wso2.appserver.integration.lazy.loading.util.LazyLoadingTestException;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;

import static org.testng.Assert.assertEquals;

/**
 * Test tenant configuration context Load and Unload in Ghost deployment enable environment.
 */
public class TenantLoadUnloadTestCase extends LazyLoadingBaseTest {

    private static final Log log = LogFactory.getLog(TenantLoadUnloadTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as.lazy.loading", description = "Login using  one tenant user. Before loginTenantUser " +
            " contexts of both users should not be loaded. After loginTenantUser  only the logged user context should" +
            " get load.", alwaysRun = true)
    public void testTenantContextLoadInLogin() throws LazyLoadingTestException, AutomationUtilException {
        assertEquals(getTenantStatus(TENANT_DOMAIN_1).isTenantContextLoaded(), false,
                "Tenant context is loaded before any action related to that tenant");
        assertEquals(getTenantStatus(TENANT_DOMAIN_2).isTenantContextLoaded(), false,
                "Tenant context is loaded before any action related to that tenant");
        log.info("Testing Tenant context loading  for :" + TENANT_DOMAIN_1);
        loginAsTenantAdmin(TENANT_DOMAIN_1_kEY);


        try {
            webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        } catch (AxisFault axisFault) {
            String customErrorMessage = "AxisFault Exception when  creating  WebAppAdminClient object, Backend URL:"
                    + backendURL + " Session Cookie: " + sessionCookie + "\n" + axisFault.getMessage();
            log.error(customErrorMessage);
            throw new LazyLoadingTestException(customErrorMessage, axisFault);
        }


        assertEquals(getTenantStatus(TENANT_DOMAIN_1).isTenantContextLoaded(), true,
                "Tenant context is  not loaded after tenant loginTenantUser");
        assertEquals(getTenantStatus(TENANT_DOMAIN_2).isTenantContextLoaded(), false, "Tenant context is loaded without" +
                " loginTenantUser");
    }

    @Test(groups = "wso2.as.lazy.loading", description = "Wait until the tenant idle time passing and  check for " +
            "the tenant context unloading.", dependsOnMethods = "testTenantContextLoadInLogin")
    public void testTenantContextUnLoadInTenantIdle()
            throws LazyLoadingTestException {
        assertEquals(getTenantStatus(TENANT_DOMAIN_1).isTenantContextLoaded(), true,
                "Tenant context is  not loaded after tenant loginTenantUser");
        log.info("Waiting for Tenant context to un-load :" + TENANT_DOMAIN_1);
        assertEquals(checkTenantAutoUnloading(TENANT_DOMAIN_1), true, "Tenant context is  not unloaded after idle time");

    }

}
