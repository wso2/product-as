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

package org.wso2.appserver.integration.tests.server.mgt;

import org.testng.annotations.BeforeClass;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.carbonserver.MultipleServersManager;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;
import org.wso2.carbon.integration.common.tests.OSGIServerBundleStatusTest;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.HashMap;

public class OSGIServerBundleStatusTestCase extends OSGIServerBundleStatusTest {

    /*private HashMap<String, String> serverPropertyMap = new HashMap();
    private MultipleServersManager manager = new MultipleServersManager();
    private static int telnetPort = 2000;

    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException, AutomationFrameworkException {
        this.serverPropertyMap.put("-DportOffset", "101");
        this.serverPropertyMap.put("-DosgiConsole", Integer.toString(telnetPort));
        AutomationContext autoCtx = new AutomationContext();
        CarbonTestServerManager server = new CarbonTestServerManager(autoCtx, System.getProperty("carbon.zip"), this.serverPropertyMap);
        this.manager.startServers(new TestServerManager[]{server});
    }*/
}
