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

import org.apache.catalina.Lifecycle;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.wso2.appserver.configuration.context.WebAppSingleSignOn;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the utility functions required for the unit tests related to SAML 2.0 based single-sign-on (SSO).
 *
 * @since 6.0.0
 */
public class TestUtils {
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

    public static List<Lifecycle> generateSampleTomcatComponents() {
        List<Lifecycle> components = new ArrayList<>();
        components.add(new StandardHost());
        components.add(new StandardServer());

        return components;
    }
}
