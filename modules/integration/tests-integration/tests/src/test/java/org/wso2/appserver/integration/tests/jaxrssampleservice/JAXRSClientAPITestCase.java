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

package org.wso2.appserver.integration.tests.jaxrssampleservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertTrue;

public class JAXRSClientAPITestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(JAXRSClientAPITestCase.class);
    private final String testWebappName = "jaxrs-client-api-test-webapp";
    private WebAppAdminClient webAppAdminClient = null;
    private String orderId = "123";
    private String hostParam;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient =
                new WebAppAdminClient(backendURL, sessionCookie);
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "jaxrs" + File.separator;
        webAppAdminClient.uploadWarFile(location + File.separator + "jaxrs_starbucks_service.war");
        boolean isDeployed =
                WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, "jaxrs_starbucks_service");
        assertTrue(isDeployed, "WebApp not deployed");
        URL serverURL = new URL(webAppURL);
        hostParam = "Host=" + serverURL.getHost() + "&Port=" + serverURL.getPort();
    }

    @Test(groups = "wso2.as", description = "Upload JAXRS-Client API Test WAR and verify deployment")
    public void testJaxrsClientAPIWARUpload() throws Exception {
        String springWarFilePath =
                System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                        File.separator + "artifacts" + File.separator + "AS" + File.separator + "jaxrs" +
                        File.separator + "client" + File.separator + testWebappName + ".war";
        webAppAdminClient.uploadWarFile(springWarFilePath);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, testWebappName));
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS Client API POST Method",
            dependsOnMethods = "testJaxrsClientAPIWARUpload")
    public void testInvokePOSTMethodUsingJAXRSClient() throws Exception {
        testClientPOSTMethod("jax-rs");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS Client API GET Method",
            dependsOnMethods = "testInvokePOSTMethodUsingJAXRSClient")
    public void testInvokeGETMethodUsingJAXRSClient() throws Exception {
        testClientGETMethod("jax-rs");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS Client API PUT Method",
            dependsOnMethods = "testInvokeGETMethodUsingJAXRSClient")
    public void testInvokePUTMethodUsingJAXRSClient() throws Exception {
        testClientPUTMethod("jax-rs");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS Client API DELETE Method",
            dependsOnMethods = "testInvokePUTMethodUsingJAXRSClient")
    public void testInvokeDELETEMethodUsingJAXRSClient() throws Exception {
        testClientDELETEMethod("jax-rs");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS-CXF Client API POST Method",
            dependsOnMethods = "testInvokeDELETEMethodUsingJAXRSClient")
    public void testInvokePOSTMethodUsingCXFClient() throws Exception {
        testClientPOSTMethod("cxf");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS-CXF Client API GET Method",
            dependsOnMethods = "testInvokePOSTMethodUsingCXFClient")
    public void testInvokeGETMethodUsingCXFClient() throws Exception {
        testClientGETMethod("cxf");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS-CXF Client API PUT Method",
            dependsOnMethods = "testInvokeGETMethodUsingCXFClient")
    public void testInvokePUTMethodUsingCXFClient() throws Exception {
        testClientPUTMethod("cxf");
    }

    @Test(groups = "wso2.as", description = "Invoke JAXRS-CXF Client API DELETE Method",
            dependsOnMethods = "testInvokePUTMethodUsingCXFClient")
    public void testInvokeDELETEMethodUsingCXFClient() throws Exception {
        testClientDELETEMethod("cxf");
    }

    private void testClientPOSTMethod(String clientID) throws IOException {
        String endpoint = webAppURL + "/" + testWebappName + "/" + clientID;
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(endpoint, "HTTPMethod=POST" + "&" + hostParam);
        log.info(response.getData());
        Assert.assertNotNull(response, "Result cannot be null");
        String expectedResponse = "{\"Order\":{\"additions\":\"Caramel\"," +
                "\"drinkName\":\"Mocha Flavored Coffee\",\"locked\":false,\"orderId\":";
        assertTrue(response.getData().contains(expectedResponse));
        orderId = getOrderId(response.getData());
        log.info("OrderId : " + orderId);
    }

    private void testClientGETMethod(String clientId) throws IOException {
        String endpoint = webAppURL + "/" + testWebappName + "/" + clientId;
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(endpoint, "HTTPMethod=GET&OrderId=" + orderId +
                "&" + hostParam);
        log.info(response.getData());
        Assert.assertNotNull(response, "Result cannot be null");
        assertTrue(response.getData().contains("\"orderId\":\"" + orderId + "\"}}"));
    }

    private void testClientPUTMethod(String clientId) throws IOException {
        String endpoint = webAppURL + "/" + testWebappName + "/" + clientId;
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(endpoint, "HTTPMethod=PUT&OrderId=" + orderId +
                "&" + hostParam);
        log.info(response.getData());
        Assert.assertEquals(response.getData(), "{\"Order\":{\"additions\":\"Chocolate Chip Cookies\"," +
                "\"drinkName\":\"Mocha Flavored Coffee\",\"locked\":false,\"orderId\":\"" + orderId + "\"}}");
    }

    private void testClientDELETEMethod(String clientId) throws IOException {
        String endpoint = webAppURL + "/" + testWebappName + "/" + clientId;
        HttpURLConnectionClient.sendGetRequest(endpoint, "HTTPMethod=DELETE&OrderId=" + orderId + "&" + hostParam);
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(endpoint, "HTTPMethod=GET&OrderId=" + orderId +
                "&" + hostParam);
        assertTrue(response.getData().isEmpty());
    }


    private String getOrderId(String response) {
        String regexString = "(?<=\"orderId\")\\s*:\\s*\"([a-z\\-0-9]*)";
        String match = null;

        Pattern regex = Pattern.compile(regexString);
        Matcher m = regex.matcher(response);
        if (m.find()) {
            match = m.group();
            match = match.trim().replaceAll("\\s*:\\s*\"", "");
        }

        return match;
    }

}
