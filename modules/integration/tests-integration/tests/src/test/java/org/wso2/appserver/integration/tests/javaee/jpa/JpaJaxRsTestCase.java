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
package org.wso2.appserver.integration.tests.javaee.jpa;

import java.io.File;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JpaJaxRsTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JpaJaxRsTestCase.class);
    private static final String webAppFileName = "jpa-student-register-1.0.war";
    private static final String webAppName = "jpa-student-register-1.0";
    private static final String webAppLocalURL = "/jpa-student-register-1.0";
    String hostname;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeProvider")
    public JpaJaxRsTestCase(TestUserMode userMode) {
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
                "AS" + File.separator + "javaee" + File.separator + "jpa" + File.separator + webAppFileName;
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");
    }

    /**
     * ex. getall response in xml
     * <p/>
     * <Students><students><index>100</index><name>John</name></students></Students>
     */
    //todo enable this test method after the sample is fixed
    // todo jira : https://wso2.org/jira/browse/WSAS-1996
    @Test(groups = "wso2.as", description = "test jpa and jax-rs", enabled = false)
    public void testJpaRsGet() throws Exception {

        String getAll = "/student/getall";
        String jndiUrl = webAppURL + getAll;

        HttpClientUtil client = new HttpClientUtil();

        //todo client.get is not working properly
        OMElement result = client.get(jndiUrl);

        log.info("Response - " + result.toString());
        OMElement students = result.getFirstElement();
        assertEquals(students.getLocalName(), "students", "response is invalid for " + jndiUrl);
        assertEquals(students.getFirstChildWithName(new QName("index")).toString(), "<index>100</index>",
                "response is invalid for " + jndiUrl);
        assertEquals(students.getFirstChildWithName(new QName("name")).toString(), "<name>John</name>",
                "response is invalid for " + jndiUrl);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }

}
