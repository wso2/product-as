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
package org.wso2.carbon.integration.test.deployedmoduleservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.module.mgt.ModuleAdminServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;

import java.util.HashSet;

/*
  This class can be used to test whether module has been correctly deployed
 */
public class DeployedModuleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(DeployedModuleTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "Check modules")
    public void checkDeployedModules() throws Exception {
        ModuleAdminServiceClient moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        ModuleMetaData[] moduleMetaDataArr = moduleAdminServiceClient.getModuleList();

        HashSet<String> moduleDataSet = new HashSet<String>(moduleMetaDataArr.length);

        for (int x = 0; x < moduleMetaDataArr.length; x++) {
            moduleDataSet.add(moduleMetaDataArr[x].getModulename());
        }

        Assert.assertTrue(moduleDataSet.contains("wso2xfer"));
        Assert.assertTrue(moduleDataSet.contains("rahas"));
        Assert.assertTrue(moduleDataSet.contains("rampart"));
        Assert.assertTrue(moduleDataSet.contains("sandesha2"));
        Assert.assertTrue(moduleDataSet.contains("wso2caching"));
        Assert.assertTrue(moduleDataSet.contains("addressing"));
        Assert.assertTrue(moduleDataSet.contains("wso2mex"));
        Assert.assertTrue(moduleDataSet.contains("wso2throttle"));

        log.info("End of Deployed module test case");
    }
}
