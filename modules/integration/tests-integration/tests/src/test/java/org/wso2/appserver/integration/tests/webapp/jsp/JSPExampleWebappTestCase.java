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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class JSPExampleWebappTestCase extends ASIntegrationTest {

    private final String webAppName = "example";
    private final String webAppContext = "/" + webAppName;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "Invoking jsp examples in the example web application")
    public void testInvokeJSPSamples() throws Exception {
        String webAppBaseURL = webAppURL + webAppContext + "/jsp/";

        // ErrorPage
        String url;
        HttpResponse response;
        url = webAppBaseURL + "error/err.jsp";
        try {
            response = HttpRequestUtil.sendGetRequest(url, "name=audi");
            fail("Invoking the error page should return response with status code 500. This should throw an exception. " +
                    "url - " + url);
        } catch (IOException e) {
            // this is the expected behavior
            String errorMessage = "Server returned HTTP response code: 500 for URL:" +
                    " http://localhost:9763/example/jsp/error/err.jsp?name=audi";
            assertEquals(e.getMessage(), errorMessage, "Error HTTP status code 500 did not return for url - " + url);
        }
        response = HttpRequestUtil.sendGetRequest(url, "name=integra");
        assertEquals(response.getResponseCode(), 200, "200 OK HTTP code did not return for url - " + url);

        // jsp:include
        url = webAppBaseURL + "include/include.jsp";
        response = HttpRequestUtil.sendGetRequest(url, null);
        String expectedString = "To get the current time in ms by including the output of another JSP";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        // jsp:forward
        url = webAppBaseURL + "forward/forward.jsp";
        response = HttpRequestUtil.sendGetRequest(url, null);
        expectedString = "VM Memory usage";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        // JSP-to-Servlet-to-JSP
        url = webAppBaseURL + "jsptoserv/jsptoservlet.jsp";
        response = HttpRequestUtil.sendGetRequest(url, null);
        expectedString = "I have been invoked byservletToJspServlet.";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));


    }

    @Test(groups = "wso2.as", description = "test jsp 1.0 tag plugins")
    public void testJSPTagPlugins() throws Exception {
        String webAppBaseURL = webAppURL + webAppContext + "/jsp/tagplugin/";
        //tag plugins - c:if check
        String url = webAppBaseURL + "if.jsp";
        HttpResponse response = HttpRequestUtil.sendGetRequest(url, null);
        String expectedString = "The result of testing for (1==1) is: true";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "It's true that (2>0)! Working.";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        //tag plugins - c:forEach check
        url = webAppBaseURL + "foreach.jsp";
        response = HttpRequestUtil.sendGetRequest(url, null);
        expectedString = "One";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "Two";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "Three";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "Four";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        //tag plugins - c:choose
        url = webAppBaseURL + "choose.jsp";
        response = HttpRequestUtil.sendGetRequest(url, null);
        String actualString = response.getData().replaceAll("\\n|\\r|\\s", "");
        expectedString = "#0:Huh?";
        assertTrue(actualString.contains(expectedString),
                getFailMessage(actualString, expectedString));
        expectedString = "#1:One!";
        assertTrue(actualString.contains(expectedString),
                getFailMessage(actualString, expectedString));
        expectedString = "#4:Four!";
        assertTrue(actualString.contains(expectedString),
                getFailMessage(actualString, expectedString));
    }


        /**
         * Generates a meaningful fail message for String#contains checks.
         */
    private String getFailMessage(String completeString, String expectedString) {
        return String.format("String contains validation failed " +
                "Expected substring, %s, in \n %s.", expectedString, completeString);
    }

}