/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.integration.test.sampleservices.tomcatwebappsservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.dssutils.SqlDataSourceUtil;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;
import java.util.ArrayList;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to test TomcatWebApps sample.
 */
public class TomcatWebAppsTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(TomcatWebAppsTestCase.class);
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());
    }

    @AfterClass(alwaysRun = true)
    public void webAppDelete() throws Exception {   // delete web apps

        webAppAdminClient.deleteWebAppFile("GenericJavaBeanResource.war");
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                "GenericJavaBeanResource.war"), "Web App GenericJavaBeanResource unDeployment failed");

        webAppAdminClient.deleteWebAppFile("JDBCDataSource.war");
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                "JDBCDataSource.war"), "Web App JDBCDataSource unDeployment failed");
    }

    @Test(groups = "wso2.as", description = "Deploying web applications")
    public void webAppsDeploymentTest() throws Exception {

        // GenericJavaBeanResource.war
        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "GenericJavaBeanResource.war");

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                "GenericJavaBeanResource"), "GenericJavaBeanResource Web Application Deployment failed");
        log.info("GenericJavaBeanResource uploaded and deployed successfully");

        // JDBCDataSource.war
        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "JDBCDataSource.war");

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(),
                "JDBCDataSource"), "JDBCDataSource Web Application Deployment failed");
        log.info("JDBCDataSource uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "JNDI resources lookup Example for Generic Java Beans" +
            " - Get values for MyBean", dependsOnMethods = "webAppsDeploymentTest")
    public void testJNDIResourcesLookup() throws Exception {

        String webAppURL = asServer.getWebAppURL() + "/GenericJavaBeanResource/bean";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        log.info("Response " + response);
        Assert.assertEquals(response.getData(), "foo = 68, bar = 23");
    }

    @Test(groups = "wso2.as", description = "JDBC Data Source Service invocation, Executing queries " +
            "and validation of results", dependsOnMethods = "testJNDIResourcesLookup", enabled = false)
    public void testJDBCDataSourceServiceInvoke() throws Exception {

        SqlDataSourceUtil sqlDataSourceUtil = new SqlDataSourceUtil(asServer.getSessionCookie(),
                asServer.getBackEndUrl(),
                FrameworkFactory.getFrameworkProperties(ProductConstant.APP_SERVER_NAME),
                Integer.parseInt(userInfo.getUserId()));

        //creating a data source and execute queries
        ArrayList<File> sqlFileList = new ArrayList<File>();

        File file = new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + "artifacts" + File.separator + "AS" + File.separator
                + "sql" + File.separator + "TomcatWebAppDBScript.sql");
        sqlFileList.add(file);

        sqlDataSourceUtil.createNonRandomDataSource(sqlFileList);  // executing sql queries

        String webAppURL = asServer.getWebAppURL() + "/JDBCDataSource/jdbc/jdbcdatasource?getValues=true";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        log.info("Response " + response);
        Assert.assertEquals(response.getData(), "1. Employee Name = WSO2 Client   Age = 25");
    }
}
