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

package jaggery.integration.tests.applicationobject;

import jaggery.integration.tests.util.Utility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.jaggeryservices.JaggeryApplicationUploaderClient;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.webapp.WebAppUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;

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

    @BeforeTest(alwaysRun = true) //uploads testapp.zip file and verify  deployment
    public void jaggeryFileUpload() throws Exception {
        super.init();
        JaggeryApplicationUploaderClient jaggeryAppUploaderClient =
                new JaggeryApplicationUploaderClient(asServer.getBackEndUrl(),
                        asServer.getSessionCookie());
        jaggeryAppUploaderClient.uploadJaggeryFile("testapp.zip",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "jaggery" + File.separator +
                        "testapp.zip");
        WebAppUtil.waitForWebAppDeployment(asServer.getWebAppURL() + "/testapp/application.jag",
                "test jaggery application value");   // verifying the deployment
        log.info("testapp.zip file uploaded successfully");
    }

    @AfterTest(alwaysRun = true)
    public void jaggeryFileDelete() throws Exception {  // deletes the testapp.zip web app file
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        webAppAdminClient.deleteWebAppFile("testapp");
        log.info("testapp deleted successfully");
    }

    @Test(groups = "wso2.as", description = "invoke applicationjag")
    public void testApplication() throws Exception {
        String response = null;
        URLConnection jaggeryServerConnection;
        URL jaggeryURL = new URL(asServer.getWebAppURL() + "/testapp/application.jag");
        jaggeryServerConnection = Utility.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = Utility.inputReader(jaggeryServerConnection);
        assertNotNull(in, "Input stream failure");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response: " + response);
        assertNotNull(response, "Response cannot be null");
        assertEquals(response, "test jaggery application value");
    }
}







