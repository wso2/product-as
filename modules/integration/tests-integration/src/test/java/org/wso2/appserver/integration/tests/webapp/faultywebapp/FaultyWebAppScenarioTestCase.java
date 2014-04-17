package org.wso2.appserver.integration.tests.webapp.faultywebapp;

import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class uploads, deploys a faulty web app , check deployment status and construct related test scenarios
 */
public class FaultyWebAppScenarioTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(FaultyWebAppScenarioTestCase.class);
    private static final String INVALID_WAR_FILE_NAME = "appServer-invalied-deploymant-1.0.0.war";
    private WebAppAdminClient webAppAdminClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void webApplicationDelete() throws Exception {
        webAppAdminClient.deleteWebAppFile("SimpleServlet.war");
        log.info("SimpleServlet.war deleted successfully");  // removing SimpleServlet.war uploaded
    }

    @Test(groups = "wso2.as", description = "faulty web app upload and verify deployment")
    public void faultyWebAppDeployment() throws Exception {
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator
                + INVALID_WAR_FILE_NAME;
        webAppAdminClient.warFileUplaoder(location);   // uploading the faulty web app
        // checking the deployment status - success or fail
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie,
                "appServer-invalied-deploymant-1.0.0"), "Web Application Deployment failed");
        // getting the faulty web app webAppMetadata list which contains the details of faulty web apps
        WebappMetadata[] webAppMetadata =
                (webAppAdminClient.getPagedFaultyWebappsSummary("", "", 1).getWebapps()[0]).getVersionGroups();
        for (WebappMetadata aWebAppMetadata : webAppMetadata) {
            if (aWebAppMetadata.getWebappFile().
                    equals(INVALID_WAR_FILE_NAME)) {
                assertTrue(aWebAppMetadata.getFaultException().  // ensuring the error exception
                        contains("java.lang.Exception: Error while deploying webapp:"));
            }
        }
    }

    @Test(groups = "wso2.as", description = "faulty .war file removal",
            dependsOnMethods = "faultyWebAppDeployment")
    public void faultyWebAppRemoval() throws Exception {
        List<String> faultyList = webAppAdminClient.getFaultyWebAppList(".war");
        if (faultyList.contains(INVALID_WAR_FILE_NAME)) {
            // removing the faulty web app
            webAppAdminClient.deleteFaultyWebApps(INVALID_WAR_FILE_NAME);
            log.info("Faulty web appServer-invalied-deploymant-1.0.0.war deleted");
        }
        // ensuring the war does not contain in the fulty list anymore
        assertFalse(webAppAdminClient.getFaultyWebAppList(".war").
                contains("appServer-invalied-deploymant-1.0.0"));
    }

    @Test(groups = "wso2.as", description = "non .war file upload",
            dependsOnMethods = "faultyWebAppRemoval")
    public void nonWarUpload() throws Exception {
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "car" +
                File.separator + "AxisCApp-1.0.0.car";
        webAppAdminClient.warFileUplaoder(location);   // uploading the web app
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL,
                sessionCookie, "AxisCApp-1.0.0"));
    }

    @Test(groups = "wso2.as", description = "Upload a .war file that has already been uploaded",
            dependsOnMethods = "nonWarUpload")
    public void alreadyUploadedWarFileUpload() throws Exception {
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" +
                File.separator + "appServer-valied-deploymant-1.0.0.war";
        webAppAdminClient.warFileUplaoder(location);   // uploading the web app
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL,
                sessionCookie, "appServer-valied-deploymant-1.0.0"),
                "appServer-valied-deploymant-1.0.0.war deployment failure");

        int numberOfWebApps = webAppAdminClient.getWebApplist("").size();  // number of webapps
        int numberOfFaultyWebApps = webAppAdminClient.getFaultyWebAppList("").size(); // faulty web apps
        webAppAdminClient.warFileUplaoder(location);   // re-uploading the web app
        int newNumberOfWebApps = webAppAdminClient.getWebApplist("").size(); // latest number of webapps
        int newNumberOfFaultyWebApps = webAppAdminClient.getFaultyWebAppList("").size(); // latest faulty web apps
        assertTrue(((newNumberOfWebApps == numberOfWebApps) && (newNumberOfFaultyWebApps == numberOfFaultyWebApps)),
                "appServer-valied-deploymant-1.0.0.war duplicated");
        webAppAdminClient.deleteWebAppFile("appServer-valied-deploymant-1.0.0.war");
        log.info("appServer-valied-deploymant-1.0.0.war deleted successfully");  // removing the artifact
    }

    @Test(groups = "wso2.as", description = "Upload a .war file uploaded once by rectifying the error",
            dependsOnMethods = "nonWarUpload")
    public void faultyWebAppErrorRectifiedRedeploy() throws Exception {
        String location = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator
                + "SimpleServlet-faulty.war";
        webAppAdminClient.warFileUplaoder(location);   // uploading the web app
        // checking the deployment status - success or fail
        assertFalse(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie,
                "appServer-invalied-deploymant-1.0.0"), "Web Application Deployment failed");
        webAppAdminClient.deleteFaultyWebApps("SimpleServlet-faulty.war"); // delete faulty web app
        log.info("Faulty web app , SimpleServlet-faulty.war deleted");

        // uploading the corrected web app
        String rectifiedWebAppLocation = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" +
                File.separator + "SimpleServlet.war";
        webAppAdminClient.warFileUplaoder(rectifiedWebAppLocation);   // uploading the web app
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL,
                sessionCookie, "SimpleServlet"),
                "SimpleServlet.war deployment failure");
        log.info("SimpleServlet.war deployed success.");
    }
}
