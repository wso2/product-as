/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.appserver.integration.tests.jaggery;

import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.JaggeryApplicationUploaderClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.tests.jaggery.utils.JaggeryTestUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class sends requests to application.jag and validates the response
 */
public class ApplicationObjectTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(ApplicationObjectTestCase.class);
    private TestUserMode userMode;
    private final String hostName = "localhost";

    @BeforeTest(alwaysRun = true) //uploads testapp.zip file and verify  deployment
    public void jaggeryFileUpload() throws Exception {
        super.init(userMode);
        JaggeryApplicationUploaderClient jaggeryAppUploaderClient =
                new JaggeryApplicationUploaderClient(backendURL, sessionCookie);
        jaggeryAppUploaderClient.uploadJaggeryFile("testapp.zip",
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "jaggery" + File.separator +
                        "testapp.zip");
//        WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie , "application.jag");   // verifying the deployment
        Thread.sleep(30000);
        log.info("testapp.zip file uploaded successfully");
    }

    @Factory(dataProvider = "userModeProvider")
    public ApplicationObjectTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }


    @AfterTest(alwaysRun = true)
    public void jaggeryFileDelete() throws Exception {  // deletes the testapp.zip web app file
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile("testapp", hostName);
        log.info("testapp deleted successfully");
    }

//    @Test(groups = "wso2.as", description = "invoke applicationjag")
//    public void testApplication() throws Exception {
//        String response = null;
//        URLConnection jaggeryServerConnection;
//        URL jaggeryURL = new URL(webAppURL + "/testapp/application.jag");
//        jaggeryServerConnection = JaggeryTestUtil.openConnection(jaggeryURL);
//        assertNotNull(jaggeryServerConnection, "Connection establishment failure");
//        BufferedReader in = JaggeryTestUtil.inputReader(jaggeryServerConnection);
//        assertNotNull(in, "Input stream failure");
//        String inputLine;
//        while ((inputLine = in.readLine()) != null) {
//            response = inputLine;
//        }
//        in.close();
//        log.info("Response: " + response);
//        assertNotNull(response, "Response cannot be null");
//        assertEquals(response, "test jaggery application value");
//    }
}







