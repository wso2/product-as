package org.wso2.appserver.integration.tests.sso;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Testing the Content-Type to avoid pages getting downloaded in SAMLSSOValue
 */
public class TenantDomainAndContentHeaderTest extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(TenantDomainAndContentHeaderTest.class);
    private HttpURLConnectionClient httpURLConnectionClient = new HttpURLConnectionClient();
    private String asBackendURL;
    private String asSessionCookie;
    private WebAppAdminClient webAppAdminClient;
    private static final String fooAppFileName = "foo-app.war";
    private static final String fooAppName = "foo-app";
    private static final String fooAppT1FileName = "foo-app-t1.war";
    private static final String fooAppT1Name = "foo-app-t1";
    private HttpClient httpClient = new HttpClient();

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();

        ServerConfigurationManager serverManager = new ServerConfigurationManager(asServer);
        Path sourcePath = Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "tomcat", "catalina-server-sso.xml");
        Path targetPath = Paths.get(System.getProperty(ServerConstants.CARBON_HOME), "repository", "conf", "tomcat", "catalina-server.xml");
        File sourceFile = new File(sourcePath.toAbsolutePath().toString());
        File targetFile = new File(targetPath.toAbsolutePath().toString());
        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        serverManager.restartForcefully();

        super.init();
        AutomationContext applicationServerAutomationContext =
                new AutomationContext("AS", "appServerInstance0001", TestUserMode.SUPER_TENANT_ADMIN);
        asBackendURL = applicationServerAutomationContext.getContextUrls().getBackEndUrl();
        asSessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(asBackendURL, sessionCookie);
        // Uploading the foo-app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + fooAppFileName);
        // Uploading the foo-app-t1
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + fooAppT1FileName);

        TenantManagementServiceClient tenantManagementServiceClient =
                new TenantManagementServiceClient(backendURL, sessionCookie);
        tenantManagementServiceClient.addTenant("t1.com", "t1admin", "t1admin", "demo");
        AuthenticatorClient authClient = new AuthenticatorClient(asServer.getContextUrls().getBackEndUrl());
        String sessionT1 = authClient.login("t1admin@t1.com", "t1admin", "localhost");
        webAppAdminClient = new WebAppAdminClient(asBackendURL, sessionT1);
        // Uploading the foo-app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + fooAppFileName);
        // Uploading the foo-app-t1
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + fooAppT1FileName);

        // Verify webapp deployment
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(asBackendURL, asSessionCookie, fooAppName),
                "Foo Web Application Deployment failed in super tenant");
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(asBackendURL, asSessionCookie, fooAppT1Name),
                "Foo T1 Web Application Deployment failed in super tenant");
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(asBackendURL, sessionT1, fooAppName),
                "Foo Web Application Deployment failed in t1.com");
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(asBackendURL, sessionT1, fooAppT1Name),
                "Foo T1 Web Application Deployment failed in t1.com");
    }

    @Test(groups = "wso2.as", description = "Verifying the Content-Type header and no tenant domain in super tenant")
    public void testPresenceOfContentTypeHeaderAndNoTenantDomainInResponse()
            throws IOException, InterruptedException, XPathExpressionException {

        GetMethod getMethod = new GetMethod(asServer.getContextUrls().getWebAppURL() + "/foo-app");
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethod.getStatusLine());
            }

            Header[] headers = getMethod.getResponseHeaders();
            assertTrue(isContentTypeHeaderAvailable(headers));

            int index = getMethod.getResponseBodyAsString().indexOf("tenantDomain");
            assertEquals(index, -1, "Response contain tenant domain (response should not have tenant domain)");
        } finally {
            getMethod.releaseConnection();
        }
    }

    @Test(groups = "wso2.as", description = "Verifying the Content-Type header and no tenant domain in tenant t1.com")
    public void testPresenceOfContentTypeHeaderAndNoTenantDomainInResponseForTenantApp()
            throws IOException, InterruptedException, XPathExpressionException {

        GetMethod getMethod = new GetMethod(asServer.getContextUrls().getWebAppURL() + "/t/t1.com/webapps/foo-app");
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethod.getStatusLine());
            }

            Header[] headers = getMethod.getResponseHeaders();
            assertTrue(isContentTypeHeaderAvailable(headers));

            int index = getMethod.getResponseBodyAsString().indexOf("tenantDomain");
            assertEquals(index, -1, "Response contain tenant domain (response should not have tenant domain)");
        } finally {
            getMethod.releaseConnection();
        }
    }

    @Test(groups = "wso2.as", description = "Verifying the Tenant domain in super tenant")
    public void testPresenceOfContentTypeHeaderAndTenantDomainInResponse()
            throws IOException, InterruptedException, XPathExpressionException {

        GetMethod getMethod = new GetMethod(asServer.getContextUrls().getWebAppURL() + "/foo-app-t1");
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethod.getStatusLine());
            }

            int index = getMethod.getResponseBodyAsString().indexOf("tenantDomain");
            assertNotEquals(index, -1, "Response doesn't contain tenant domain");

            String temp = getMethod.getResponseBodyAsString()
                    .substring(getMethod.getResponseBodyAsString().indexOf("tenantDomain"));
            String tenantDomain = temp.substring(temp.indexOf("value='") + 7, temp.indexOf("value='") + 13);
            assertEquals(tenantDomain, "t1.com", "Invalid tenant domain in the response");
        } finally {
            getMethod.releaseConnection();
        }
    }

    @Test(groups = "wso2.as", description = "Verifying Tenant domain in tenant t1.com")
    public void testTenantDomainInResponseForTenantApp()
            throws IOException, InterruptedException, XPathExpressionException {

        GetMethod getMethod = new GetMethod(asServer.getContextUrls().getWebAppURL() + "/t/t1.com/webapps/foo-app-t1");
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Method failed: " + getMethod.getStatusLine());
            }

            int index = getMethod.getResponseBodyAsString().indexOf("tenantDomain");
            assertNotEquals(index, -1, "Response doesn't contain tenant domain");

            String temp = getMethod.getResponseBodyAsString()
                    .substring(getMethod.getResponseBodyAsString().indexOf("tenantDomain"));
            String tenantDomain = temp.substring(temp.indexOf("value='") + 7, temp.indexOf("value='") + 13);
            assertEquals(tenantDomain, "t1.com", "Invalid tenant domain in the response");
        } finally {
            getMethod.releaseConnection();
        }
    }

    private boolean isContentTypeHeaderAvailable(Header[] headers) {
        for (Header header : headers) {
            if (HttpHeaders.CONTENT_TYPE.equals(header.getName())) {
                if (header.getValue() != null && header.getValue().startsWith("text/html")) {
                    return true;
                }
            }
        }
        return false;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
