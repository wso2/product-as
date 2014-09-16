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
package org.wso2.appserver.integration.tests.webapp.virtualhost;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterTest;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/*
*  catalina-server.xml should have a virtual host entry as follows to pass this test case
*   <Host name="www.vhost.com" unpackWARs="true" deployOnStartup="false" autoDeploy="false" appBase="${carbon.home}/repository/deployment/server/dir/"/>
*/

public class VitualHostWebApplicationDeploymentTestCase extends ASIntegrationTest {
    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";
    private final String appBaseDir = "dir";
    private final String vhostName = "www.vhost.com";
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

    @AfterTest(alwaysRun = true)
    public void revertVirtualhostConfiguration() throws Exception {
        //reverting the changes done to appsever
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }

    }

    @Test
    public void testWebApplicationDeployment() throws Exception {
        uploadWarFileToAppBase();
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(dependsOnMethods = "testWebApplicationDeployment")
    public void testInvokeWebApplication() throws Exception {

        GetMethod getRequest = invokeWebapp();
        String response = getRequest.getResponseBodyAsString();
        int statusCode = getRequest.getStatusCode();

        assertEquals(statusCode, HttpStatus.SC_ACCEPTED, "Request failed. Received response code " + statusCode);
        assertEquals("<status>success</status>\n", response, "Unexpected response: " + response);

    }

    @Test(dependsOnMethods = {"testWebApplicationDeployment", "testInvokeWebApplication"})
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(vhostName + ":" + webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        GetMethod getRequest = invokeWebapp();
        int statusCode = getRequest.getStatusCode();
        assertEquals(statusCode, HttpStatus.SC_NOT_FOUND, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed , Status code: " + statusCode);
    }

    private void uploadWarFileToAppBase() throws IOException {
        //add war file to a virtual host appBase
        String sourceLocation = TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" +
                File.separator + "AS" + File.separator + "war" + File.separator + webAppFileName;
        String targetLocation = System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository" +
                File.separator + "deployment" + File.separator + "server" + File.separator + appBaseDir;
        FileManager.copyResourceToFileSystem(sourceLocation, targetLocation, webAppFileName);
    }

    private GetMethod invokeWebapp() throws IOException {
        String webappUrl = webAppURL + "/" + webAppName + "/";
        HttpClient client = new HttpClient();
        GetMethod getRequest = new GetMethod(webappUrl);
        //set Host tag value of request header to $vhostName
        //(This avoids the requirement to add an entry to etc/hosts/ to pass this test case)
        getRequest.getParams().setVirtualHost(vhostName);
        client.executeMethod(getRequest);

        return getRequest;
    }
}
