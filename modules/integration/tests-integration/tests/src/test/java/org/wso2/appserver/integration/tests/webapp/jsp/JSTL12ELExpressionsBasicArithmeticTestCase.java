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
package org.wso2.appserver.integration.tests.webapp.jsp;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class JSTL12ELExpressionsBasicArithmeticTestCase extends ASIntegrationTest {
    private final String webAppFileName = "jstl12-example.war";
    private final String webAppName = "jstl12-example";
    private final String webAppContext = "/" + webAppName;
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public JSTL12ELExpressionsBasicArithmeticTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);

    }

    @Test(groups = "wso2.as", description = "Deploying JSTL web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as", description = "Test EL expressions",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApp() throws Exception {
        String webAppURLLocal = null;
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + webAppContext + "/arithmetic.jsp";
        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            webAppURLLocal = webAppURL + "/webapps" + webAppContext + "/arithmetic.jsp";
        }

        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        assertEquals(response.getResponseCode(), 200);
        String responseData = response.getData().trim();

        String defaultJndiInfoPath = FrameworkPathUtil.getSystemResourceLocation()
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "JSTL12ELExpressionsBasicArithmeticTestCase.properties";
        BufferedReader in = new BufferedReader(new FileReader(defaultJndiInfoPath));

        Properties correctValues = new Properties();
        correctValues.load(in);

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(responseData));
        NodeList nodes = (NodeList) xpath.evaluate("/Arithmetics/Arithmetic", inputSource, XPathConstants.NODESET);

        assertNotNull(nodes);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String expression = element.getElementsByTagName("Expression").item(0).getTextContent();
            String evaluatedValue = element.getElementsByTagName("Value").item(0).getTextContent();

            String correctValue = correctValues.getProperty(expression);

            assertEquals(correctValue, evaluatedValue, "El expression validation failed for the expression - "
                    + expression);
        }
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
            dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                        backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + webAppContext;
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed");
    }


    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_ADMIN},
        };
    }


}
