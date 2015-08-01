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
import org.wso2.appserver.integration.common.utils.WebAppMode;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import java.io.File;

/**
 * This class to test when different combinations of webapp-classloading.xml and wso2as-web.xml being inside the webapp
 * classloading configuration correctly happens
 */
public class WebAppDescriptorClassloadingTestCase extends WebAppDescriptorTest {
    //This app requires Tomcat, Carbon
    private static final String NO_CONFIG_FILES = "appServer-carbon-cl-app-1.0.0-no-config-file-present.war";

    //This app requires Tomcat, CXF, Spring
    // webapp-classloading.xml has Tomcat and wso2as-web.xml has CXF
    private static final String BOTH_FILES_PRESENT_WITH_DIFFERENT_ENV = "appServer-cxf-cl-app-1.0.0-two-config-files-both-have-env.war";

    //This app requires Tomcat, Carbon
    //wso2as-web.xml has no environment element present and webapp-classloading.xml has CXF
    private static final String BOTH_FILES_PRESENT_ONLY_OLD_HAS_ENV = "appServer-carbon-cl-app-1.0.0-two-config-files-old-have-env.war";

    //This app requires Tomcat, Carbon
    //Both webapp-classloading.xml and wso2as-web.xml are present but none has the environment specified
    private static final String BOTH_FILES_PRESENT_ENV_IN_NONE = "appServer-carbon-cl-app-1.0.0-two-config-files-none-have-env.war";

    //This app requires Tomcat, CXF, Spring
    //Only webapp-classloading.xml is present with CXF specified
    private static final String ONLY_OLD_CONFIG_FILE_PRESENT = "appServer-cxf-cl-app-1.0.0-old-config-file.war";

    //This app requires Tomcat, CXF, Spring
    //Only wso2as-web.xml is present with CXF specified
    private static final String ONLY_NEW_CONFIG_FILE_PRESENT = "appServer-cxf-cl-app-1.0.0-new-config-file.war";

    @Factory(dataProvider = "webAppModeProvider")
    public WebAppDescriptorClassloadingTestCase(WebAppMode webAppMode) {
        super(webAppMode);
    }

    @DataProvider(name = "webAppModeProvider")
    private static WebAppMode[][] WebAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] { new WebAppMode(NO_CONFIG_FILES, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(NO_CONFIG_FILES, TestUserMode.TENANT_USER) }, new WebAppMode[] {
                new WebAppMode(BOTH_FILES_PRESENT_WITH_DIFFERENT_ENV, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(BOTH_FILES_PRESENT_WITH_DIFFERENT_ENV, TestUserMode.TENANT_USER) },
                new WebAppMode[] {
                        new WebAppMode(BOTH_FILES_PRESENT_ONLY_OLD_HAS_ENV, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(BOTH_FILES_PRESENT_ONLY_OLD_HAS_ENV, TestUserMode.TENANT_USER) },
                new WebAppMode[] { new WebAppMode(BOTH_FILES_PRESENT_ENV_IN_NONE, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(BOTH_FILES_PRESENT_ENV_IN_NONE, TestUserMode.TENANT_USER) },
                new WebAppMode[] { new WebAppMode(ONLY_OLD_CONFIG_FILE_PRESENT, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(ONLY_OLD_CONFIG_FILE_PRESENT, TestUserMode.TENANT_USER) },
                new WebAppMode[] { new WebAppMode(ONLY_NEW_CONFIG_FILE_PRESENT, TestUserMode.SUPER_TENANT_USER) },
                new WebAppMode[] { new WebAppMode(ONLY_NEW_CONFIG_FILE_PRESENT, TestUserMode.TENANT_USER) } };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        sampleAppDirectory = SAMPLE_APP_LOCATION + File.separator + "classloading";
        super.init();
    }

    @Test(groups = "wso2.as",
            description = "Deploying web application")
    public void webApplicationDeploymentTest() throws Exception {
        webApplicationDeployment();
    }

    @Test(groups = "wso2.as",
            description = "Invoke web application",
            dependsOnMethods = "webApplicationDeploymentTest")
    public void testInvokeWebApp() throws AutomationFrameworkException { //TODO: verify the benefit of throwing only Exception
        invokeWebApp(true, true, true, true);
    }

    @AfterClass(alwaysRun = true)
    public void deleteWebApp() throws Exception {
        deleteWebApplication();
    }

}
