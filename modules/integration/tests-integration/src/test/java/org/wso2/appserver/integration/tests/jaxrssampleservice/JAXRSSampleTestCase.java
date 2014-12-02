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

package org.wso2.appserver.integration.tests.jaxrssampleservice;

import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import java.io.*;
import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * This class uploads a jaxrs_sample_02.war and send
 * requests in json,xml and plain text formats.
 */
public class JAXRSSampleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JAXRSSampleTestCase.class);
    private final String hostName = "localhost";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void webApplicationDelete() throws Exception {
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile("jaxrs_sample_02.war", hostName);
        log.info("jaxrs_sample_02.war deleted successfully");
    }

    //Upload a war file and verify
    @Test(groups = "wso2.as", description = "upload war file and verify deployment")
    public void webApplicationUpload() throws Exception {
        WebAppAdminClient jaxwsWebappAdminClient =
                new WebAppAdminClient(backendURL, sessionCookie);
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator + "AS" + File.separator + "jaxrs" + File.separator;
        jaxwsWebappAdminClient.warFileUplaoder(location + File.separator + "jaxrs_sample_02.war");
        boolean isDeployed =
                WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, "jaxrs_sample_02");
        assertTrue(isDeployed, "WebApp not deployed");
    }

    //sends a GET request and verify

    @Test(groups = "wso2.as", description = "invoke JAXRS service from a GET request",
          dependsOnMethods = "webApplicationUpload")
    public void sendGETRequestToJAXRSSample02() throws Exception {

        String endpoint = webAppURL + "/jaxrs_sample_02/services/Starbucks_Outlet_Service/orders/123";
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(endpoint, null);
        log.info(response.getData());
        Assert.assertNotNull(response, "Result cannot be null");
        assertTrue(response.getData().contains("\"orderId\":123}}"));

    }

    //sends a POST request in text/xml format and verify

    @Test(groups = "wso2.as", description = "invoke JAXRS service in text/xml request",
          dependsOnMethods = "webApplicationUpload")
    public void sendTextXMLPOSTRequestToJAXRSSample02() throws Exception {

        URL endpoint = new URL(webAppURL + "/jaxrs_sample_02/services/Starbucks_Outlet_Service/orders");
        Reader data = new StringReader("<Order>\n" +
                                       "    <drinkName>Mocha Flavored Coffee</drinkName>\n" +
                                       "    <additions>Caramel</additions>\n" +
                                       "</Order>");
        Writer writer = new StringWriter();
        HttpURLConnectionClient.sendPostRequest(data, endpoint, writer, "text/xml");
        assertTrue(writer.toString().contains("{\"Order\":{\"additions\":\"Caramel\",\"drinkName\""
                + ":\"Mocha Flavored Coffee\",\"locked\":false,\"orderId\":"));
    }

    //sends a PUT request in application/json format

    @Test(groups = "wso2.as", description = "invoke JAXRS service in application/json request",
          dependsOnMethods = "webApplicationUpload")
    public void sendApplicationJSONPUTRequestToJAXRSSample02() throws Exception {

        URL endpoint = new URL(webAppURL + "/jaxrs_sample_02/services/Starbucks_Outlet_Service/orders");
        Reader data = new StringReader("{\"Order\":{\"orderId\":\"123\",\"additions\":\"Chocolate Chip Cookies\"}}");
        Writer writer = new StringWriter();
        HttpURLConnectionClient.sendPutRequest(data, endpoint, writer, "application/json");
        Assert.assertEquals(writer.toString(), "{\"Order\":{\"additions\":\"Chocolate Chip Cookies\",\"drinkName\":\"" +
                                               "Vanilla Flavored Coffee\",\"locked\":false,\"orderId\":123}}");
    }
}
