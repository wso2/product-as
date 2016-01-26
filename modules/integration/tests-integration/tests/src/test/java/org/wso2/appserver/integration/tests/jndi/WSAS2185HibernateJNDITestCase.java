package org.wso2.appserver.integration.tests.jndi;
/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This test class is use to test the JNDI Hibernate Integration
 * If the app can't be deploy successfully and throws a NamingException it implies that the integration fails
 */
public class WSAS2185HibernateJNDITestCase extends ASIntegrationTest {
    private static final String WEBAPP_FILENAME = "hibernate-jndi-sample.war";
    private static final java.lang.String APP_NAME = "hibernate-jndi-sample";
    private TestUserMode userMode;
    private WebAppAdminClient webAppAdminClient;
    private HttpClient httpClient = new HttpClient();

    @Factory(dataProvider = "userModeDataProvider")
    public WSAS2185HibernateJNDITestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeDataProvider() {
        return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_USER } };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        String path = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "war" + File.separator + WEBAPP_FILENAME;
        webAppAdminClient.uploadWarFile(path);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, APP_NAME),
                   "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "Set tenant ID without resolving domain and check if domain is null")
    public void testAddingEmployeeWithJNDI() throws Exception {
        String url = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + APP_NAME + "/EmployeeManager?empName=WSO2";

        GetMethod getMethod = new GetMethod(url);
        assertEquals(httpClient.executeMethod(getMethod), HttpStatus.SC_OK,
                     "Method failed: " + getMethod.getStatusLine());
        assertEquals(getMethod.getResponseBodyAsString(), "Successfully persist the Employee",
                     "Received message is differ than the expected");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(WEBAPP_FILENAME, asServer.getDefaultInstance().getHosts().get("default"));
    }
}
