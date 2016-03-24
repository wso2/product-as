/*
 * Copyright 2015 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.appserver.integration.tests.webapp.hibernate;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class HibernateExampleTestCase extends ASIntegrationTest {

    private final String webAppName = "hibernate-example";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {
        String warFilePath =
                System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                        File.separator + "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator +
                        "hibernate" + File.separator + webAppName + ".war";
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, warFilePath);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "invoke hibernate webapp", dependsOnMethods = "testWebApplicationDeployment")
    public void testHibernateWebAppInvoke() throws Exception {
        String baseURL = webAppURL + "/" + webAppName + "/EmployeeService/";
        URL endpoint = new URL(baseURL + "add");
        Reader data = new StringReader("<Employees>\n" +
                "<Employee>\n" +
                "    <firstName>Kasun</firstName>\n" +
                "    <lastName>Gajasinghe</lastName>\n" +
                "    <salary>10000</salary>\n" +
                "</Employee>\n" +
                "</Employees>");
        Writer writer = new StringWriter();
        HttpURLConnectionClient.sendPostRequest(data, endpoint, writer, "application/xml");
        String employeeId = writer.toString();
        assertEquals(employeeId, "1"); //the generated id

        String getEndpoint = baseURL + "get";
        String expectedValue = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Employees>" +
                "<Employee><firstName>Kasun</firstName><id>1</id><lastName>Gajasinghe</lastName><salary>10000</salary>" +
                "</Employee></Employees>";
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Accept", "application/xml");

        HttpResponse response = ASHttpRequestUtil.doGet(getEndpoint, headers);
        String actualValue = response.getData();
        assertEquals(actualValue, expectedValue);

        endpoint = new URL(baseURL + "update");
        data = new StringReader("id=1&salary=15000");
        writer = new StringWriter();
        HttpURLConnectionClient.sendPutRequest(data, endpoint, writer, "application/x-www-form-urlencoded");

        response = ASHttpRequestUtil.doGet(getEndpoint, headers);
        expectedValue = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Employees>" +
                "<Employee><firstName>Kasun</firstName><id>1</id><lastName>Gajasinghe</lastName><salary>15000</salary>" +
                "</Employee></Employees>";
        actualValue = response.getData();
        assertEquals(actualValue, expectedValue);

        endpoint = new URL(baseURL + "delete?id=1");

        HttpURLConnectionClient.sendDeleteRequest(endpoint, null);

        response = ASHttpRequestUtil.doGet(getEndpoint, headers);
        expectedValue = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Employees/>"; //empty array
        actualValue = response.getData();
        assertEquals(actualValue, expectedValue);
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
            dependsOnMethods = "testHibernateWebAppInvoke")
    public void testDeleteWebApplication() throws Exception {
        String hostname = "localhost";
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname,sessionCookie, webAppName + ".war");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                        backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }
}
