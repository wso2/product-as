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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.tests.javaee.WebappDeploymentTestCase;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;

import static org.testng.Assert.assertTrue;

public class CdiScopeTestCase extends WebappDeploymentTestCase {

    private static final Log log = LogFactory.getLog(CdiScopeTestCase.class);
    private static final String webAppFileName = "cdi-scope.war";
    private static final String webAppFilePath = "cdi";
    private static final String webAppName = "cdi-scope";
    private static final String webAppLocalURL = "/cdi-scope";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        setWebAppFileName(webAppFileName);
        setWebAppFilePath(webAppFilePath);
        setWebAppName(webAppName);
        setWebAppURL(getWebAppURL() + webAppLocalURL);
    }

    @Test(groups = "wso2.as", description = "test cdi scopes, post construct & pre destroy with servlet",
            dependsOnMethods = "webApplicationDeploymentTest")
    public void testCdiServlet() throws Exception {

        String servletUrl = getWebAppURL();
        HttpResponse response = HttpRequestUtil.sendGetRequest(servletUrl, null);
        String result = response.getData();
        log.info("Response 1 : " + result);

        response = HttpRequestUtil.sendGetRequest(servletUrl, null);
        result = response.getData();
        log.info("Response 2 : " + result);

        org.wso2.appserver.integration.common.clients.LogViewerClient logViewerClient = new org.wso2.appserver.integration.common.clients.LogViewerClient(backendURL, sessionCookie);
        PaginatedLogEvent paginatedLogEvent = logViewerClient.getPaginatedApplicationLogEvents(0, "ALL", "", webAppName, "", "");

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
}
