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

package org.wso2.appserver.integration.tests.multitenancy;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * This test case checks whether a given tenant id (set through the TenantMgtAdminService) is preserved as the tenant id
 */
public class CARBON14560PersistProvidedTenantIDTestCase extends ASIntegrationTest {

    private static final String TENANT_INFO = "CARBON14560";
    private static final String TENANT_DOMAIN = TENANT_INFO + ".org";
    private static final int TENANT_ID = 14560;

    private TenantManagementServiceClient tenantManagementServiceClient;
    private TenantInfoBean tenantInfoBean;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        tenantManagementServiceClient = new TenantManagementServiceClient(backendURL, sessionCookie);
        tenantInfoBean = new TenantInfoBean();

        // populate the tenant info bean
        tenantInfoBean.setTenantDomain(TENANT_DOMAIN);
        tenantInfoBean.setUsagePlan("demo");
        tenantInfoBean.setFirstname(TENANT_INFO);
        tenantInfoBean.setLastname(TENANT_INFO);
        tenantInfoBean.setAdmin(TENANT_INFO);
        tenantInfoBean.setAdminPassword(TENANT_INFO);
        tenantInfoBean.setEmail(TENANT_INFO + "@" + TENANT_DOMAIN);
        tenantInfoBean.setTenantId(TENANT_ID);
        tenantInfoBean.setActive(true);
    }

    @Test(groups = "{wso2.as}", description = "test whether the tenant id is preserved for a new tenant")
    public void testTenantIDPreservationOfANewTenant() throws RemoteException, TenantMgtAdminServiceExceptionException {
        tenantManagementServiceClient.addTenant(tenantInfoBean);
        assertEquals(tenantManagementServiceClient.getTenant(TENANT_DOMAIN).getTenantId(), TENANT_ID,
                     "Provided tenant ID is not preserved.");
    }

    @Test(groups = "{wso2.as}",
            description = "test whether an exception is thrown when setting the tenant id of an existing tenant",
            dependsOnMethods = {"testTenantIDPreservationOfANewTenant"},
            expectedExceptions = {TenantMgtAdminServiceExceptionException.class},
            expectedExceptionsMessageRegExp = "TenantMgtAdminServiceExceptionException")
    public void testProvidingAnExistingTenantID() throws RemoteException, TenantMgtAdminServiceExceptionException {
        // change the tenant domain and add the tenant
        tenantInfoBean.setTenantDomain("temp" + TENANT_DOMAIN);
        tenantManagementServiceClient.addTenant(tenantInfoBean);
    }

    @AfterClass
    public void clean() throws TenantMgtAdminServiceExceptionException, RemoteException {
        tenantManagementServiceClient.deleteTenant(TENANT_DOMAIN);

        // TenantManagementServiceClient.deleteTenant() internally deactivates the tenant, hence the assert to tenant activate status
        assertFalse(tenantManagementServiceClient.getTenant(TENANT_DOMAIN).getActive(),
                    TENANT_DOMAIN + " tenant is in active state");
    }

}
