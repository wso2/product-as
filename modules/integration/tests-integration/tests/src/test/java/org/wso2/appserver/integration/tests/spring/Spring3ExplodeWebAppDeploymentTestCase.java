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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.SqlDataSourceUtil;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class Spring3ExplodeWebAppDeploymentTestCase extends ASIntegrationTest {

    private final String webAppName = "spring3-restful-jndi-service";
    private final String endpointURL = "/student";
    private SqlDataSourceUtil sqlDataSource;
    private String webAppDeploymentDir;
    private WebAppAdminClient webAppAdminClient;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE })
    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        String dataSourceName = "spring3-restful-testdb";
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS);
        createTable();
        createDataSource(dataSourceName, sqlDataSource);
        webAppDeploymentDir =
                System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                "deployment" + File.separator + "server" + File.separator + "webapps" + File.separator;
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Deploying exploded web application file to deployment directory", enabled = false)
    public void testWebApplicationExplodedDeployment() throws Exception {
        String source = System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                        File.separator + "artifacts" + File.separator + "AS" + File.separator + "spring" +
                        File.separator + "spring3" + File.separator + webAppName + ".war";

        ArchiveExtractor archiveExtractor = new ArchiveExtractor();
        archiveExtractor.extractFile(source, webAppDeploymentDir + File.separator + webAppName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                   "Web Application Deployment failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "Verify Get Operation",
            dependsOnMethods = "testWebApplicationExplodedDeployment", enabled = false)
    public void testGetOperation() throws Exception {
        String endpoint = webAppURL + "/" + webAppName + endpointURL;
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
            dependsOnMethods = "testGetOperation", enabled = false)
    public void testDeployModifiedWAROverExplodedWebApp() throws Exception {
        String newWebAppName = "spring3-restful-simple-service";
        File sourceFile = new File(
                System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                File.separator + "artifacts" + File.separator + "AS" + File.separator + "spring" + File.separator +
                "spring3" + File.separator + newWebAppName + ".war");
        File changeWarFilename = new File(
                System.getProperty("basedir", ".") + File.separator + "target" + File.separator + "resources" +
                File.separator + "artifacts" + File.separator + "AS" + File.separator + "spring" + File.separator +
                "spring3" + File.separator + "tmp" + File.separator + webAppName + ".war");
        FileManipulator.copyFile(sourceFile, changeWarFilename);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(changeWarFilename.getAbsolutePath());
        String endpoint = webAppURL + "/" + webAppName + endpointURL;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        String expectedMsg = "{\"status\":\"success\"}";
        assertTrue(expectedMsg.equalsIgnoreCase(response.getData()));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppName + ".war", asServer.getInstance().getHosts().get("default"));
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
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
