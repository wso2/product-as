/*
* Copyright 2004,2013 The Apache Software Foundation.
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
package org.wso2.appserver.integration.tests.javaee.jta;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.LogViewerClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JtaServletTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JtaServletTestCase.class);
    private static final String webAppFileName = "jta-money-transfer.war";
    private static final String webAppName = "jta-money-transfer";
    private static final String webAppLocalURL = "/jta-money-transfer";
    String hostname;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public JtaServletTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);

        hostname = asServer.getInstance().getHosts().get("default");
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS) + webAppLocalURL;

        String webAppFilePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "AS" + File.separator + "javaee" + File.separator + "jta" + File.separator + webAppFileName;
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "test cdi scopes, post construct & pre destroy with servlet")
    public void testJtaServlet() throws Exception {

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        String result = response.getData();
        log.info("Response : " + result);

        assertEquals(result, "Please have a look at the terminal to see the output",
                "Response doesn't contain the expected data.");

        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        PaginatedLogEvent[] paginatedLogEvents = new PaginatedLogEvent[3];

        paginatedLogEvents[0] = logViewerClient.getPaginatedApplicationLogEvents(0, "ALL", "", webAppName, "", "");
        paginatedLogEvents[1] = logViewerClient.getPaginatedApplicationLogEvents(1, "ALL", "", webAppName, "", "");
        paginatedLogEvents[2] = logViewerClient.getPaginatedApplicationLogEvents(2, "ALL", "", webAppName, "", "");

        assertTrue(paginatedLogEvents[2] != null && paginatedLogEvents[2].getLogInfo().length == 10,
                "Paginated log page 3 is empty");
        assertTrue(paginatedLogEvents[1] != null && paginatedLogEvents[1].getLogInfo().length == 15,
                "Paginated log page 2 is empty");
        assertTrue(paginatedLogEvents[0] != null && paginatedLogEvents[0].getLogInfo().length == 15,
                "Paginated log page 1 is empty");
        //page 3
        assertTrue("Sample transaction with commit".equals(paginatedLogEvents[2].getLogInfo()[7].getMessage()),
                "Log doesn't contain the expected output");
        assertTrue("Operation: Transferring 100.0 from Account2 to Account 1".equals(
                        paginatedLogEvents[2].getLogInfo()[5].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 1 entry successful".equals(paginatedLogEvents[2].getLogInfo()[4].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 2 entry successful".equals(paginatedLogEvents[2].getLogInfo()[3].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Log entry successful".equals(paginatedLogEvents[2].getLogInfo()[2].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 1:".equals(paginatedLogEvents[2].getLogInfo()[0].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");

        //page 2
        assertTrue("Transaction ID | Amount | Transaction Type | Timestamp".equals(
                        paginatedLogEvents[1].getLogInfo()[14].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue(paginatedLogEvents[1].getLogInfo()[13].getMessage().contains("1 | 100.0 | CREDIT"),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 2:".equals(paginatedLogEvents[1].getLogInfo()[11].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Transaction ID | Amount | Transaction Type | Timestamp".equals(
                        paginatedLogEvents[1].getLogInfo()[10].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue(paginatedLogEvents[1].getLogInfo()[9].getMessage().contains("51 | 100.0 | DEBIT"),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Log ID | Credit acc | Debit acc | Amount | Timestamp".equals(
                        paginatedLogEvents[1].getLogInfo()[6].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue(paginatedLogEvents[1].getLogInfo()[5].getMessage().contains("101 | acc1 | acc2 | 100.0"),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Sample transaction with rollback".equals(paginatedLogEvents[1].getLogInfo()[3].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Operation: Transferring 100.0 from Account2 to Account 1".equals(
                        paginatedLogEvents[1].getLogInfo()[1].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 1 entry successful".equals(paginatedLogEvents[1].getLogInfo()[0].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");

        //page 1
        assertTrue("Account 2 entry successful".equals(paginatedLogEvents[0].getLogInfo()[14].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("rollback method was called".equals(paginatedLogEvents[0].getLogInfo()[13].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 1:".equals(paginatedLogEvents[0].getLogInfo()[11].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Transaction ID | Amount | Transaction Type | Timestamp".equals(
                        paginatedLogEvents[0].getLogInfo()[10].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue(paginatedLogEvents[0].getLogInfo()[9].getMessage().contains("1 | 100.0 | CREDIT"),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Account 2:".equals(paginatedLogEvents[0].getLogInfo()[7].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Transaction ID | Amount | Transaction Type | Timestamp".equals(
                        paginatedLogEvents[0].getLogInfo()[6].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue(paginatedLogEvents[0].getLogInfo()[5].getMessage().contains("51 | 100.0 | DEBIT"),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue("Log ID | Credit acc | Debit acc | Amount | Timestamp".equals(
                        paginatedLogEvents[0].getLogInfo()[2].getMessage()),
                "Log doesn't contSample transaction with contain the expected output");
        assertTrue(paginatedLogEvents[0].getLogInfo()[1].getMessage().contains("101 | acc1 | acc2 | 100.0"),
                "Log doesn't contSample transaction with contain the expected output");

    }

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }

}
