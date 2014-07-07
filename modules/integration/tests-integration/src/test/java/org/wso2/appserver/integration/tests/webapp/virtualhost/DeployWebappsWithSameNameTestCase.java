package org.wso2.appserver.integration.tests.webapp.virtualhost;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.testng.Assert;
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
import java.util.Calendar;

import static org.testng.Assert.assertTrue;


/*
*  catalina-server.xml should have a virtual host entry as follows to pass this test case
*   <Host name="www.vhost.com" unpackWARs="true" deployOnStartup="false" autoDeploy="false" appBase="${carbon.home}/repository/deployment/server/dir/"/>
*   <Host name="www.vhost1.com" unpackWARs="true" deployOnStartup="false" autoDeploy="false" appBase="${carbon.home}/repository/deployment/server/dir1/"/>
*/

public class DeployWebappsWithSameNameTestCase extends ASIntegrationTest {

    private static int GET_RESPONSE_DELAY = 30 * 1000;
    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";
    private final String appBaseDir1 = "dir";
    private final String appBaseDir2 = "dir1";
    private final String vhostName1 = "www.vhost.com";
    private final String vhostName2 = "www.vhost1.com";
    private ServerConfigurationManager serverManager;
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(asServer);

        //restart server with virtual hosts
        //TODO replace server.xml inside afterclass (in both test classes)
        File sourceFile = new File(TestConfigurationProvider.getResourceLocation()+File.separator+
                "artifacts"+File.separator+"AS"+File.separator+"tomcat"+File.separator+"appbase2"+File.separator+"catalina-server.xml");
        File targetFile = new File(System.getProperty(ServerConstants.CARBON_HOME)+File.separator+"repository"+File.separator+"conf"+
                File.separator+"tomcat"+File.separator+"catalina-server.xml");
        serverManager.applyConfigurationWithoutRestart(sourceFile,targetFile, true);
        serverManager.restartForcefully();

        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);
    }

    @AfterTest(alwaysRun = true)
    public void revertVirtualhostConfiguration() throws Exception {
        //reverting changes may fail due to  jira issue - WSAS-1736
        //reverting the changes done to appsever
        if (serverManager != null) {
            serverManager.restoreToLastConfiguration();
        }
    }

    @Test
    public void testWebApplication1Deployment() throws Exception {
        uploadWarFileToAppBase(appBaseDir1);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test
    public void testWebApplication2Deployment() throws Exception {
        uploadWarFileToAppBase(appBaseDir2);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(dependsOnMethods = {"testWebApplication1Deployment","testWebApplication2Deployment"})
    public void testInvokeWebApplications() throws Exception {

        GetMethod getRequest1 = getHttpRequest(vhostName1);
        String response1 = getRequest1.getResponseBodyAsString();

        GetMethod getRequest2 = getHttpRequest(vhostName2);
        String response2 = getRequest2.getResponseBodyAsString();

        assertTrue((response1.equals("<status>success</status>\n")) &&
                (response2.equals("<status>success</status>\n")), "Web app invocation fail");

    }

    @Test (dependsOnMethods = {"testInvokeWebApplications"})
    public void testDeleteWebApplications() throws Exception {
        webAppAdminClient.deleteWebAppFile(vhostName1+":"+webAppFileName);
        webAppAdminClient.deleteWebAppFile(vhostName2+":"+webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        GetMethod getRequest1 = getHttpRequest(vhostName1);
        int statusCode1 = getRequest1.getStatusCode();

        GetMethod getRequest2 = getHttpRequest(vhostName2);
        int statusCode2 = getRequest2.getStatusCode();
        Assert.assertEquals(statusCode1, 404, "Response code mismatch. Client request " +
                "got a response even after web app 1 is undeployed");
        Assert.assertEquals(statusCode2,404,"Response code mismatch. Client request" +
                "got a response even after web app 2 is undeployed");
    }

    private void uploadWarFileToAppBase(String appBaseDir) throws IOException {
        //add war file to a virtual host appBase
        String sourceLocation = TestConfigurationProvider.getResourceLocation()+File.separator+"artifacts"+
                File.separator+"AS"+File.separator+"war"+File.separator+webAppFileName;
        String targetLocation = System.getProperty(ServerConstants.CARBON_HOME)+ File.separator + "repository" +
                File.separator +"deployment"+File.separator+"server"+File.separator+appBaseDir;
        FileManager.copyResourceToFileSystem(sourceLocation, targetLocation, webAppFileName);
    }

    private GetMethod getHttpRequest(String vhostName) throws IOException {
        String webappUrl = webAppURL + "/" +webAppName+"/";
        HttpClient client = new HttpClient();
        GetMethod getRequest = new GetMethod(webappUrl);
        //set Host tag value of request header to $vhostName
        getRequest.getParams().setVirtualHost(vhostName);
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < GET_RESPONSE_DELAY){
            client.executeMethod(getRequest);
            if(!getRequest.getResponseBodyAsString().isEmpty()){
                return getRequest;
            }
        }

        return getRequest;
    }
}
