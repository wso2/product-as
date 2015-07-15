/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.appserver.integration.tests.webapp.spring;

import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.*;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SpringExplodeWebAppDeploymentTestCase extends ASIntegrationTest {

    private WebAppMode webAppMode;
    private final String endpointURL = "/student";
    private static final String SPRING3JNDIAPP = "spring3-restful-jndi-service";
    private static final String SPRING4JNDIAPP = "spring4-restful-jndi-service";
    private SqlDataSourceUtil sqlDataSource;
    private String webAppDeploymentDir;
    private WebAppAdminClient webAppAdminClient;

    @Factory(dataProvider = "webAppModeProvider")
    public SpringExplodeWebAppDeploymentTestCase(WebAppMode webAppMode) {
        this.webAppMode = webAppMode;
    }

    @DataProvider
    private static WebAppMode[][] webAppModeProvider() {
        return new WebAppMode[][] {
                new WebAppMode[] {new WebAppMode(SPRING3JNDIAPP, TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode(SPRING3JNDIAPP, TestUserMode.TENANT_USER)},
                new WebAppMode[] {new WebAppMode(SPRING4JNDIAPP, TestUserMode.SUPER_TENANT_ADMIN)},
                new WebAppMode[] {new WebAppMode(SPRING4JNDIAPP, TestUserMode.TENANT_USER)},
        };
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE })
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        createTable();
        createDataSource(webAppMode.getWebAppName(), sqlDataSource);
        webAppDeploymentDir =
                System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Deploying exploded web application file to deployment directory")
    public void testWebApplicationExplodedDeployment() throws Exception {
        String source = ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator +
                        webAppMode.getWebAppName() + ".war";

        ArchiveExtractor archiveExtractor = new ArchiveExtractor();
        archiveExtractor.extractFile(source, webAppDeploymentDir + File.separator + webAppMode.getWebAppName());
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()),
                   "Web Application Deployment failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Verify Get Operation",
            dependsOnMethods = "testWebApplicationExplodedDeployment")
    public void testGetOperation() throws Exception {
        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        try {
            JSONArray jsonArray = new JSONArray(response.getData());
            assertTrue(jsonArray.length() > 0);
        } catch (JSONException e) {
            assertTrue(false);
        }
    }

    @SetEnvironment(executionEnvironments =  {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Deploy a WAR file which has the same name as the exploded Web Application, but with changes in it",
            dependsOnMethods = "testGetOperation")
    public void testDeployModifiedWAROverExplodedWebApp() throws Exception {
        String endpoint = webAppURL + "/" + webAppMode.getWebAppName() + endpointURL + "/deployedtime";
        String newWebAppName = "";
        if (webAppMode.getWebAppName().equalsIgnoreCase(SPRING3JNDIAPP)) {
            newWebAppName = "spring3-restful-simple-service";
        } else if (webAppMode.getWebAppName().equalsIgnoreCase(SPRING4JNDIAPP)) {
            newWebAppName = "spring4-restful-simple-service";
        } else {
            assertTrue(false);
        }
        File sourceFile = new File(
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + newWebAppName + ".war");
        File changeWarFilename = new File(
                ASIntegrationConstants.TARGET_RESOURCE_LOCATION + "spring" + File.separator + "tmp" + File.separator +
                webAppMode.getWebAppName() + ".war");
        FileManipulator.copyFile(sourceFile, changeWarFilename);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);

        webAppAdminClient.uploadWarFile(changeWarFilename.getAbsolutePath());
        assertTrue(WebAppDeploymentUtil.isWebAppRedeployed(webAppMode.getWebAppName(), response.getData(), endpoint),
                   "Web app redeployment failed: " + webAppMode.getWebAppName());

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE })
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppMode.getWebAppName() + ".war",
                                           asServer.getInstance().getHosts().get("default"));
        assertTrue(
                WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppMode.getWebAppName()),
                   "Web Application Deployment failed");
    }

    private void createTable() throws Exception {
        sqlDataSource = new SqlDataSourceUtil(sessionCookie,asServer.getContextUrls().getBackEndUrl());
        File sqlFile = new File(TestConfigurationProvider.getResourceLocation() + "artifacts" + File.separator + "AS" +
                                File.separator + "spring" + File.separator + "studentDb.sql");
        List<File> sqlFileList = new ArrayList<>();
        sqlFileList.add(sqlFile);
        sqlDataSource.createDataSource(sqlFileList, "dataService");
    }
}
