/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.appserver.integration.tests.jaggery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.JaggeryApplicationUploaderClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.appserver.integration.tests.jaggery.utils.JaggeryTestUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class sends requests to /usermanager/index.jag and validates the response
 */
public class WSAS1886UserManagerTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(WSAS1886UserManagerTest.class);
    private final String hostName = "localhost";

    @BeforeClass(alwaysRun = true) //uploads usermanager.zip file and verify  deployment
    public void jaggeryFileUpload() throws Exception {
        super.init();
        JaggeryApplicationUploaderClient jaggeryAppUploaderClient = new JaggeryApplicationUploaderClient(backendURL,
                sessionCookie);
        jaggeryAppUploaderClient
                .uploadJaggeryFile("usermanager.zip", FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "jaggery" + File.separator +
                        "usermanager.zip");
        JaggeryTestUtil.isJaggeryAppDeployed("usermanager", backendURL, sessionCookie);
        log.info("usermanager.zip file uploaded successfully");
    }

    @AfterClass(alwaysRun = true) // deletes the usermanager jaggery app
    public void jaggeryFileDelete() throws Exception {
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile("usermanager", hostName);
        log.info("usermanager deleted successfully");
    }

    @Test(groups = "wso2.as", description = "invoke index.jag")
    public void testApplication() throws Exception {
        String response = null;
        URLConnection jaggeryServerConnection;
        URL jaggeryURL = new URL(getWebAppURL(WebAppTypes.JAGGERY) + "/usermanager/index.jag");
        jaggeryServerConnection = JaggeryTestUtil.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        boolean status;
        try {
            BufferedReader in = JaggeryTestUtil.inputReader(jaggeryServerConnection);
            status = true;
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response = inputLine;
            }

            in.close();
        } catch (NullPointerException e) {
            status = false;
        }
        assertTrue(status, "Input stream failure");

        String expectedJsonString = "{\"currentList\" : [\"admin\", \"testu1\", \"testu2\", \"testu3\"],"
                + " \"newUserListBefore\" : [\"admin\", \"chamila\", \"testu1\", \"testu2\", \"testu3\"],"
                + " \"newUserDetails\" : {\"um\" : {\"server\" : {\"url\" : \"local:/\"}, \"tenantId\" : "
                + "-1234, \"manager\" : {}, \"authorizer\" : {}}, \"tenantId\" : -1234, \"username\" : "
                + "\"chamila\"}, \"userExists\" : true, \"newUserListAfter\" : [\"admin\", \"testu1\", "
                + "\"testu2\", \"testu3\"], \"adminClaims\" : [{}], \"roleList\" : [\"admin\", "
                + "\"Internal/everyone\"], \"roleExist\" : false}";

        JSONObject expectedJsonObj = new JSONObject(expectedJsonString);
        JSONObject responseJsonObject = new JSONObject(response);
        log.info("Response: " + response);

        JSONAssert.assertEquals(expectedJsonObj, responseJsonObject, false);
    }
}







