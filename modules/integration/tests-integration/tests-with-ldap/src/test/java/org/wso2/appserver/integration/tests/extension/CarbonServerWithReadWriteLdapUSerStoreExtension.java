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
package org.wso2.appserver.integration.tests.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.FileManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

public class CarbonServerWithReadWriteLdapUSerStoreExtension extends ExecutionListenerExtension {
    private static final Log log = LogFactory.getLog(CarbonServerWithReadWriteLdapUSerStoreExtension.class);
    private static TestServerManager asServerWithApacheLdap;

    @Override
    public void initiate() throws AutomationFrameworkException {
        AutomationContext context;
        try {
            context = new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN);
        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("Error Initiating Server Information", e);
        }

        //if port offset is not set, setting it to 0
        if (getParameters().get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) == null) {
            getParameters().put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, "0");
        }

        asServerWithApacheLdap = new TestServerManager(context, null, getParameters()) {
            public void configureServer() throws AutomationFrameworkException {

                String userMgtXml = TestConfigurationProvider.getResourceLocation("AS")
                                    + File.separator + "configs" + File.separator
                                    + "readwriteldap" + File.separator + "user-mgt.xml";

                try {
                    log.info("Changing the primary user store configuration to read write ldap");
                    FileManager.copyFile(new File(userMgtXml)
                            , asServerWithApacheLdap.getCarbonHome() + File.separator + "repository" +
                     File.separator + "conf" + File.separator + "user-mgt.xml");
                } catch (IOException e) {
                    log.error("Error while coping user-mgt.xml", e);
                    throw new AutomationFrameworkException(e.getMessage(), e);
                }
            }
        };
    }

    @Override
    public void onExecutionStart() throws AutomationFrameworkException {
        try {
            asServerWithApacheLdap.startServer();
        } catch (IOException e) {
            throw new AutomationFrameworkException("Error while starting server " + e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("Error while starting server " + e.getMessage(), e);
        }

    }

    @Override
    public void onExecutionFinish() throws AutomationFrameworkException {
        asServerWithApacheLdap.stopServer();
    }


    public static TestServerManager getTestServer() {
        return asServerWithApacheLdap;
    }


}
