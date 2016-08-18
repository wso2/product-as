/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appserver.test.integration.javaee.cdi;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CdiInteceptortTestCase extends TestBase {
    private static final String webAppLocalURL = "/cdi-inteceptor-" + System.getProperty("appserver.version");

    @Test(description = "test cdi interceptor with servlet")
    public void testCdiServlet() throws IOException {

        URL requestUrlGet = new URL(getBaseUrl() + webAppLocalURL);
        HttpURLConnection connectionGet = (HttpURLConnection) requestUrlGet.openConnection();
        connectionGet.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCodeGet = connectionGet.getResponseCode();
        Assert.assertEquals(responseCodeGet, 200, "Server Response Code");
        if (responseCodeGet == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connectionGet.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            Assert.assertTrue(sb.toString().contains("Hi, please check the console for interceptor messages"),
                    "Response doesn't contain the greeting");

            Path logPath = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "logs");
            Optional<Path> pathOptional = Files.walk(logPath).filter(path -> path.toString().contains("catalina"))
                    .findFirst();

            Path catalinaLog = null;
            if (pathOptional.isPresent()) {
                catalinaLog = pathOptional.get();
            }
            if (catalinaLog == null) {
                Assert.fail("Cataline log file not found.");
            }
            List<String> logContent = Files.lines(catalinaLog).collect(Collectors.toList());
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Before greeting")));
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Inside greet method")));
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("After greeting")));
        }
    }
}
