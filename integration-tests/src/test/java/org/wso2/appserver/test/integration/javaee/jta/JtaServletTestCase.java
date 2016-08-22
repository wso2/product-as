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
package org.wso2.appserver.test.integration.javaee.jta;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JtaServletTestCase extends TestBase {

    private static final Log log = LogFactory.getLog(JtaServletTestCase.class);
    private static final String webAppLocalURL = "/jta-money-transfer";

    @Test(description = "test cdi scopes, post construct & pre destroy with servlet")
    public void testJtaServlet() throws Exception {

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
            log.info(sb.toString());
            Assert.assertTrue(sb.toString().contains("Please have a look at the terminal to see the output"),
                    "Response doesn't contain expected data");

            Path logPath = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "logs");
            Optional<Path> pathOptional = Files.walk(logPath).filter(path -> path.toString().contains("catalina"))
                    .findFirst();

            Path catalinaLog = null;
            if (pathOptional.isPresent()) {
                catalinaLog = pathOptional.get();
            }
            if (catalinaLog == null) {
                Assert.fail("Catalina log file not found.");
            }
            List<String> logContent = Files.lines(catalinaLog).collect(Collectors.toList());
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Sample transaction with commit")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream()
                            .anyMatch(str -> str.contains("Operation: Transferring 100.0 from Account2 to Account 1")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Account 1 entry successful")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Account 2 entry successful")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Log entry successful")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Account 1:")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream()
                            .anyMatch(str -> str.contains("Transaction ID | Amount | Transaction Type | Timestamp")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("1 | 100.0 | CREDIT")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Account 2:")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("51 | 100.0 | DEBIT")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream()
                            .anyMatch(str -> str.contains("Log ID | Credit acc | Debit acc | Amount | Timestamp")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("101 | acc1 | acc2 | 100.0")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("Sample transaction with rollback")),
                    "Log doesn't contain Sample transaction with contain the expected output");
            Assert.assertTrue(logContent.stream().anyMatch(str -> str.contains("rollback method was called")),
                    "Log doesn't contain Sample transaction with contain the expected output");
        }
    }

}
