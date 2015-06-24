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

import static org.testng.Assert.assertTrue;

public class CdiInjectTestCase extends WebappDeploymentTestCase {

    private static final Log log = LogFactory.getLog(CdiInjectTestCase.class);
    private static final String webAppFileName = "cdi-inject.war";
    private static final String webAppFilePath = "cdi";
    private static final String webAppName = "cdi-inject";
    private static final String webAppLocalURL = "/cdi-inject";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        setWebAppFileName(webAppFileName);
        setWebAppFilePath(webAppFilePath);
        setWebAppName(webAppName);
        setWebAppURL(getWebAppURL() + webAppLocalURL);
    }

    @Test(groups = "wso2.as", description = "test cdi inject with servlet", dependsOnMethods = "webApplicationDeploymentTest")
    public void testCdiServlet() throws Exception {

        String servletUrl = getWebAppURL();
        HttpResponse response = HttpRequestUtil.sendGetRequest(servletUrl, null);
        String result = response.getData();

        log.info("Response - " + result);

        assertTrue(result.startsWith("Hi, this is CDI-Servlet sample in WSO2 Application Server"),
                "Response doesn't contain the greeting " + servletUrl);
    }
}
