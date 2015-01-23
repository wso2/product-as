/*
 *Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appserver.integration.tests.javaee;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public abstract class WebappDeploymentTestCase extends
                                    ASIntegrationTest {

    private static final Log log = LogFactory.getLog(WebappDeploymentTestCase.class);

    public static final String PASS = "Pass";
    public static final String FAIL = "Fail";
    private final String hostName = "localhost";

    private String webAppFileName;
    private String webAppName;
    //private String webAppURL;
    protected WebAppAdminClient webAppAdminClient;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

    }

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie,
                webAppName), "Web Application unDeployment failed");

    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void webApplicationDeploymentTest() throws Exception {
        webAppAdminClient
                .warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation()
                                 + "artifacts" + File.separator + "AS" + File.separator
                                 + "javaee" + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie,
                webAppName), "Web Application Deployment failed");
    }

//    @Test(groups = "wso2.as", description = "Invoke web application", dependsOnMethods = "webApplicationDeploymentTest")
//    public void testInvokeWebApp() throws Exception {
//        Map<String, String> results = toResultMap(runAndGetResultAsString(webAppURL));
//        assertEquals(PASS, results.get("Tomcat"));
//        assertEquals(PASS, results.get("Carbon"));
//        assertEquals(PASS, results.get("CXF"));
//        assertEquals(PASS, results.get("Spring"));
//    }

    protected String runAndGetResultAsString(String webAppURL) throws Exception {
        log.info("Webapp URL : " + webAppURL);
        HttpURLConnection httpCon = null;
        String text = null;
        boolean responseCode = true;

        int responseCode1;
        try {
            URL e = new URL(webAppURL);
            httpCon = (HttpURLConnection)e.openConnection();
            httpCon.setConnectTimeout(30000);
            InputStream in = httpCon.getInputStream();
            text = getStringFromInputStream(in);
            responseCode1 = httpCon.getResponseCode();
            in.close();
        } catch (Exception var12) {
            log.error("Failed to get the response " + var12);
            throw new Exception("Failed to get the response :" + var12);
        } finally {
            if(httpCon != null) {
                httpCon.disconnect();
            }

        }

        Assert.assertEquals(responseCode1, 200, "Response code not 200 for " + webAppURL);

        return text;
    }

    protected OMElement runAndGetResultAsOM(String webAppURL) throws Exception {
        log.info("Endpoint : " + webAppURL);
        HttpURLConnection httpCon = null;
        String xmlContent = null;
        boolean responseCode = true;

        int responseCode1;
        try {
            URL e = new URL(webAppURL);
            httpCon = (HttpURLConnection)e.openConnection();
            httpCon.setConnectTimeout(30000);
            httpCon.setRequestProperty("Accept", "application/xml");
            InputStream in = httpCon.getInputStream();
            xmlContent = getStringFromInputStream(in);
            responseCode1 = httpCon.getResponseCode();
            in.close();
        } catch (Exception var12) {
            log.error("Failed to get the response " + var12);
            throw new Exception("Failed to get the response :" + var12);
        } finally {
            if(httpCon != null) {
                httpCon.disconnect();
            }

        }

        Assert.assertEquals(responseCode1, 200, "Response code not 200");
        if(xmlContent != null) {
            try {
                return AXIOMUtil.stringToOM(xmlContent);
            } catch (XMLStreamException var11) {
                log.error("Error while processing response to OMElement" + var11);
                throw new XMLStreamException("Error while processing response to OMElement" + var11);
            }
        } else {
            return null;
        }
    }

    protected Map<String, String> toResultMap(String resultString)
            throws Exception {
        if (resultString == null) {
            log.error("resultString is null");
            return null;
        }
        Map<String, String> resultMap = new HashMap<String, String>();
        String[] resultArray = resultString.split("\n");
        for (String s : resultArray) {
            int i = s.indexOf('=');
            String key = "", value = "";
            if (i > 0) {
                key = s.substring(0, i);
            }
            if (s.length() > i+1) {
                value = s.substring(i+1);
            }
            resultMap.put(key, value);
        }
        log.debug(resultMap);
        return resultMap;
    }

    private String getStringFromInputStream(InputStream in) throws Exception {
        InputStreamReader reader = new InputStreamReader(in);
        char[] buff = new char[1024];
        StringBuilder retValue = new StringBuilder();

        int i;
        try {
            while((i = reader.read(buff)) > 0) {
                retValue.append(new String(buff, 0, i));
            }
        } catch (Exception var6) {
            log.error("Failed to get the response " + var6);
            throw new Exception("Failed to get the response :" + var6);
        }

        return retValue.toString();
    }


    public String getWebAppFileName() {
        return webAppFileName;
    }

    public String getWebAppName() {
        return webAppName;
    }

    public void setWebAppFileName(String webAppFileName) {
        this.webAppFileName = webAppFileName;
    }

    public void setWebAppName(String webAppName) {
        this.webAppName = webAppName;
    }
    public String getWebAppURL() {
        return webAppURL;
    }

    public void setWebAppURL(String webAppURL) {
        this.webAppURL = webAppURL;
    }

}
