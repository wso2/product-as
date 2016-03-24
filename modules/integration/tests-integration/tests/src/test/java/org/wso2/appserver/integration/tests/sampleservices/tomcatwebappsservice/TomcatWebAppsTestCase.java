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
package org.wso2.appserver.integration.tests.sampleservices.tomcatwebappsservice;

import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.appserver.integration.common.utils.ASHttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.util.ArrayList;

import static org.testng.Assert.assertTrue;

/**
 * This class can be used to test TomcatWebApps sample.
 */
public class TomcatWebAppsTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(TomcatWebAppsTestCase.class);
    private WebAppAdminClient webAppAdminClient;
    private final String hostName = "localhost";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,
                sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void webAppDelete() throws Exception {   // delete web apps

        webAppAdminClient.deleteWebAppFile("GenericJavaBeanResource.war", hostName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie,
                "GenericJavaBeanResource.war"), "Web App GenericJavaBeanResource unDeployment failed");

        webAppAdminClient.deleteWebAppFile("JDBCDataSource.war", hostName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie,
                "JDBCDataSource.war"), "Web App JDBCDataSource unDeployment failed");
    }

    @Test(groups = "wso2.as", description = "Deploying web applications")
    public void webAppsDeploymentTest() throws Exception {

        // GenericJavaBeanResource.war
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation()
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "GenericJavaBeanResource.war");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie,
                "GenericJavaBeanResource"), "GenericJavaBeanResource Web Application Deployment failed");
        log.info("GenericJavaBeanResource uploaded and deployed successfully");

        // JDBCDataSource.war
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation()
                + "artifacts" + File.separator + "AS" + File.separator
                + "war" + File.separator + "JDBCDataSource.war");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie,
                "JDBCDataSource"), "JDBCDataSource Web Application Deployment failed");
        log.info("JDBCDataSource uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "JNDI resources lookup Example for Generic Java Beans" +
            " - Get values for MyBean", dependsOnMethods = "webAppsDeploymentTest")
    public void testJNDIResourcesLookup() throws Exception {

        String webAppURL1 = webAppURL + "/GenericJavaBeanResource/bean";
        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURL1, null);
        log.info("Response " + response);
        Assert.assertEquals(response.getData(), "foo = 68, bar = 23");
    }

    @Test(groups = "wso2.as", description = "JDBC Data Source Service invocation, Executing queries " +
            "and validation of results", dependsOnMethods = "testJNDIResourcesLookup", enabled = false)
    public void testJDBCDataSourceServiceInvoke() throws Exception {

//        SqlDataSourceUtil sqlDataSourceUtil = new SqlDataSourceUtil(sessionCookie,
//                backendURL),
//                FrameworkFactory.getFrameworkProperties(ProductConstant.APP_SERVER_NAME),
//                Integer.parseInt(userInfo.getUserId()));
//
//        //creating a data source and execute queries
//        ArrayList<File> sqlFileList = new ArrayList<File>();
//
//        File file = new File(FrameworkPathUtil.getSystemResourceLocation()
//                + "artifacts" + File.separator + "AS" + File.separator
//                + "sql" + File.separator + "TomcatWebAppDBScript.sql");
//        sqlFileList.add(file);
//
//        sqlDataSourceUtil.createNonRandomDataSource(sqlFileList);  // executing sql queries
//
//        String webAppURL1 = webAppURL + "/JDBCDataSource/jdbc/jdbcdatasource?getValues=true";
//        HttpResponse response = ASHttpRequestUtil.sendGetRequest(webAppURL1, null);
//        log.info("Response " + response);
//        Assert.assertEquals(response.getData(), "1. Employee Name = WSO2 Client   Age = 25");
    }
}
