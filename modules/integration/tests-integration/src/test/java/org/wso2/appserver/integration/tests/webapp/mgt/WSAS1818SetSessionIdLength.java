package org.wso2.appserver.integration.tests.webapp.mgt;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.Utils;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.extensions.carbonserver.CarbonServerManager;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class WSAS1818SetSessionIdLength extends ASIntegrationTest {

    private final String webAppFileName = "sessionid-webapp-1.0.0.war";
    private final String webAppName = "sessionid-webapp-1.0.0";
    private WebAppAdminClient webAppAdminClient;
    CarbonServerManager serverManager;
    private final int portOffSet = 1;
    private final String configDir = "sessionIdLength";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        String dirPath = FrameworkPathUtil.getSystemResourceLocation() + ASIntegrationConstants.CONFIGS_PATH_KEY +
                File.separator + configDir;
        serverManager = Utils.configureServer(asServer, portOffSet, dirPath);
        //todo update port/backendURL with offset
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppFileName);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "Invoke web application",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testCheckSesionIdLength() throws Exception {
        String webAppURLLocal = webAppURL + "/sessionid-webapp-1.0.0";

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getData().length(), 100, "Session id length mismatched");
    }

    @AfterClass(alwaysRun = true)
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");

        String webAppURLLocal = webAppURL + "/sessionid-webapp-1.0.0";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                "got a response even after web app is undeployed");
    }
}
