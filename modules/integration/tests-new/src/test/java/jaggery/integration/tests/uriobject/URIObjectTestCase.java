/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package jaggery.integration.tests.uriobject;

import jaggery.integration.tests.util.Utility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class sends requests to uri.jag and validates the response
 */
public class URIObjectTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(URIObjectTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.as"}, description = "Test URI object")
    public void testURI() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(asServer.getWebAppURL() + "/testapp/uri.jag");
        URLConnection jaggeryServerConnection = Utility.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = Utility.inputReader(jaggeryServerConnection);
        assertNotNull(in, "Input stream failure");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response: " + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response, "dir0 element is : testapp ,page element is : uri.jag");
    }

    @Test(groups = {"wso2.as"}, description = "Test urlMappings config",
            dependsOnMethods = "testURI")
    public void testURIUrlMappingsConfig() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(asServer.getWebAppURL() + "/testapp/uri/");
        URLConnection jaggeryServerConnection = Utility.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = Utility.inputReader(jaggeryServerConnection);
        assertNotNull(in, "Input stream failure");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response: " + response);
        assertNotNull(response, "Result cannot be null");
    }


    @Test(groups = {"wso2.as"}, description = "Test URI operations for dir 4",
            dependsOnMethods = "testURIUrlMappingsConfig")
    public void testURIOperationsDir() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(asServer.getWebAppURL() + "/testapp/uri/aa/bb/");
        URLConnection jaggeryServerConnection = Utility.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = Utility.inputReader(jaggeryServerConnection);
        assertNotNull(in, "Input stream failure");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response: " + response);
        assertEquals(response, "dir0 element is : testapp " + ",dir1 element is : uri ,dir2 " +
                "element " + "is : aa " + ",dir3 element is : bb");
    }

}
