/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.appserver.integration.test.server.security.manager;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * Test class to verify the security restrictions when java security manager is enabled
 */
public class JavaSecurityManagerTestCase extends ASIntegrationTest {

    private final String webAppFileName = "security-check.war";
    private final String webAppName = "security-check";
    private final String hostName = "localhost";
    private WebAppAdminClient webAppAdminClient;
    private String webAppUrl;
    private TestUserMode userMode;

    @Factory(dataProvider = "userModeDataProvider")
    public JavaSecurityManagerTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.warFileUplaoder(TestConfigurationProvider.getResourceLocation("AS")
                                          + File.separator + "security"
                                          + File.separator + "manager" + File.separator + "webapp"
                                          + File.separator + webAppFileName);
        //let webapp to deploy
        Thread.sleep(2000);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName)
                , webAppName + " Web Application Deployment failed");
        webAppUrl = getWebAppURL(WebAppTypes.WEBAPPS) + "/" + webAppName;

    }

    @Test(groups = {"wso2.as"}, description = "Accessing user-mgt.xml test")
    public void testAccessingFileUnderConfDirSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppUrl + "/directFile"
                , "fileName=repository/conf/user-mgt.xml");
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred while reading file. Reason:" +
                                                      " access denied (\\\"java.io.FilePermission")
                , "Error Message mismatched. File can be accessed > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "Accessing registry database configurations test")
    public void testGetRegistryDBConfigSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppUrl + "/registryDBConfig", null);
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred when reading registry DB config. " +
                                                      "Reason: access denied (\\\"java.io.FilePermission\\")
                , "Error Message mismatched. Registry Database config be accessed > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "Accessing user management database configurations")
    public void testGetUserManagerDBConfigSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppUrl + "/userManagerDBConfig", null);
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred when reading user manager DB config. " +
                                                      "Reason: access denied (\\\"java.io.FilePermission\\")
                , "Error Message mismatched. User Management database config can be accessed > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "calling ServerConfiguration.getInstance()")
    public void getServerConfigurationSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppUrl + "/serverConfiguration"
                , null);
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred while calling ServerConfiguration.getInstance()." +
                                                      " Reason: java.security.AccessControlException:" +
                                                      " access denied (\\\"java.lang.management.ManagementPermission\\\"" +
                                                      " \\\"control\\\")")
                , "Error Message mismatched. ServerConfiguration.getInstance() can be called > "
                  + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "reading axis2 file path from carbon using CarbonUtils")
    public void testAccessingFilePathFromCarbonUtilsSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppUrl + "/axis2FilePath", null);
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred while reading axis2 file path." +
                                                      " Reason: access denied " +
                                                      "(\\\"java.lang.management.ManagementPermission\\\"" +
                                                      " \\\"control\\\")")
                , "Error Message mismatched. File path can be retrieved > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "Copping file to carbon home")
    public void testCopyFileToCarbonHomeSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(webAppUrl + "/fileCopy" +
                                                               "?source=repository/conf/axis2/axis2.xml" +
                                                               "&destination=repository/conf" +
                                                               "/axis2/axis2.xml-dummy"),
                                                       "");
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred while copying file. Reason: " +
                                                      "access denied (\\\"java.io.FilePermission")
                , "Error Message mismatched. File copied successfully > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "Creating a file on the server")
    public void testWritingFileUnderConfDirSecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(webAppUrl + "/directFile?fileName=repository/" +
                                                               "conf/user-mgt-dummy.xml"), "");
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred while creating file. Reason: " +
                                                      "access denied (\\\"java.io.FilePermission")
                , "Error Message mismatched. File can be accessed > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "Accessing System Properties")
    public void testGettingSystemPropertySecurity() throws Exception {
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppUrl + "/systemProperty/carbon.home"
                , null);
        //verifying the error message
        Assert.assertTrue(response.getData().contains("SYSTEM PROPERTY >")
                , "SYSTEM PROPERTY can not be accessed > " + response.getData());
    }

    @Test(groups = {"wso2.as"}, description = "Deleting File From Server")
    public void testDeleteFileFromServerSecurity() throws Exception {
        HttpResponse response = HttpURLConnectionClient.sendDeleteRequest(new URL(webAppUrl
                               + "/directFile?fileName=repository/conf/user-mgt-dummy.xml"), null);
        //verifying the error message
        Assert.assertTrue(response.getData().contains("Error occurred while deleting file" +
                                                      ". Reason: access denied (\\\"java.io.FilePermission")
                , "Error Message mismatched. File can be deleted > " + response.getData());
    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, hostName);
        //let webapp to undeploy
        Thread.sleep(2000);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                   webAppName + " Web Application unDeployment failed");
    }

    @DataProvider
    public static Object[][] userModeDataProvider() {
        return new Object[][]{
                new Object[]{TestUserMode.SUPER_TENANT_ADMIN},
                new Object[]{TestUserMode.TENANT_ADMIN},
        };
    }
}
