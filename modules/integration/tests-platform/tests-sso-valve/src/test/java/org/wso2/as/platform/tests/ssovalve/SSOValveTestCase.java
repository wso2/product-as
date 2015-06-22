/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.as.platform.tests.ssovalve;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.jmeter.JMeterTest;
import org.wso2.carbon.automation.extensions.jmeter.JMeterTestManager;
import org.wso2.carbon.identity.sso.saml.stub.IdentitySAMLSSOConfigServiceIdentityException;
import org.wso2.carbon.identity.sso.saml.stub.types.SAMLSSOServiceProviderDTO;

import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class SSOValveTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(SSOValveTestCase.class);
    private static final String fooAppFileName = "foo-app.war";
    private static final String fooAppName = "foo-app";
    private static final String barAppFileName = "bar-app.war";
    private static final String barAppName = "bar-app";
    private String isBackendURL;
    private String asBackendURL;
    private String isSessionCookie;
    private String asSessionCookie;
    private WebAppAdminClient webAppAdminClient;
    private SAMLSSOConfigServiceClient ssoConfigServiceClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        AutomationContext identityServerAutomationContext =
                new AutomationContext("IS", "is001", TestUserMode.SUPER_TENANT_ADMIN);
        AutomationContext applicationServerAutomationContext =
                new AutomationContext("AS", "appServerInstance0001", TestUserMode.SUPER_TENANT_ADMIN);

        isBackendURL = identityServerAutomationContext.getContextUrls().getBackEndUrl();
        isSessionCookie = loginLogoutClient.login(identityServerAutomationContext.getContextTenant().getContextUser().getUserName(),
                identityServerAutomationContext.getContextTenant().getContextUser().getPassword(),
                identityServerAutomationContext.getInstance().getHosts().get("default"));
//        ssoConfigServiceClient = new SAMLSSOConfigServiceClient(isBackendURL, isSessionCookie);
        ssoConfigServiceClient = new SAMLSSOConfigServiceClient(isBackendURL,
                identityServerAutomationContext.getContextTenant().getContextUser().getUserName(),
                identityServerAutomationContext.getContextTenant().getContextUser().getPassword());
        addServiceProviders();

        asBackendURL = applicationServerAutomationContext.getContextUrls().getBackEndUrl();
        asSessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(asBackendURL, sessionCookie);
        // Uploading the foo-app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + fooAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(asBackendURL, asSessionCookie, fooAppName),
                "Foo Web Application Deployment failed");

        // Uploading the bar-app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "webapps" + File.separator + barAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(asBackendURL, asSessionCookie, barAppName),
                "Bar Web Application Deployment failed");

        // Waiting till the Apps available in the AS
        Thread.sleep(20000);
    }

    public void addServiceProviders() throws RemoteException, IdentitySAMLSSOConfigServiceIdentityException {

        //registering SP for foo-app
        SAMLSSOServiceProviderDTO fooAppDTO = createSsoServiceProviderDTO();
        fooAppDTO.setIssuer("foo-app");
        fooAppDTO.setAssertionConsumerUrl("https://localhost:9444/foo-app/acs");
        ssoConfigServiceClient.addServiceProvider(fooAppDTO);

        //registering SP for bar-app
        SAMLSSOServiceProviderDTO barAppDTO = createSsoServiceProviderDTO();
        barAppDTO.setIssuer("bar-app");
        barAppDTO.setAssertionConsumerUrl("https://localhost:9444/bar-app/acs");
        ssoConfigServiceClient.addServiceProvider(barAppDTO);

        SAMLSSOServiceProviderDTO[] samlssoServiceProviderDTOs = ssoConfigServiceClient
                .getServiceProviders().getServiceProviders();
    }

    @Test(groups = "wso2.as", description = "Testing SSO Valve")
    public void testSSOValve() throws Exception {

//        log.info("Starting Test.");
//        JMeterTest ssoTestScript = new JMeterTest(new File(TestConfigurationProvider.getResourceLocation() +
//                File.separator + "artifacts" + File.separator + "AS" + File.separator + "scripts"
//                + File.separator + "sso-valve_webapp-sso-test.jmx"));
//
//        JMeterTestManager manager = new JMeterTestManager();
//        manager.runTest(ssoTestScript);

//        log.info("Finished running SSO Valve test");
    }

    private SAMLSSOServiceProviderDTO createSsoServiceProviderDTO() {
        SAMLSSOServiceProviderDTO serviceProviderDTO = new SAMLSSOServiceProviderDTO();
        serviceProviderDTO.setCertAlias("wso2carbon");
        serviceProviderDTO.setLogoutURL("");
        serviceProviderDTO.setAttributeConsumingServiceIndex("");
        serviceProviderDTO.setUseFullyQualifiedUsername(Boolean.TRUE);
        serviceProviderDTO.setDoSingleLogout(Boolean.TRUE);
        serviceProviderDTO.setDoSignAssertions(Boolean.TRUE);
        serviceProviderDTO.setDoSignResponse(Boolean.TRUE);
        serviceProviderDTO.setEnableAttributeProfile(Boolean.TRUE);
        serviceProviderDTO.setEnableAttributesByDefault(Boolean.TRUE);
        serviceProviderDTO.setNameIDFormat("urn/oasis/names/tc/SAML/1.1/nameid-format/emailAddress");
        serviceProviderDTO.setIdPInitSSOEnabled(Boolean.FALSE);
        serviceProviderDTO.setDoEnableEncryptedAssertion(Boolean.FALSE);
        serviceProviderDTO.setDoValidateSignatureInRequests(Boolean.TRUE);

        return serviceProviderDTO;
    }
}
