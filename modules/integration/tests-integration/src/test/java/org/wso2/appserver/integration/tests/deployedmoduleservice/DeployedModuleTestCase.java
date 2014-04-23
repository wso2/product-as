/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.tests.deployedmoduleservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ModuleAdminServiceClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;

import java.util.HashSet;

/*
  This class can be used to test whether module has been correctly deployed
 */
public class DeployedModuleTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(DeployedModuleTestCase.class);
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
    }

    @Factory(dataProvider = "userModeProvider")
    public DeployedModuleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @Test(groups = "wso2.as", description = "Check modules")
    public void checkDeployedModules() throws Exception {
        ModuleAdminServiceClient moduleAdminServiceClient =
                new ModuleAdminServiceClient(backendURL, sessionCookie);
        ModuleMetaData[] moduleMetaDataArr = moduleAdminServiceClient.getModuleList();
        HashSet<String> moduleDataSet = new HashSet<String>(moduleMetaDataArr.length);
        for (ModuleMetaData aModuleMetaDataArr : moduleMetaDataArr) {
            moduleDataSet.add(aModuleMetaDataArr.getModulename());
        }

        Assert.assertTrue(moduleDataSet.contains("rahas"), "rahas module not found");
        Assert.assertTrue(moduleDataSet.contains("rampart"), "rampart module not found");
        Assert.assertTrue(moduleDataSet.contains("sandesha2"), "sandesha module not found");
        Assert.assertTrue(moduleDataSet.contains("wso2caching"), "wso2caching module not found");
        Assert.assertTrue(moduleDataSet.contains("addressing"), "addressing module not found");
        Assert.assertTrue(moduleDataSet.contains("wso2throttle"), "wso2throttle module not found");

        log.info("End of Deployed module test case");
    }
}
