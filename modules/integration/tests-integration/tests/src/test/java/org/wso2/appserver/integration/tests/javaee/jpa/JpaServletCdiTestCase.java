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
import java.net.MalformedURLException;
import java.net.URL;
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
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;


import static org.testng.Assert.assertTrue;

public class JpaServletCdiTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JpaServletCdiTestCase.class);
    private static final String webAppFileName = "jpa-order-processor.war";
    private static final String webAppName = "jpa-order-processor";
    private static final String webAppLocalURL = "/jpa-order-processor";
    private TestUserMode userMode;
    private String hostname;

    @Factory(dataProvider = "userModeProvider")
    public JpaServletCdiTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                //todo enable tenant mode after fixing sample issue with tenant
                //jira : https://wso2.org/jira/browse/WSAS-1998
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    public static void main(String[] args) {
        try {
            URL orderEp = new URL("http://localhost:9768/jpa-order-processor/order");
            HttpResponse response = HttpRequestUtil.doPost(orderEp, "item=Item0001&quantity=100&placeOrder=Place+Order");
            System.out.println(response.getData());
        } catch (AutomationFrameworkException | MalformedURLException e) {
            e.printStackTrace();
        }
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

    @Test(groups = "wso2.as", description = "test jpa and jax-ws")
    public void testJpaServletCdi() throws Exception {
        URL orderEp = new URL(webAppURL + "/order");
        HttpResponse response = HttpRequestUtil.doPost(orderEp, "item=Item0001&quantity=100&placeOrder=Place+Order");
        String result = response.getData();

        log.info("Response - " + result);

        assertTrue(result.contains(
                "        <tr>        <td>1        </td>        <td>Item0001        </td>        <td>100        </td>"),
                "Response doesn't contain expected data");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }
}
