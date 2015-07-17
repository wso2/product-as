/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.appserver.integration.tests;

import org.testng.annotations.BeforeSuite;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.extensions.usermgt.UserPopulator;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;

/**
 * This test cass will change the primary user store configuration in user-mgt.xml
 * to org.wso2.carbon.user.core.ldap.ReadWriteLDAPUserStoreManager and populate the users in automation.xml
 * in to apache Ldap server
 */
public class ConfigureReadWriteLdapTestCase extends ASIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeSuite(alwaysRun = true)
    public void updateUserManagementXml() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        serverConfigurationManager = new ServerConfigurationManager(asServer);
        serverConfigurationManager.applyConfiguration(new File(TestConfigurationProvider.getResourceLocation("AS")
                                                               + File.separator + "configs" + File.separator
                                                               + "readwriteldap" + File.separator + "user-mgt.xml"));
        //populate users in automation.xml
        UserPopulator userPopulator = new UserPopulator("AS", "appServerInstance0001");
        userPopulator.populateUsers();
    }

}
