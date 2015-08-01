/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.appserver.integration.tests.webapp.mgt.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebAppDescriptorTest extends ASIntegrationTest {
    public static final String PASS = "Pass";
    public static final String FAIL = "Fail";
    public static final String SAMPLE_APP_LOCATION =
            FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "AS" + File.separator + "war"
                    + File.separator + "configTesting";
    private static final Log log = LogFactory.getLog(WebAppDescriptorTest.class);
    protected final String webAppFileName;
    protected final String webAppName;
    protected final String webAppLocalURL;
    protected final String hostName = "localhost";
    protected String sampleAppDirectory;
    protected WebAppAdminClient webAppAdminClient;

    public WebAppDescriptorTest(WebAppMode webAppMode) {
        webAppName = webAppMode.getWebAppName().split(".war")[0];
        webAppFileName = webAppMode.getWebAppName();
        webAppLocalURL = File.separator + webAppName;

    }

    protected void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    protected void webApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(sampleAppDirectory + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                webAppName+" web Application Deployment failed");
    }

    /**
     * This sends a request to the webapp and checks if the expected result is returned
     *
     * @param tomcat The expected value for Tomcat
     * @param carbon The expected value for Carbon
     * @param cxf    The expected value for CXF
     * @param spring The expected value for Spring
     * @throws AutomationFrameworkException
     */
    protected void invokeWebApp(boolean tomcat, boolean carbon, boolean cxf, boolean spring)
            throws AutomationFrameworkException {
        String webAppURLLocal = webAppURL + webAppLocalURL;
        Map<String, String> results = toResultMap(runAndGetResultAsString(webAppURLLocal));
        assertEquals(tomcat ? PASS : FAIL, results.get("Tomcat"),"Tomcat test failed");
        assertEquals(carbon ? PASS : FAIL, results.get("Carbon"),"Carbon test failed");
        assertEquals(cxf ? PASS : FAIL, results.get("CXF"),"CXF test failed");
        assertEquals(spring ? PASS : FAIL, results.get("Spring"),"Spring test failed");
    }

    /**
     * Returns a Map after processing the result string
     *
     * @param resultString The string to be processed
     * @return Map
     */
    protected Map<String, String> toResultMap(String resultString) {
        if (resultString == null) {
            log.warn("resultString is null");
            return null;
        }
        resultString = resultString.replace("<status>", "").replace("</status>", "");
        Map<String, String> resultMap = new HashMap<>();
        String[] resultArray = resultString.split(",");
        for (String s : resultArray) {
            String[] temp = s.split("-");
            if (!temp[0].equals("")) {
                resultMap.put(temp[0], temp[1]);
            }
        }
        log.debug(resultMap);
        return resultMap;
    }

    protected String runAndGetResultAsString(String webAppURL) throws AutomationFrameworkException {
        HttpClientUtil client = new HttpClientUtil();
        return client.get(webAppURL).toString();
    }

    protected void deleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + webAppLocalURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);

        Assert.assertEquals(response.getResponseCode(), 302,
                "Response code mismatch. Client request " + "got a response even after web app is undeployed");
    }
}
