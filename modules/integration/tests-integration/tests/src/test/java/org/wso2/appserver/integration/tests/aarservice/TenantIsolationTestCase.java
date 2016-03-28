package org.wso2.appserver.integration.tests.aarservice;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationLoggingUtil;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;

import java.io.File;
import java.io.IOException;

/**
 * Test tenant isolation test case.
 */

public class TenantIsolationTestCase extends ASIntegrationTest {

    private final String carbonLogFile = CarbonBaseUtils.getCarbonHome() + File.separator +
            "repository" + File.separator + "logs" + File.separator + "wso2carbon.log";
    private final String serviceURI = "http://localhost:9863/services/t/t1.com/echo/";
    private final String disableServiceURI = "https://localhost:9543/t/t1.com/carbon/service-mgt/" +
            "change_service_state_ajaxprocessor.jsp?serviceName=echo&isActive=false";
    private HttpsURLConnectionClient httpsClient = new HttpsURLConnectionClient();

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();

        // create a tenant
        TenantManagementServiceClient tenantManagementServiceClient =
                new TenantManagementServiceClient(backendURL,
                        sessionCookie);

        //adding two tenants
        tenantManagementServiceClient.addTenant("t1.com", "t1admin", "t1admin", "demo");
        tenantManagementServiceClient.addTenant("t2.com", "t2admin", "t2admin", "demo");

        // log as tenant t1 and deploy artifacts
        AuthenticatorClient authClient = new AuthenticatorClient(asServer.getContextUrls().getBackEndUrl());
        String sessionT1 = authClient.login("t1admin@t1.com", "t1admin", "localhost");

        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(backendURL, sessionT1);
        aarServiceUploaderClient.uploadAARFile("Echo.aar",
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "Echo.aar", "");
        String axis2Service = "Echo";
        isServiceDeployed(axis2Service);
        authClient.logOut();
    }


    @Test(groups = "wso2.esb", description = "Try disable proxy from other tenant t2", enabled = false)
    public void testTenantIsolation() throws IOException, InterruptedException {
        // Try disable axis2 service from another tenant t2
        httpsClient.getWithBasicAuth(disableServiceURI, null, "t2admin@t2.com", "t2admin");
        httpsClient.getRequest(serviceURI, null);
        String[] commonsLogs = ASIntegrationLoggingUtil.getLogsFromLogfile(
                new File(carbonLogFile));
        boolean isExpectedErrorOccurred =
                ASIntegrationLoggingUtil.searchLogRecord("Access Denied. A user t2admin@t2.com is trying to " +
                        "access services in domain t1.com", commonsLogs);
        Assert.assertTrue(isExpectedErrorOccurred, "Could disable the axis2 service of t1 using t2");

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
