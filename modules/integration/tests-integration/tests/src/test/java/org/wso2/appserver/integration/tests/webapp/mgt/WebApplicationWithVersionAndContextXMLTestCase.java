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

package org.wso2.appserver.integration.tests.webapp.mgt;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.nio.file.Paths;

import static org.testng.Assert.assertTrue;

/**
 * This test checks the successful deployment of a webapp with a version (e.g. example#2.war) and with a context.xml
 * inside the war
 */

public class WebApplicationWithVersionAndContextXMLTestCase extends ASIntegrationTest {

    private String webAppName = "context-reload-test-webapp";
    private String webAppFileName = "context-reload-test-webapp.war";
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeDataProvider")
    public WebApplicationWithVersionAndContextXMLTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    protected static TestUserMode[][] userModeDataProvider() {
        return new TestUserMode[][]{{TestUserMode.SUPER_TENANT_ADMIN}, {TestUserMode.TENANT_ADMIN}};
    }

    @BeforeClass
    public void init() throws Exception {
        super.init(userMode);
    }

    @Test(groups = "wso2.as", description = "deploying the versioned webapp with context.xml")
    public void testDeploymentOfVersionedWebappWithContextXML() throws Exception {
        String webAppFilePath = Paths.get(FrameworkPathUtil.getSystemResourceLocation(), "artifacts", "AS", "war",
                                          webAppFileName).normalize().toString();
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                   webAppName + " webapp is not deployed");
    }

    @AfterClass
    public void clean() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(
                backendURL, asServer.getDefaultInstance().getHosts().get("default"), sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                   webAppName + " webapp is not undeployed");
    }
}
