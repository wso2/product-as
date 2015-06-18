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
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.identity.sso.saml.stub.IdentitySAMLSSOConfigServiceIdentityException;
import org.wso2.carbon.identity.sso.saml.stub.types.SAMLSSOServiceProviderDTO;
import org.wso2.carbon.automation.extensions.servers.carbonserver.CarbonServerExtension;

import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class SSOValveTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(SSOValveTestCase.class);
    private static final String CONTENT_TYPE = "text/html";
    private static final String fooAppFileName = "foo-app.war";
    private static final String fooAppName = "foo-app";
    private static final String barAppFileName = "bar-app";
    private static final String barAppName = "bar-app.war";
    private WebAppAdminClient webAppAdminClient;
    private SAMLSSOConfigServiceClient ssoConfigServiceClient;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        AutomationContext identityServerAutomationContext = new AutomationContext("IS", "is001",
                TestUserMode.SUPER_TENANT_ADMIN);

        ssoConfigServiceClient = new SAMLSSOConfigServiceClient(backendURL, sessionCookie);
        addServiceProviders();

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        // Uploading the foo-app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + fooAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, fooAppName),
                "Foo Web Application Deployment failed");

        // Uploading the bar-app
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + barAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, barAppName),
                "BAR Web Application Deployment failed");

        // Waiting till the Apps available in the AS
        Thread.sleep(20000);
    }

    public void addServiceProviders() throws RemoteException, IdentitySAMLSSOConfigServiceIdentityException {

        //registering SP for foo-app
        SAMLSSOServiceProviderDTO fooAppDTO = createSsoServiceProviderDTO();
        fooAppDTO.setIssuer("foo-app");
        fooAppDTO.setAssertionConsumerUrl("http://localhost:9764/foo-app/acs");
        ssoConfigServiceClient.addServiceProvider(fooAppDTO);

        //registering SP for bar-app
        SAMLSSOServiceProviderDTO barAppDTO = createSsoServiceProviderDTO();
        barAppDTO.setIssuer("bar-app");
        barAppDTO.setAssertionConsumerUrl("http://localhost:9764/bar-app/acs");
        ssoConfigServiceClient.addServiceProvider(barAppDTO);


        SAMLSSOServiceProviderDTO[] samlssoServiceProviderDTOs = ssoConfigServiceClient
                .getServiceProviders().getServiceProviders();

    }

    private SAMLSSOServiceProviderDTO createSsoServiceProviderDTO() {
        SAMLSSOServiceProviderDTO serviceProviderDTO = new SAMLSSOServiceProviderDTO();
//        serviceProviderDTO.setIssuer("foo-app");
//        serviceProviderDTO.setAssertionConsumerUrl("http://localhost:9764/foo-app/acs");
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

    /*private static class SAMLConfig{
        private TestUserMode userMode;
        private User user;
        private HttpBinding httpBinding;
        private ClaimType claimType;
        private App app;

        private SAMLConfig(TestUserMode userMode, User user, HttpBinding httpBinding, ClaimType claimType, App app) {
            this.userMode = userMode;
            this.user = user;
            this.httpBinding = httpBinding;
            this.claimType = claimType;
            this.app = app;
        }

        public TestUserMode getUserMode() {
            return userMode;
        }

        public App getApp() {
            return app;
        }

        public User getUser() {
            return user;
        }

        public ClaimType getClaimType() {
            return claimType;
        }

        public HttpBinding getHttpBinding() {
            return httpBinding;
        }

        @Override
        public String toString() {
            return "SAMLConfig[" +
                    ", userMode=" + userMode.name() +
                    ", user=" + user.getUsername() +
                    ", httpBinding=" + httpBinding +
                    ", claimType=" + claimType +
                    ", app=" + app.getArtifact() +
                    ']';
        }
    }*/

}
