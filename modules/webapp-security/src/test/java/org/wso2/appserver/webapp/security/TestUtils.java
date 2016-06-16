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
package org.wso2.appserver.webapp.security;

import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.configuration.server.AppServerSecurity;
import org.wso2.appserver.configuration.server.AppServerSingleSignOn;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the utility functions required for the unit tests related to SAML 2.0 based single-sign-on (SSO).
 *
 * @since 6.0.0
 */
public class TestUtils {
    public static AppServerSingleSignOn getDefaultServerSSOConfiguration() {
        AppServerSingleSignOn configuration = new AppServerSingleSignOn();

        configuration.setIdpURL(TestConstants.DEFAULT_IDP_URL);
        configuration.setIdpEntityId(TestConstants.DEFAULT_IDP_ENTITY_ID);
        configuration.setSignatureValidatorImplClass(TestConstants.DEFAULT_SIGN_VALIDATOR);
        configuration.setIdpCertificateAlias(TestConstants.DEFAULT_IDP_CERT_ALIAS);
        configuration.setACSBase(TestConstants.DEFAULT_APPLICATION_SERVER_URL);

        return configuration;
    }

    public static AppServerSecurity getDefaultServerSecurityConfiguration() {
        AppServerSecurity configuration = new AppServerSecurity();

        AppServerSecurity.Keystore keystore = new AppServerSecurity.Keystore();
        keystore.setLocation(TestConstants.DEFAULT_KEY_STORE_LOCATION);
        keystore.setType(TestConstants.DEFAULT_KEY_STORE_TYPE);
        keystore.setPassword(TestConstants.DEFAULT_KEY_STORE_PASSWORD);
        keystore.setKeyAlias(TestConstants.DEFAULT_KEY_ALIAS);
        keystore.setKeyPassword(TestConstants.DEFAULT_KEY_PASSWORD);

        configuration.setKeystore(keystore);

        return configuration;
    }

    public static WebAppSingleSignOn getDefaultWebAppSSOConfiguration() {
        WebAppSingleSignOn configuration = new WebAppSingleSignOn();

        configuration.enableSSO(true);
        configuration.setHttpBinding(TestConstants.DEFAULT_HTTP_BINDING);
        configuration.setIssuerId(TestConstants.DEFAULT_SP_ENTITY_ID);
        configuration.setConsumerURL(TestConstants.DEFAULT_ACS_URL);
        configuration.setConsumerURLPostfix(TestConstants.DEFAULT_CONSUMER_URL_POSTFIX);

        List<String> skipURIs = new ArrayList<>();
        skipURIs.add(TestConstants.SKIP_URI_ONE);
        skipURIs.add(TestConstants.SKIP_URI_TWO);
        WebAppSingleSignOn.SkipURIs uris = new WebAppSingleSignOn.SkipURIs();
        uris.setSkipURIs(skipURIs);
        configuration.setSkipURIs(uris);

        configuration.setOptionalParams(TestConstants.DEFAULT_QUERY_PARAMS);
        configuration.enableSLO(true);
        configuration.setSLOURLPostfix(TestConstants.DEFAULT_SLO_URL_POSTFIX);
        configuration.enableAssertionEncryption(false);
        configuration.enableAssertionSigning(false);
        configuration.enableRequestSigning(false);
        configuration.enableResponseSigning(false);

        return configuration;
    }
}
