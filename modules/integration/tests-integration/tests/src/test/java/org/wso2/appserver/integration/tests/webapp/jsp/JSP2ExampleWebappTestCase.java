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
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import static org.testng.Assert.assertTrue;

public class JSP2ExampleWebappTestCase extends ASIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "Invoking jsp examples in the example web application")
    public void testInvokeJSP2Samples() throws Exception {
        String webAppName = "example";
        String webAppContext = "/" + webAppName;
        String webAppBaseURL = webAppURL + webAppContext + "/jsp/jsp2/";

        String url = webAppBaseURL + "simpletag/hello.jsp";
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(url, null);
        String expectedString = "Hello, world!";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        url = webAppBaseURL + "simpletag/repeat.jsp";
        response = ASHttpRequestUtil.sendGetRequest(url, null);
        expectedString = "Invocation 1 of 5";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "Invocation 5 of 5";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        url = webAppBaseURL + "simpletag/book.jsp";
        response = ASHttpRequestUtil.sendGetRequest(url, null);
        expectedString = "The Lord of the Rings";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "J. R. R. Tolkein";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "THE LORD OF THE RINGS";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));
        expectedString = "J. R. R. TOLKEIN";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

        url = webAppBaseURL + "tagfiles/hello.jsp";
        response = ASHttpRequestUtil.sendGetRequest(url, null);
        expectedString = "Hello, world!";
        assertTrue(response.getData().contains(expectedString),
                getFailMessage(response.getData(), expectedString));

    }

    /**
     * Generates a meaningful fail message for String#contains checks.
     */
    private String getFailMessage(String completeString, String expectedString) {
        return String.format("String contains validation failed " +
                "Expected substring, %s, in \n %s.", expectedString, completeString);
    }

}
