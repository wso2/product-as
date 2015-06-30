/*
* Copyright 2004,2013 The Apache Software Foundation.
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
package org.wso2.appserver.integration.tests.javaee.jsf;

import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.xml.sax.InputSource;


import static org.testng.Assert.assertTrue;

public class JsfEjbTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JsfEjbTestCase.class);
    private static final String webAppFileName = "jsf-greeting.war";
    private static final String webAppName = "jsf-greeting";
    private static final String webAppLocalURL = "/jsf-greeting";
    private TestUserMode userMode;
    private String hostname;

    @Factory(dataProvider = "userModeProvider")
    public JsfEjbTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);

        hostname = asServer.getInstance().getHosts().get("default");
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS) + webAppLocalURL;

        String webAppFilePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "AS" + File.separator + "javaee" + File.separator + "jsf" + File.separator + webAppFileName;
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "test JSF Bean Validation")
    public void testJsfEjb() throws Exception {
        String CalculatorEndpoint = webAppURL + "/index.jsf";

        //here used Commons http client in order to manage same session throughout
        HttpMethod getMethod = new GetMethod(CalculatorEndpoint);
        HttpClient client = new HttpClient();

        client.executeMethod(getMethod);
        String getResponse = getMethod.getResponseBodyAsString();
        log.info("getResponse - " + getResponse);

        //extracting jsf viewState from the get request
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(getResponse));
        Document document = documentBuilder.parse(inputSource);

        //Evaluate XPath against Document
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate("//input[4]/@value", document.getDocumentElement(),
                XPathConstants.NODESET);
        String viewState = nodes.item(0).getNodeValue();

        HttpMethod postMethod = new PostMethod(CalculatorEndpoint);

        NameValuePair[] nameValuePairs = new NameValuePair[4];
        nameValuePairs[0] = new NameValuePair("j_id_4:j_id_6", "John Doe");
        nameValuePairs[1] = new NameValuePair("j_id_4:j_id_7", "Enter");
        nameValuePairs[2] = new NameValuePair("j_id_4_SUBMIT", "1");
        nameValuePairs[3] = new NameValuePair("javax.faces.ViewState", viewState);
        postMethod.setQueryString(nameValuePairs);

        client.executeMethod(postMethod);

        String postResponse = postMethod.getResponseBodyAsString();
        log.info("postResponse - " + postResponse);

        assertTrue(postResponse.contains("Welcome John Doe"), "Response doesn't contain expected data");
        assertTrue(postResponse.contains("Distinct characters in the name: d, e, n, o, j, h"), "Response doesn't contain expected data");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }
}
