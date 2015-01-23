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
package org.wso2.appserver.integration.tests.javaee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class CdiServletTestCase extends WebappDeploymentTestCase {

    private static final Log log = LogFactory.getLog(CdiServletTestCase.class);
    private static final String webAppFileName = "cdi-produces-1.0.war";
    private static final String webAppName = "cdi-produces-1.0";
    private static final String webAppLocalURL ="/cdi-produces-1.0";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        setWebAppFileName(webAppFileName);
        setWebAppName(webAppName);
        setWebAppURL(getWebAppURL() + webAppLocalURL);
    }

    @Test(groups = "wso2.as", description = "test cdi with servlet", dependsOnMethods = "webApplicationDeploymentTest")
    public void testCdiServlet() throws Exception {

        String servletUrl = getWebAppURL();
        String result = runAndGetResultAsString(servletUrl);

        assertTrue(result.startsWith("Hi, greetings from implementation one"),
                   "Response doesn't contain the greeting, hi, of the url " + servletUrl);
        assertTrue(result.contains("Bye !"),
                   "Response doesn't contain the greeting, bye, of the url " + servletUrl);
    }




}
