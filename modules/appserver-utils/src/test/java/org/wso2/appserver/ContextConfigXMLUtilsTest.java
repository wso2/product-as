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
package org.wso2.appserver;

import org.wso2.appserver.configuration.context.ClassLoaderConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines unit-tests for context level configurations.
 *
 * @since 6.0.0
 */
public class ContextConfigXMLUtilsTest {

    private static ClassLoaderConfiguration prepareClassLoaderConfiguration() {
        ClassLoaderConfiguration classloading = new ClassLoaderConfiguration();
        classloading.enableParentFirst(true);
        classloading.setEnvironments(TestConstants.JAXRS_ENV_NAME);
        return classloading;
    }

    private static SSOConfiguration prepareSSOConfiguration() {
        SSOConfiguration ssoConfiguration = new SSOConfiguration();

        SSOConfiguration.SkipURIs skipURIs = new SSOConfiguration.SkipURIs();
        List<String> uris = new ArrayList<>();
        uris.add(TestConstants.SKIP_URI);
        skipURIs.setSkipURIs(uris);
        ssoConfiguration.setSkipURIs(skipURIs);

        ssoConfiguration.enableHandlingConsumerURLAfterSLO(false);
        ssoConfiguration.setQueryParams(TestConstants.QUERY_PARAMS);
        ssoConfiguration.setApplicationServerURL(TestConstants.APP_SERVER_URL);
        ssoConfiguration.enableSSO(true);
        ssoConfiguration.setRequestURLPostFix(TestConstants.REQUEST_URL_POSTFIX);
        ssoConfiguration.setHttpBinding(TestConstants.SAML_BINDING);
        ssoConfiguration.setConsumerURLPostFix(TestConstants.CONSUMER_URL_POSTFIX);
        ssoConfiguration.setAttributeConsumingServiceIndex(TestConstants.ATTR_CONSUMER_SERVICE_INDEX);
        ssoConfiguration.enableSLO(true);
        ssoConfiguration.setSLOURLPostFix(TestConstants.SLO_URL_POSTFIX);
        ssoConfiguration.enableAssertionSigning(true);
        ssoConfiguration.enableAssertionEncryption(true);
        ssoConfiguration.enableRequestSigning(true);
        ssoConfiguration.enableResponseSigning(true);
        ssoConfiguration.enableForceAuthn(false);
        ssoConfiguration.enablePassiveAuthn(false);

        return ssoConfiguration;
    }

    /*private static boolean compareSSOConfigurations(SSOConfiguration actual, SSOConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            //  TODO: SKIP URIS

            boolean handlingConsumerURLAfterSLO = (actual.handleConsumerURLAfterSLO() == expected.
                    handleConsumerURLAfterSLO());

        } else {
            return false;
        }
    }*/

}
