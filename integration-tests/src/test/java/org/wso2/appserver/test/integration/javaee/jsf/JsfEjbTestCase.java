/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appserver.test.integration.javaee.jsf;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wso2.appserver.test.integration.TestBase;
import org.xml.sax.InputSource;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static org.testng.Assert.assertTrue;

public class JsfEjbTestCase extends TestBase {
    private static final String webAppLocalURL = "/jsf-greeting";

    @Test(description = "test JSF Bean Validation")
    public void testJsfEjb() throws Exception {
        String calculatorEndpoint = getBaseUrl() + webAppLocalURL + "/index.jsf";

        //here used Commons http client in order to manage same session throughout
        HttpMethod getMethod = new GetMethod(calculatorEndpoint);
        HttpClient client = new HttpClient();

        client.executeMethod(getMethod);
        String getResponse = getMethod.getResponseBodyAsString();

        String cookie = null;
        for (Header header : getMethod.getResponseHeaders()) {
            if (header.getValue().contains("JSESSIONID")) {
                cookie = header.getValue();
            }
        }

        //extracting jsf viewState from the get request
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(getResponse));
        Document document = documentBuilder.parse(inputSource);

        //Evaluate XPath against Document
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate("//input[4]/@value", document.getDocumentElement(),
                XPathConstants.NODESET);
        String viewState = nodes.item(0).getNodeValue();

        HttpMethod postMethod = new PostMethod(calculatorEndpoint);

        NameValuePair[] nameValuePairs = new NameValuePair[4];
        nameValuePairs[0] = new NameValuePair("j_id_5:j_id_7", "John Doe");
        nameValuePairs[1] = new NameValuePair("j_id_5:j_id_8", "Enter");
        nameValuePairs[2] = new NameValuePair("j_id_5_SUBMIT", "1");
        nameValuePairs[3] = new NameValuePair("javax.faces.ViewState", viewState);
        postMethod.setQueryString(nameValuePairs);
        postMethod.addRequestHeader("Cookie", cookie);
        client.executeMethod(postMethod);

        String postResponse = postMethod.getResponseBodyAsString();

        assertTrue(postResponse.contains("Welcome John Doe"), "Response doesn't contain expected data");
        assertTrue(postResponse.contains("Distinct characters in the name"),
                "Response doesn't contain expected data");
    }
}
