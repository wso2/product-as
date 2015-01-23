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
package org.wso2.appserver.integration.tests.jaggery;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.appserver.integration.tests.jaggery.utils.JaggeryTestUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class sends requests to file.jag and validates the response
 */
public class FileHostObjectTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(FileHostObjectTestCase.class);
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
    }

    @Factory(dataProvider = "userModeProvider")
    public FileHostObjectTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @Test(groups = {"wso2.as"}, description = "Test for file host object")
    public void testFileExist() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(getWebAppURL(WebAppTypes.JAGGERY) + "/testapp/file.jag");
        URLConnection jaggeryServerConnection = JaggeryTestUtil.openConnection(jaggeryURL);
        assertNotNull(jaggeryServerConnection, "Connection establishment failure");

        BufferedReader in = JaggeryTestUtil.inputReader(jaggeryServerConnection);
        assertNotNull(in, "Input stream failure");

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertNotNull(response, "Result cannot be null");
        assertEquals(response, "Successfully read testfile.txt");
    }

    @Test(groups = {"wso2.as"}, description = "Test for file host object write",
            dependsOnMethods = "testFileExist")
    public void testFileWrite() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(webAppURL + "/testapp/file.jag?action=write");
        URLConnection jaggeryServerConnection = jaggeryURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                jaggeryServerConnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertEquals(response, "write was success");
    }

    @Test(groups = {"wso2.as"}, description = "Test for file host object operations",
            dependsOnMethods = "testFileWrite")
    public void testFileOperations() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(webAppURL + "/testapp/file.jag?action=test");
        URLConnection jaggeryServerConnection = jaggeryURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                jaggeryServerConnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertEquals(response, "length : 30, exists : true, stream : Successfully read testfile.txt");
    }

    @Test(groups = {"wso2.as"}, description = "Test for file host object read char",
            dependsOnMethods = "testFileOperations")
    public void testFileReadChars() throws Exception {

        String response = null;
        URL jaggeryURL = new URL(webAppURL + "/testapp/file.jag?action=read");
        URLConnection jaggeryServerConnection = jaggeryURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                jaggeryServerConnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response = inputLine;
        }

        in.close();
        log.info("Response :" + response);
        assertEquals(response, "Success");
    }
}
