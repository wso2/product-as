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
package org.wso2.appserver.integration.tests.javaee.cdi;

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
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;


import static org.testng.Assert.assertTrue;

public class CdiScopeTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(CdiScopeTestCase.class);
    private static final String webAppFileName = "cdi-scope.war";
    private static final String webAppName = "cdi-scope";
    private static final String webAppLocalURL = "/cdi-scope";
    String hostname;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public CdiScopeTestCase(TestUserMode userMode) {
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
                "AS" + File.separator + "javaee" + File.separator + "cdi" + File.separator + webAppFileName;
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "test cdi scopes, post construct & pre destroy with servlet")
    public void testCdiServlet() throws Exception {

        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURL, null);
        String result = response.getData();
        log.info("Response 1 : " + result);

        response = ASHttpRequestUtil.sendGetRequest(webAppURL, null);
        result = response.getData();
        log.info("Response 2 : " + result);

        LogViewerClient logViewerClient = new LogViewerClient(backendURL, sessionCookie);
        PaginatedLogEvent paginatedLogEvent = logViewerClient.
                getPaginatedApplicationLogEvents(0, "ALL", "", webAppName, "", "");

        //post Construct & Pre Destroy tests
        assertTrue("Post construct of LiftOperator".equals(paginatedLogEvent.getLogInfo()[8].getMessage()),
                "Log doesn't contain the expected post construct");
        assertTrue("Post construct of Receptionist".equals(paginatedLogEvent.getLogInfo()[7].getMessage()),
                "Log doesn't contain the expected post construct");

        assertTrue("Pre destroy of Receptionist".equals(paginatedLogEvent.getLogInfo()[4].getMessage()),
                "Log doesn't contain the expected pre destroy");
        assertTrue("Post construct of Receptionist".equals(paginatedLogEvent.getLogInfo()[3].getMessage()),
                "Log doesn't contain the expected post construct");

        assertTrue("Pre destroy of Receptionist".equals(paginatedLogEvent.getLogInfo()[0].getMessage()),
                "Log doesn't contain the expected pre destroy");


        //Scope Tests
        assertTrue("Receptionist: Hi, this is the first time I meet you".equals(paginatedLogEvent.getLogInfo()[6].getMessage()),
                "Log doesn't contain the expected message");
        assertTrue("Lift Operator: Hi, this is the first time I meet you".equals(paginatedLogEvent.getLogInfo()[5].getMessage()),
                "Log doesn't contain the expected message");

        assertTrue("Receptionist: Hi, this is the first time I meet you".equals(paginatedLogEvent.getLogInfo()[2].getMessage()),
                "Log doesn't contain the expected message");
        assertTrue("Lift Operator: Hi, I met you for 1 time(s)".equals(paginatedLogEvent.getLogInfo()[1].getMessage()),
                "Log doesn't contain the expected message");

    }

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }
}
