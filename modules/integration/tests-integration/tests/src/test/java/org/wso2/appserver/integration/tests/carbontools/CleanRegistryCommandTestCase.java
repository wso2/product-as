/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appserver.integration.tests.carbontools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.CarbonCommandToolsUtil;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.extensions.carbonserver.MultipleServersManager;
import org.wso2.carbon.integration.common.tests.CarbonTestServerManager;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

/**
 * This class to test --cleanRegistry by adding a resource and clean it by this command
 */
public class CleanRegistryCommandTestCase extends ASIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceClient;
    private static final String RESOURCE_PATH_NAME = "/_system/config/repository/";

    private static final Log log = LogFactory.getLog(CleanRegistryCommandTestCase.class);
    private HashMap<String, String> serverPropertyMap = new HashMap<String, String>();
    private MultipleServersManager manager = new MultipleServersManager();
    private String carbonHome;
    private AutomationContext context;
    String sessionCookieForInstance002;
    String backendURLForInstance002;
    LoginLogoutClient loginLogoutClientForInstance002;
    private int portOffset = 1;
    private Process process;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        serverPropertyMap.put("-DportOffset",Integer.toString(portOffset));
        AutomationContext autoCtx = new AutomationContext();
        CarbonTestServerManager server =
                new CarbonTestServerManager(autoCtx, System.getProperty("carbon.zip"), serverPropertyMap);
        manager.startServers(server);
        carbonHome = server.getCarbonHome();

        context = new AutomationContext("AS", "appServerInstance0002", ContextXpathConstants.SUPER_TENANT,
                                        ContextXpathConstants.ADMIN);
        create();

        resourceAdminServiceClient =
                new ResourceAdminServiceClient(backendURLForInstance002, sessionCookieForInstance002);

    }

    @Test(groups = "wso2.greg", description = "Add resource")
    public void testCleanResource() throws Exception {
        boolean isResourceFound;
        String resourcePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                              File.separator + "AS" + File.separator + "carbontools" +
                              File.separator + "resource.txt";

        DataHandler dh = new DataHandler(new URL("file:///" + resourcePath));
        resourceAdminServiceClient.addResource(RESOURCE_PATH_NAME + "resource.txt", "txt", "testDesc", dh);
        isResourceFound = true;
        manager.stopAllServers();
        String[] cmdArrayToCleanRegistry;
        try {
            if (CarbonCommandToolsUtil.isCurrentOSWindows()) {
                cmdArrayToCleanRegistry = new String[]{"--cleanRegistry"};
                process = CarbonCommandToolsUtil.startServerUsingCarbonHome(carbonHome, portOffset, context,
                                                                            cmdArrayToCleanRegistry);
            } else {
                cmdArrayToCleanRegistry =
                        new String[]{"--cleanRegistry"};
                process = CarbonCommandToolsUtil.startServerUsingCarbonHome(carbonHome, portOffset, context,
                                                                            cmdArrayToCleanRegistry);
            }

            boolean startupStatus = CarbonCommandToolsUtil.isServerStartedUp(context, portOffset);
            log.info("Server startup status : " + startupStatus);
            create();
            resourceAdminServiceClient =
                    new ResourceAdminServiceClient(backendURLForInstance002, sessionCookieForInstance002);
            resourceAdminServiceClient.getResource(RESOURCE_PATH_NAME + "resource.txt");
        } catch (Exception ex) {
            if (ex.getMessage().contains("Resource does not exist")) {
                isResourceFound = false;
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        Assert.assertFalse(isResourceFound, "Resource not deleted successfully");
    }

    private void create()
            throws IOException, XPathExpressionException, URISyntaxException, SAXException,
                   XMLStreamException, LoginAuthenticationExceptionException {
        loginLogoutClientForInstance002 = new LoginLogoutClient(context);
        sessionCookieForInstance002 = loginLogoutClientForInstance002.login();
        backendURLForInstance002 = context.getContextUrls().getBackEndUrl();
    }

    @AfterClass(alwaysRun = true)
    public void serverShutDown() throws Exception {
        CarbonCommandToolsUtil.serverShutdown(portOffset, context);
    }

}
