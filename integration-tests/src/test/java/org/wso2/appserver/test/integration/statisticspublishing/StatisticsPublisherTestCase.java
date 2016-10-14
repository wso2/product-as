/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.appserver.test.integration.statisticspublishing;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestUtils;

import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class defines integration tests for HTTP monitoring stats publisher.
 *
 * @since 6.0.0
 */
public class StatisticsPublisherTestCase extends TestBase {

    // timeout for reading the number of events from the thrift server.
    private static final int TIMEOUT = 15;

    private ThriftTestServer thriftTestServer;
    private int thriftPort;
    private int thriftSSLPort;

    @BeforeClass
    public static void init() {
        DataPublisherTestUtil.setKeyStoreParams();
    }

    @Test(description = "tests whether the thrift server is started.")
    public void testThriftServerStart() throws Exception {
        thriftPort = Integer.parseInt(System.getProperty(Constants.THRIFT_PORT));
        thriftSSLPort = Integer.parseInt(System.getProperty(Constants.THRIFT_SSL_PORT));
        thriftTestServer = new ThriftTestServer(thriftSSLPort, thriftPort);
        thriftTestServer.start();
        thriftTestServer.addStreamDefinition(convertJSONtoString());
        Assert.assertTrue(TestUtils.isServerListening(Constants.HOST, thriftPort), "Thrift server is not started.");
    }

    @Test(description = "tests whether the data published are received by the server.",
            dependsOnMethods = {"testThriftServerStart"})
    public void testDataPublishing() throws Exception {
        String endpointURL = getBaseUrl() + "/examples/servlets/servlet/HelloWorldExample";
        URL requestUrl = new URL(endpointURL);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        Assert.assertEquals(responseCode, 200, "Error in accessing the HelloWorldExample application");

        int time = 0;
        int numberOfEventsReceived;
        while (((numberOfEventsReceived = thriftTestServer.getNumberOfEventsReceived()) == 0) || time < TIMEOUT) {
            Thread.sleep(1000);
            time++;
        }

        Assert.assertTrue(numberOfEventsReceived > 0, "Data is not published to the thrift server");
    }

    @AfterClass
    public void destroy() {
        thriftTestServer.stop();
    }

    private String convertJSONtoString() throws IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            Object obj = parser.parse(new FileReader(DataPublisherTestUtil.getStreamDefinitionPath()));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            throw new IOException("Failed");
        }
        return jsonObject.toJSONString();
    }
}
