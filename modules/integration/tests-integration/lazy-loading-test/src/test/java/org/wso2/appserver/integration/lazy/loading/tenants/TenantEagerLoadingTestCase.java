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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.lazy.loading.LazyLoadingBaseTest;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class TenantEagerLoadingTestCase extends LazyLoadingBaseTest {
    private static final Log log = LogFactory.getLog(TenantEagerLoadingTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        ARTIFACTS_LOCATION =
                TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator +
                        "AS" + File.separator + "eager" + File.separator;
        super.init();
    }

    @Test(groups = "wso2.as.eager.loading", description = "Test for loading all tenants with server startup",
            alwaysRun = true)
    public void testLoadAllTenant() throws Exception {
        String carbonArtifactLocation = ARTIFACTS_LOCATION + "carbon-01.xml";
        applyCarbonXMLConfigChange(carbonArtifactLocation);
        assertEquals(getTenantStatus(TENANT_DOMAIN_1).isTenantContextLoaded(), true,
                "Tenant " + TENANT_DOMAIN_1 + " is not loaded");
        assertEquals(getTenantStatus(TENANT_DOMAIN_2).isTenantContextLoaded(), true,
                "Tenant " + TENANT_DOMAIN_1 + " is not loaded");
    }

    @Test(groups = "wso2.as.eager.loading", description = "Test for loading all tenants, but not tenant1.com with " +
            "server startup",
            alwaysRun = true, dependsOnMethods = "testLoadAllTenant")
    public void testNotLoadSpecificTenant() throws Exception {
        String carbonArtifactLocation = ARTIFACTS_LOCATION + "carbon-02.xml";
        applyCarbonXMLConfigChange(carbonArtifactLocation);
        assertEquals(getTenantStatus(TENANT_DOMAIN_1).isTenantContextLoaded(), false,
                "Tenant " + TENANT_DOMAIN_1 + " is loaded, but expected not to be loaded");
        assertEquals(getTenantStatus(TENANT_DOMAIN_2).isTenantContextLoaded(), true,
                "Tenant " + TENANT_DOMAIN_1 + " is not loaded");
    }

    @Test(groups = "wso2.as.eager.loading", description = "Test for load tenant1.com only at server startup",
            alwaysRun = true, dependsOnMethods = "testNotLoadSpecificTenant")
    public void testLoadSpecificTenant() throws Exception {
        String carbonArtifactLocation = ARTIFACTS_LOCATION + "carbon-03.xml";
        applyCarbonXMLConfigChange(carbonArtifactLocation);
        assertEquals(getTenantStatus(TENANT_DOMAIN_1).isTenantContextLoaded(), true,
                "Tenant " + TENANT_DOMAIN_1 + " is not loaded");
        assertEquals(getTenantStatus(TENANT_DOMAIN_2).isTenantContextLoaded(), false,
                "Tenant " + TENANT_DOMAIN_1 + " is loaded, but expected not to be loaded");
    }

    private void applyCarbonXMLConfigChange(String carbonXMLLocation) throws Exception {
        File sourceFile = new File(carbonXMLLocation);
        File targetFile = new File(CARBON_REPOSITORY_LOCATION);
        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        log.info("carbon.xml replaced with :" + carbonXMLLocation);
        serverManager.restartGracefully();
        log.info("Server Restarted after applying carbon.xml and tenant information utility web application");
    }
}