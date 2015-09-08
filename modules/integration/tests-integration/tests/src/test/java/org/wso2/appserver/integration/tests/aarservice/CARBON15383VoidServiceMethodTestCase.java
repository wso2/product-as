/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.tests.aarservice;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpClient;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class contains test case to test the response http status code for a request that send to
 * a service method which has void as the return type
 */
public class CARBON15383VoidServiceMethodTestCase extends ASIntegrationTest {
    private final String SERVICE_ARCHIVE = "Mepinandouttest.aar";
    private ServerConfigurationManager serverManager;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public CARBON15383VoidServiceMethodTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][] { new TestUserMode[] { TestUserMode.SUPER_TENANT_ADMIN } };
    }

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(userMode);
        serverManager = new ServerConfigurationManager(asServer);
        // load the custom axis2 config
        String axis2configFileLocation =
                TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator +
                        "AS"+File.separator+"axismepconfig"+File.separator+"axis2.xml";
        File axis2Config = new File(axis2configFileLocation);

        serverManager.applyConfiguration(axis2Config);
        super.init(userMode);
    }

    @Test(groups = "wso2.as", description = "Upload aar service and verify deployment")
    public void testAarServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient = new AARServiceUploaderClient(backendURL, sessionCookie);
        aarServiceUploaderClient
                .uploadAARFile(SERVICE_ARCHIVE, FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        SERVICE_ARCHIVE, "");

        assertTrue(isServiceDeployed("HelloServiceTest"), "Axis service is not uploaded successfully");
    }

    @Test(groups = { "wso2.as" },
            description = "Send a request to an axis service method which has void as "
                    + "the return type and check the response http status code", dependsOnMethods = "testAarServiceUpload")
    public void testServiceMethodReturnTypeVoid() throws Exception {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("SOAPAction", "urn:returnVoid");
        headers.put("Content-Type", "text/xml;charset=UTF-8");
        SimpleHttpClient simpleHttpClient = new SimpleHttpClient();
        String payload = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/"
                + "\" xmlns:typ=\"http://www.wso2.org/types\">\n" + "   <soapenv:Header/>" + "<soapenv:Body>"
                + "      <typ:getVoid/>\n" + "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        HttpResponse response = simpleHttpClient
                .doPost(getServiceUrl("HelloServiceTest"), headers, payload, "text/xml");
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK,
                "Wrong http status code has been recieved in " + userMode);
    }

    @AfterClass(alwaysRun = true)
    public void restoreServer() throws Exception {
        String carbonHome = System.getProperty("carbon.home");
        FileManager.deleteFile(carbonHome + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "axis2services" + SERVICE_ARCHIVE);
        serverManager.restoreToLastConfiguration();
    }

}
