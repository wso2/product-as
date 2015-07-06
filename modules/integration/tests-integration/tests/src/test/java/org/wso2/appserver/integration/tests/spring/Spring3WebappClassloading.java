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

package org.wso2.appserver.integration.tests.spring;

import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.SqlDataSourceUtil;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class Spring3WebappClassloading extends ASIntegrationTest{

    private static ServerConfigurationManager serverConfigurationManager;
    private TestUserMode userMode;
    private WebAppAdminClient webAppAdminClient;
    private SqlDataSourceUtil sqlDataSource;
    private String webAppName = "spring3-restful-webapp-classloading";
    private static File destRuntimeLibDir;
    private static int isRestarted = 0;

    @Factory(dataProvider = "userModeProvider")
    public Spring3WebappClassloading(TestUserMode userMode) {
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
        String dataSourceName = "spring3-restful-testdb";
        String webappClassloadingEnv = "webapp-classloading-environments.xml";

        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);

        //Restart the Server only once
        if (isRestarted == 0) {
            serverConfigurationManager =
                    new ServerConfigurationManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_USER));
            File sourceWebappClassloadingDir = new File(
                    FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "AS" +
                    File.separator +
                    "spring" + File.separator + "webapp" + File.separator + "classloading" + File.separator +
                    webappClassloadingEnv);
            File destWebappClassloadingDir = new File(
                    System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                    "conf" + File.separator + "tomcat" + File.separator + webappClassloadingEnv);

            File sourceRuntimeLibDir = new File(
                    System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                    File.separator + "artifacts" + File.separator + "AS" + File.separator + "spring" + File.separator +
                    "spring3" + File.separator + "runtime");
            destRuntimeLibDir = new File(
                    System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "lib" + File.separator +
                    "runtimes" +
                    File.separator + "spring");
            FileManipulator.copyDir(sourceRuntimeLibDir, destRuntimeLibDir);

            serverConfigurationManager.applyConfiguration(sourceWebappClassloadingDir, destWebappClassloadingDir, true,
                                                          true);
        }
        ++isRestarted;
        sessionCookie = loginLogoutClient.login();

        createTable();
        createDataSource(dataSourceName, sqlDataSource);
    }

    @Test(groups = "wso2.as", description = "Upload Spring 3 WAR and verify deployment")
    public void testSpringWARUpload() throws Exception {
        String springWarFilePath =
                System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                File.separator + "artifacts" + File.separator + "AS" + File.separator + "spring" + File.separator +
                "spring3" + File.separator + webAppName + ".war";
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(springWarFilePath);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName));
    }

    @Test(groups = "wso2.as", description = "Verify Get Operation", dependsOnMethods = "testSpringWARUpload")
    public void testGetOperation() throws Exception {
        String endpointURL = "/student";
        String endpoint = webAppURL + "/" + webAppName + endpointURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        try {
            JSONArray jsonArray = new JSONArray(response.getData());
            assertTrue(jsonArray.length() > 0);
        } catch (JSONException e) {
            assertTrue(false);
        }
    }

    @AfterClass(alwaysRun = true)
    public void restoreServer() throws Exception {
        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(webAppName + ".war", asServer.getInstance().getHosts().get("default"));

        //Revert and restart only once
        --isRestarted;
        if (isRestarted == 0) {
            FileManipulator.deleteDir(destRuntimeLibDir);
            serverConfigurationManager.restoreToLastConfiguration();
        }
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
