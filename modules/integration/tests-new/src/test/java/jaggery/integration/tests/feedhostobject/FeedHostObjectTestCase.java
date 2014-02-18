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

package jaggery.integration.tests.feedhostobject;


import jaggery.integration.tests.util.Utility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This class sends requests to feed.jag and validates the response
 */
public class FeedHostObjectTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(FeedHostObjectTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = {"wso2.as"}, description = "Test feed host object")
    public void testFeed() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(asServer.getWebAppURL() + "/testapp/feed.jag");
        URLConnection jaggeryServerConnection = Utility.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = Utility.inputReader(jaggeryServerConnection);
        assertNotNull(in, "Input stream failure");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
    }

    @Test(groups = {"wso2.as"}, description = "Test feed host object members",
            dependsOnMethods = "testFeed")
    public void testFeedMembers() throws Exception {

        String response = null;
        URL jaggeryURL = new URL("http://localhost:9763/testapp/feed.jag?action=members");
        URLConnection jaggeryServerConnection = jaggeryURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                jaggeryServerConnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assert response != null;
        assertTrue(response.contains("Testing feed members success"));
    }

    @Test(groups = {"wso2.as"}, description = "Test feed host object toXML",
            dependsOnMethods = "testFeedMembers")
    public void testFeedXML() throws Exception {

        String response = null;
        URL jaggeryURL = new URL("http://localhost:9763/testapp/feed.jag?action=xml");
        URLConnection jaggeryServerConnection = jaggeryURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                jaggeryServerConnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertEquals(response, "Feed to XML success");
    }

    @Test(groups = {"wso2.as"}, description = "Test feed host object toString",
            dependsOnMethods = "testFeedXML")
    public void testFeedToString() throws Exception {

        String response = null;
        URL jaggeryURL = new URL("http://localhost:9763/testapp/feed.jag?action=string");
        URLConnection jaggeryServerConnection = jaggeryURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                jaggeryServerConnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertEquals(response, "Feed to String success");
    }
}
