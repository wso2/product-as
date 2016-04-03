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
import java.util.List;

public class StatisticsPublisherTestCase extends TestBase {

    // timeout for reading the number of events from the thrift server.
    private static final int TIMEOUT = 15;

    private ThriftTestServer thriftTestServer;
    private int thriftPort = Constants.DEFAULT_THRIFT_PORT;

    @BeforeClass
    public static void init() {
        DataPublisherTestUtil.setKeyStoreParams();
    }

    @Test(description = "tests whether the thrift server is started.")
    public void testThriftServerStart() throws Exception {
        if (!TestUtils.isPortAvailable(thriftPort)) {
            List<Integer> availablePort = TestUtils.getAvailablePortsFromRange(
                    Constants.PORT_SCAN_MIN, Constants.PORT_SCAN_MAX, 1);
            if ((availablePort != null) && availablePort.size() > 0) {
                thriftPort = availablePort.get(0);
            }
        }
        thriftTestServer = new ThriftTestServer(thriftPort);
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
        while (((numberOfEventsReceived = thriftTestServer.getNumberOfEventsReceived()) < 1) || time < TIMEOUT) {
            Thread.sleep(1000);
            time++;
        }

        Assert.assertEquals(numberOfEventsReceived, 1, "Data is not published to the thrift server");
    }

    @AfterClass
    public void destroy() {
        thriftTestServer.stop();
    }

    private String convertJSONtoString() throws IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            Object obj = parser.parse(new FileReader(DataPublisherTestUtil.getStreamDefinitionPath()));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            throw new IOException("Failed");
        }
        return jsonObject.toJSONString();
    }
}
