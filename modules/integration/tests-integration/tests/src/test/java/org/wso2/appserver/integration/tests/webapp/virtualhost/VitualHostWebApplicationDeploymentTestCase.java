/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appserver.integration.tests.webapp.virtualhost;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

/*
*  catalina-server.xml should have a virtual host entry as follows to pass this test case
*   <Host name="www.vhost.com" unpackWARs="true" deployOnStartup="false" autoDeploy="false" appBase="${carbon.home}/repository/deployment/server/dir/"/>
*/

public class VitualHostWebApplicationDeploymentTestCase extends ASIntegrationTest {
    private static int GET_RESPONSE_DELAY = 30 * 1000;
    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppFileName1 = "HelloWorldWebapp.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";
    private final String webAppName1 = "HelloWorldWebapp";
    private final String appBaseDir = "dir";
    private final String appBaseDir1 = "dir1";
    private final String vhostName = "www.vhost.com";
    private final String vhostName1 = "www.vhost1.com";
    private ServerConfigurationManager serverManager;
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(asServer);

        //restart server with virtual hosts
        File sourceFile = new File(TestConfigurationProvider.getResourceLocation() + File.separator +
                "artifacts" + File.separator + "AS" + File.separator + "tomcat" + File.separator + "catalina-server.xml");
        File targetFile = new File(System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" + File.separator + "conf" +
                File.separator + "tomcat" + File.separator + "catalina-server.xml");
        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        serverManager.restartForcefully();

        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void revertVirtualhostConfiguration() throws Exception {
        //reverting the changes done to appsever
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }

    }

    @Test
    public void testWebApplicationDeployment() throws Exception {
        uploadWarFile(appBaseDir, webAppFileName);
        uploadWarFile(appBaseDir1, webAppFileName1);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName1)
                , "Web Application Deployment failed");
    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplication() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, vhostName);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplicationWithLocalhost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, null);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertNotEquals("<status>success</status>\n", response, "Unexpected response: " + response);

    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplicationWithDifferentVirtualHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, vhostName1);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND,
                "Unexpectedly able to access the Webapp via invalid virtual host. Received response code " + statusCode);

    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplicationsInDifferentVirtualHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, vhostName);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

        getRequest = invokeWebapp(webAppURL, webAppName1, vhostName1);
        response = getRequest.getResponseBodyAsString();
        statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertTrue(response.contains("Hello 1!"), "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = {"testWebApplicationDeployment", "testInvokeWebApplication",
            "testInvokeWebApplicationWithLocalhost", "testInvokeWebApplicationWithDifferentVirtualHost",
            "testInvokeWebApplicationsInDifferentVirtualHost"})
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, vhostName);
        webAppAdminClient.deleteWebAppFile(webAppFileName, vhostName1);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, vhostName);
        int statusCode = getRequest.getStatusCode();
        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed , Status code: " + statusCode);
    }

    @Test (dependsOnMethods = {"testDeleteWebApplication"})
    public void testWebApplicationDeploymentInDefaultHost() throws Exception {
        uploadWarFile("webapps", webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, null);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithLocalhost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, "localhost");
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithNonExistingHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, "nonexisting.com");
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithOtherExistingHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, vhostName);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response with other virtual host , Status code: " + statusCode);

    }

    @Test(dependsOnMethods = {"testWebApplicationDeploymentInDefaultHost", "testInvokeWebApplicationInDefaultHost",
        "testInvokeWebApplicationInDefaultHostWithLocalhost" ,"testInvokeWebApplicationInDefaultHostWithNonExistingHost",
        "testInvokeWebApplicationInDefaultHostWithOtherExistingHost"})
    public void testDeleteWebApplicationInDefaultHost() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                        backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName, vhostName);
        int statusCode = getRequest.getStatusCode();
        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed , Status code: " + statusCode);
    }

    private void uploadWarFile(String appBaseDir, String webAppFileName) throws IOException {
        //add war file to a virtual host appBase
        String sourceLocation = TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" +
                File.separator + "AS" + File.separator + "war" + File.separator + webAppFileName;
        String targetLocation = System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" +
                File.separator + "deployment" + File.separator + "server";
        if (appBaseDir != null) {
            targetLocation += File.separator + appBaseDir;
        }
        FileManager.copyResourceToFileSystem(sourceLocation, targetLocation, webAppFileName);
    }

    private GetMethod invokeWebapp(String webAppURL, String webAppName, String vhostName) throws IOException {
        String webappUrl = webAppURL + "/" + webAppName + "/";
        HttpClient client = new HttpClient();
        GetMethod getRequest = new GetMethod(webappUrl);
        if (vhostName != null) {
            //set Host tag value of request header to $vhostName
            //(This avoids the requirement to add an entry to etc/hosts/ to pass this test case)
            getRequest.getParams().setVirtualHost(vhostName);
        }
        Calendar startTime = Calendar.getInstance();
        while ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) < GET_RESPONSE_DELAY) {
            client.executeMethod(getRequest);
            if (!getRequest.getResponseBodyAsString().isEmpty()) {
                return getRequest;
            }
        }

        return getRequest;
    }
}
