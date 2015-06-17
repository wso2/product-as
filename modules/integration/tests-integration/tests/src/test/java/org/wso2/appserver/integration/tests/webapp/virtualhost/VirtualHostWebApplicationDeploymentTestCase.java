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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

/*
*  catalina-server.xml should have a virtual host entry as follows to pass this test case
*   <Host name="www.vhost.com" unpackWARs="true" deployOnStartup="false" autoDeploy="false" appBase="${carbon.home}/repository/deployment/server/dir/"/>
*/

public class VirtualHostWebApplicationDeploymentTestCase extends ASIntegrationTest {
    private static int GET_RESPONSE_DELAY = 15 * 1000;
    private final String webAppFileName1 = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppFileName2 = "HelloWorldWebapp.war";
    private final String webAppName1 = "appServer-valied-deploymant-1.0.0";
    private final String webAppName2 = "HelloWorldWebapp";
    private final String appBaseDir1 = "dir1";
    private final String appBaseDir2 = "dir2";
    private final String vhostName1 = "www.vhost1.com";
    private final String vhostName2 = "www.vhost2.com";
    private ServerConfigurationManager serverManager;
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(asServer);

        //restart server with virtual hosts
        Path sourcePath = Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "tomcat", "catalina-server.xml");
        Path targetPath = Paths.get(System.getProperty(ServerConstants.CARBON_HOME), "repository", "conf", "tomcat", "catalina-server.xml");
        File sourceFile = new File(sourcePath.toAbsolutePath().toString());
        File targetFile = new File(targetPath.toAbsolutePath().toString());
        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        serverManager.restartForcefully();

        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void revertVirtualHostConfiguration() throws Exception {
        //reverting the changes done to appsever
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }
    }

    @Test
    public void testWebApplicationDeployment() throws Exception {
        uploadWarFile(appBaseDir1, webAppFileName1);
        uploadWarFile(appBaseDir2, webAppFileName2);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName1),
                "Web Application 1 Deployment failed");
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName2),
                "Web Application 2 Deployment failed");
    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplication() throws Exception {
        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, vhostName1);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplicationWithoutHostHeader() throws Exception {
        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, null);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertNotEquals("<status>success</status>\n", response, "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplicationWithDifferentVirtualHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, vhostName2);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND,
                "Unexpectedly able to access the Webapp via invalid virtual host. Received response code " + statusCode);

    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplicationsInDifferentVirtualHost() throws Exception {

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, vhostName1);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

        getRequest = invokeWebapp(webAppURL, webAppName2, vhostName2);
        response = getRequest.getResponseBodyAsString();
        statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertTrue(response.contains("Hello 1!"), "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = {"testWebApplicationDeployment", "testInvokeWebApplication",
            "testInvokeWebApplicationWithoutHostHeader", "testInvokeWebApplicationWithDifferentVirtualHost",
            "testInvokeWebApplicationsInDifferentVirtualHost"})
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName1, vhostName1);
        webAppAdminClient.deleteWebAppFile(webAppFileName2, vhostName2);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName1),
                "Web Application unDeployment failed");

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, vhostName1);
        int statusCode = getRequest.getStatusCode();
        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed , Status code: " + statusCode);
    }

    @Test(dependsOnMethods = {"testDeleteWebApplication"})
    public void testWebApplicationDeploymentInDefaultHost() throws Exception {
        uploadWarFile("webapps", webAppFileName1);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName1),
                "Web Application Deployment failed");
    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithoutHostHeader() throws Exception {
        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, null);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithLocalhost() throws Exception {
        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, "localhost");
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithNonExistingHost() throws Exception {
        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, "nonexisting.com");
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_OK, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);
    }

    @Test(dependsOnMethods = "testWebApplicationDeploymentInDefaultHost")
    public void testInvokeWebApplicationInDefaultHostWithOtherExistingHost() throws Exception {
        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, vhostName1);
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response with other virtual host , Status code: " + statusCode);
    }

    @Test(dependsOnMethods = {"testWebApplicationDeploymentInDefaultHost", "testInvokeWebApplicationInDefaultHostWithoutHostHeader",
            "testInvokeWebApplicationInDefaultHostWithLocalhost", "testInvokeWebApplicationInDefaultHostWithNonExistingHost",
            "testInvokeWebApplicationInDefaultHostWithOtherExistingHost"})
    public void testDeleteWebApplicationInDefaultHost() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName1, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName1),
                "Web Application unDeployment failed");

        GetMethod getRequest = invokeWebapp(webAppURL, webAppName1, vhostName1);
        int statusCode = getRequest.getStatusCode();
        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed , Status code: " + statusCode);
    }

    private void uploadWarFile(String appBaseDir, String webAppFileName) throws IOException {
        //add war file to a virtual host appBase
        Path sourcePath = Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "war", webAppFileName);
        Path targetPath = Paths.get(System.getProperty(ServerConstants.CARBON_HOME), "repository", "deployment", "server");
        String targetLocation = targetPath.toAbsolutePath().toString();
        if (appBaseDir != null) {
            targetLocation += File.separator + appBaseDir;
        }
        FileManager.copyResourceToFileSystem(sourcePath.toAbsolutePath().toString(), targetLocation, webAppFileName);
    }

    private GetMethod invokeWebapp(String baseURL, String webappName, String vHostName) throws IOException {
        String webappUrl = baseURL + "/" + webappName + "/";
        HttpClient client = new HttpClient();
        GetMethod getRequest = new GetMethod(webappUrl);
        if (vHostName != null) {
            //set Host tag value of request header to vHostName
            //(This avoids the requirement to add an entry to etc/hosts/ to pass this test case)
            getRequest.getParams().setVirtualHost(vHostName);
        }
        Calendar startTime = Calendar.getInstance();
        while ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) < GET_RESPONSE_DELAY) {
            client.executeMethod(getRequest);
            if (!getRequest.getResponseBodyAsString().isEmpty()) {
                return getRequest;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }

        return getRequest;
    }
}
