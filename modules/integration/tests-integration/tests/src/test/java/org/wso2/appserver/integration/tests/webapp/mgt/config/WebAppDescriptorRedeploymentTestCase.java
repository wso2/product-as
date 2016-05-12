/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.appserver.integration.tests.webapp.mgt.config;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppMode;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import java.nio.file.Paths;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test if the changes in the webapp classloading configuration are taken into consideration when
 * redeploying
 */
public class WebAppDescriptorRedeploymentTestCase extends WebAppDescriptorTestBase {
    //This app requires Tomcat, Carbon, CXF, Spring
    //wso2as-web.xml doesn't have the environment specified
    private static final String CORRECT_ENV_NOT_SPECIFIED = "appServer-cxf-cl-app-1.0.0-no-env.war";

    @Factory(dataProvider = "webAppModeProvider")
    public WebAppDescriptorRedeploymentTestCase(WebAppMode webAppMode) {
        super(webAppMode);
    }

    @DataProvider(name = "webAppModeProvider")
    private static WebAppMode[][] WebAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] { new WebAppMode(CORRECT_ENV_NOT_SPECIFIED, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(CORRECT_ENV_NOT_SPECIFIED, TestUserMode.TENANT_USER) } };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        sampleAppDirectory = Paths.get(SAMPLE_APP_LOCATION, "redeployment").toString();

        super.init();
    }

    @Test(groups = "wso2.as",
            description = "Deploying web application")
    public void webApplicationDeploymentTest() throws Exception {
        webApplicationDeployment();
    }

    @Test(groups = "wso2.as",
            description = "Invoke web application before reload",
            dependsOnMethods = "webApplicationDeploymentTest")
    public void testInvokeWebAppBeforeReload() throws AutomationFrameworkException {
        //Since we have not specified the environments in the configuration file
        //and this app requires Tomcat, CXF and Spring, only Tomcat should PASS
        testForEnvironment(true, "Tomcat");
        testForEnvironment(false, "Carbon");
        testForEnvironment(false, "CXF");
        testForEnvironment(false, "Spring");
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.as",
            description = "Deploying exploded web application" + " file to deployment directory",
            dependsOnMethods = "testInvokeWebAppBeforeReload")
    public void testWebApplicationReDeployment() throws Exception {

        String source = Paths.get(SAMPLE_APP_LOCATION, "redeployment", "env-added").toString();

        webAppAdminClient.uploadWarFile(Paths.get(source, webAppFileName).toString());

        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName));

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as",
            description = "Invoke web application after reload",
            dependsOnMethods = "testWebApplicationReDeployment")
    public void testInvokeWebAppAfterReload() throws AutomationFrameworkException {
        //After redeployment with correct environments, all Tomcat,Carbon,CXF and Spring should PASS
        testForEnvironment(true, "Tomcat");
        testForEnvironment(true, "Carbon");
        testForEnvironment(true, "CXF");
        testForEnvironment(true, "Spring");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        deleteWebApplication();
    }

}
