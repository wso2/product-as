/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.webapp.security.agent;

import org.testng.annotations.Test;
import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.configuration.server.AppServerSecurity;
import org.wso2.appserver.configuration.server.AppServerSingleSignOn;
import org.wso2.appserver.webapp.security.TestConstants;
import org.wso2.appserver.webapp.security.TestUtils;
import org.wso2.appserver.webapp.security.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

/**
 * This is a class which defines unit test cases for single-sign-on (SSO) agent configurations.
 *
 * @since 6.0.0
 */
public class SSOAgentConfigurationTest {
    @Test(description = "Tests the generation of valid single-sign-on (SSO) agent configuration object")
    public void testValidSSOConfiguration() throws SSOException {
        SSOAgentConfiguration agentConfiguration = new SSOAgentConfiguration();

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        agentConfiguration.initialize(serverConfiguration, TestUtils.getDefaultWebAppSSOConfiguration());

        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_LOCATION, TestConstants
                .DEFAULT_KEY_STORE_LOCATION);
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_PASSWORD, TestConstants
                .DEFAULT_KEY_STORE_PASSWORD);
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_TYPE, TestConstants.DEFAULT_KEY_STORE_TYPE);

        agentConfiguration.getSAML2().setSSOX509Credential(new SSOX509Credential(
                serverConfiguration.getIdpCertificateAlias(), TestUtils.getDefaultServerSecurityConfiguration()));

        agentConfiguration.validate();
    }


    @Test(description = "Tests the invalid combination of setting service provider entity ID to null",
            expectedExceptions = {SSOException.class})
    public void testInvalidSPEntityIdCombination() throws SSOException {
        SSOAgentConfiguration agentConfiguration = new SSOAgentConfiguration();

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        WebAppSingleSignOn webappConfiguration = TestUtils.getDefaultWebAppSSOConfiguration();
        webappConfiguration.setIssuerId(null);
        agentConfiguration.initialize(serverConfiguration, webappConfiguration);

        agentConfiguration.validate();
    }

    @Test(description = "Tests the invalid combination of setting Assertion Consumer Service URL to null",
            expectedExceptions = {SSOException.class})
    public void testInvalidACSURLCombination() throws SSOException {
        SSOAgentConfiguration agentConfiguration = new SSOAgentConfiguration();

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        WebAppSingleSignOn webappConfiguration = TestUtils.getDefaultWebAppSSOConfiguration();
        webappConfiguration.setConsumerURL(null);
        agentConfiguration.initialize(serverConfiguration, webappConfiguration);

        agentConfiguration.validate();
    }

    @Test(description = "Tests the invalid combination of setting 'SSOX509Credential' to null while request signing" +
            "is enabled", expectedExceptions = {SSOException.class})
    public void testInvalidSignatureValidationWithoutSSOX509Credential() throws SSOException {
        SSOAgentConfiguration agentConfiguration = new SSOAgentConfiguration();

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        WebAppSingleSignOn webappConfiguration = TestUtils.getDefaultWebAppSSOConfiguration();
        webappConfiguration.enableRequestSigning(true);
        agentConfiguration.initialize(serverConfiguration, webappConfiguration);

        agentConfiguration.validate();
    }

    @Test(description = "Tests the invalid combination of requiring the signature application without setting the " +
            "entity certificate", expectedExceptions = {SSOException.class})
    public void testInvalidSignatureValidationWithoutEntityCert() throws SSOException {
        SSOAgentConfiguration agentConfiguration = new SSOAgentConfiguration();

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        WebAppSingleSignOn webappConfiguration = TestUtils.getDefaultWebAppSSOConfiguration();
        webappConfiguration.enableResponseSigning(true);
        agentConfiguration.initialize(serverConfiguration, webappConfiguration);

        agentConfiguration.getSAML2().setSSOX509Credential(new SSOX509Credential(null,
                TestUtils.getDefaultServerSecurityConfiguration()));

        agentConfiguration.validate();
    }

    @Test(description = "Tests the invalid combination of requiring the signature application without setting the " +
            "private key alias", expectedExceptions = {SSOException.class})
    public void testInvalidSignatureValidationWithoutPrivateKeyAlias() throws SSOException {
        SSOAgentConfiguration agentConfiguration = new SSOAgentConfiguration();

        AppServerSecurity securityConfiguration = TestUtils.getDefaultServerSecurityConfiguration();
        securityConfiguration.getKeystore().setKeyAlias(null);

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        WebAppSingleSignOn webappConfiguration = TestUtils.getDefaultWebAppSSOConfiguration();
        webappConfiguration.enableRequestSigning(true);
        agentConfiguration.initialize(serverConfiguration, webappConfiguration);

        agentConfiguration.getSAML2().setSSOX509Credential(new SSOX509Credential(serverConfiguration.
                getIdpCertificateAlias(), securityConfiguration));

        agentConfiguration.validate();
    }
}
