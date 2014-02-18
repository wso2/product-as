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
package org.wso2.carbon.integration.test.webappsampleservice;

import org.apache.catalina.util.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.user.mgt.UserManagementClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/*
   This class adds user to web application, checks authentication,  adds resources
*/
public class WebAppSampleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(WebAppSampleTestCase.class);
    private static final String USER_NAME = "OldUser";
    private static final String PASSWORD = "OldUser";
    private static final String RESOURCE_VALUE = "OldModest";
    private static final String RESOURCE_PATH = "path/to/kicha";
    private static final String CACHE_KEY = "cacheKey3";
    private static final String CACHE_VALUE = "cacheValue3";
    private static final String CLIENT_AUTH_HEADER = "authorization";
    private HttpClient httpClient = new HttpClient();


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void delete() throws Exception {
        UserManagementClient userManagementClient = new UserManagementClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());
        userManagementClient.deleteUser("OldUser");
    }

    @Test(groups = "wso2.as", description = "Login to WSO2 Carbon User Manage Usage Demo")
    public void testUserManagerAndAuthenticationDemo() throws IOException {
        String urlOne = asServer.getWebAppURL() + "/example/carbon" + "/usermgt/index.jsp?username=" +
                USER_NAME + "&password=" + PASSWORD;
        GetMethod getMethodOne = new GetMethod(urlOne);
        try {
            log.info("Adding test user to User Realm");
            int statusCode = httpClient.executeMethod(getMethodOne);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethodOne.getStatusLine());
            }

        } finally {
            getMethodOne.releaseConnection();
        }

        String urlTwo = asServer.getWebAppURL() + "/example/carbon" + "/authentication/login.jsp?" +
                "username=" + USER_NAME + "&password=" + PASSWORD;
        GetMethod getMethodTwo = new GetMethod(urlTwo);

        try {
            log.info("Authenticating test user with carbon user realm");
            int statusCode = httpClient.executeMethod(getMethodTwo);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethodTwo.getStatusLine());
            } else {
                boolean success = Boolean.
                        parseBoolean(getMethodTwo.getResponseHeader("logged-in").getValue());
                if (success) {
                    String userName = getMethodTwo.getResponseHeader("username").getValue();
                    assertEquals(userName, USER_NAME);
                } else {
                    fail("Authentication failed for test user");

                }
            }

        } finally {
            getMethodTwo.releaseConnection();
        }

    }

    @Test(groups = {"wso2.as"}, description = "Adding a resource",
            dependsOnMethods = "testUserManagerAndAuthenticationDemo")
    public void testRegistryUsageDemo() throws Exception {
        log.info("Running registry usage demo test case");
        String urlOne = asServer.getWebAppURL() + "/example/carbon" + "/registry/index.jsp?add=" +
                "Add&resourcePath=" + RESOURCE_PATH + "&value=" + RESOURCE_VALUE;
        GetMethod getMethodOne = new GetMethod(urlOne);

        try {
            log.info("Adding test resource to registry");
            int statusCode = httpClient.executeMethod(getMethodOne);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethodOne.getStatusLine());
            }
        } finally {
            getMethodOne.releaseConnection();
        }

        String urlTwo = asServer.getWebAppURL() + "/example/carbon" + "/registry/index.jsp?view" +
                "=View&resourcePath=" + RESOURCE_PATH;
        GetMethod getMethodTwo = new GetMethod(urlTwo);
        try {
            log.info("Getting test resource content from registry");
            int statusCode = httpClient.executeMethod(getMethodTwo);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethodTwo.getStatusLine());
            } else {
                String resourceContent = getMethodTwo.
                        getResponseHeader("resource-content").getValue();
                assertEquals(resourceContent, RESOURCE_VALUE);
            }
        } finally {
            getMethodTwo.releaseConnection();
        }
    }


    //Folowing test case is failing because of the new changes
    //to carbon caching (in 4.2.0 release)
    // need to create anew test case for carbon caching demo

    @Test(groups = {"wso2.as"}, description = "Add a cache",
            dependsOnMethods = "testRegistryUsageDemo", enabled = false)
    public void testCarbonCachingDemo() throws Exception {
        log.info("Running carbon caching demo test case");
        String urlOne = asServer.getWebAppURL() + "/example/carbon" + "/caching/index.jsp?add=" +
                "Add&key=" + CACHE_KEY + "&value=" + CACHE_VALUE;
        GetMethod getMethodOne = new GetMethod(urlOne);

        try {
            log.info("Adding test cache value to carbon context cache");
            int statusCode = httpClient.executeMethod(getMethodOne);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethodOne.getStatusLine());
            }
        } finally {
            getMethodOne.releaseConnection();
        }

        String urlTwo = asServer.getWebAppURL() + "/example/carbon" + "/caching/index.jsp?view=" +
                "View&key=" + CACHE_KEY;
        GetMethod getMethodTwo = new GetMethod(urlTwo);
        try {
            log.info("Getting test cache value from carbon context");
            int statusCode = httpClient.executeMethod(getMethodTwo);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethodTwo.getStatusLine());
            } else {
                String cacheValue = getMethodTwo.
                        getResponseHeader("cache-value").getValue();
                assertEquals(cacheValue, CACHE_VALUE);
            }
        } finally {
            getMethodTwo.releaseConnection();
        }
    }



    // disabled because depends on caching test case

    @Test(groups = {"wso2.as"}, description = "Basic authentication",
            dependsOnMethods = "testCarbonCachingDemo", enabled = false)
    public void testBasicAuth() throws Exception {
        log.info("Running Basic Authentication test case for example webapp ...");
        String userName = "admin";
        String pwd = "admin";
        String resourceURL = asServer.getWebAppURL() + "/example/jsp/security/protected/index.jsp";

        // the first access attempt should be challenged
        Map<String, List<String>> reqHeaders1 =
                new HashMap<String, List<String>>();
        Map<String, List<String>> respHeaders1 =
                new HashMap<String, List<String>>();

        ByteChunk bc = new ByteChunk();
        int rc = getResponseCode(resourceURL, bc, 1000000, reqHeaders1,
                respHeaders1);

        assertEquals(401, rc);
        assertNull(bc.toString());

        // the second access attempt should be sucessful
        String credentials = userName + ":" + pwd;
        byte[] credentialsBytes = ByteChunk.convertToBytes(credentials);
        String base64auth = Base64.encode(credentialsBytes);
        String authLine = "Basic " + base64auth;

        List<String> auth = new ArrayList<String>();
        auth.add(authLine);
        Map<String, List<String>> reqHeaders2 = new HashMap<String, List<String>>();
        reqHeaders2.put(CLIENT_AUTH_HEADER, auth);

        Map<String, List<String>> respHeaders2 =
                new HashMap<String, List<String>>();
        bc.reset();
        rc = getResponseCode(resourceURL, bc, 1000000, reqHeaders2,
                respHeaders2);
        assertEquals(200, rc);
    }

    private int getResponseCode(String path, ByteChunk out, int readTimeout,
                                Map<String, List<String>> reqHead,
                                Map<String, List<String>> resHead) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setReadTimeout(readTimeout);
        if (reqHead != null) {
            for (Map.Entry<String, List<String>> entry : reqHead.entrySet()) {
                StringBuilder valueList = new StringBuilder();
                for (String value : entry.getValue()) {
                    if (valueList.length() > 0) {
                        valueList.append(',');
                    }
                    valueList.append(value);
                }
                connection.setRequestProperty(entry.getKey(),
                        valueList.toString());
            }
        }
        connection.connect();
        int rc = connection.getResponseCode();
        if (resHead != null) {
            Map<String, List<String>> head = connection.getHeaderFields();
            resHead.putAll(head);
        }
        if (rc == HttpServletResponse.SC_OK) {
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(is);
                byte[] buf = new byte[2048];
                int rd = 0;
                while ((rd = bis.read(buf)) > 0) {
                    out.append(buf, 0, rd);
                }
            } finally {
                if (bis != null) {
                    bis.close();
                }
            }
        }
        return rc;
    }
}
