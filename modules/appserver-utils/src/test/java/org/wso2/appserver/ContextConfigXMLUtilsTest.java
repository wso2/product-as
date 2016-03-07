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

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.context.ClassLoaderConfiguration;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerException;
import org.wso2.appserver.utils.XMLUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines unit-tests for context level configurations.
 *
 * @since 6.0.0
 */
public class ContextConfigXMLUtilsTest {
    @Test
    public void loadObjectFromFilePath() {
        Path xmlSchema = Paths.get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER,
                TestConstants.WEBAPP_DESCRIPTOR_XSD_FILE);
        Path parent = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.PARENT_DESCRIPTOR);
        Path child = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.CHILD_DESCRIPTOR);

        try {
            ContextConfiguration parentConfig = XMLUtils.
                    getUnmarshalledObject(parent, xmlSchema, ContextConfiguration.class);
            ContextConfiguration childConfig = XMLUtils.
                    getUnmarshalledObject(child, xmlSchema, ContextConfiguration.class);
            parentConfig.merge(childConfig);
            Assert.assertTrue(compare(parentConfig, prepareDefault()));
        } catch (ApplicationServerException e) {
            Assert.fail();
        }
    }

    private static ContextConfiguration prepareDefault() {
        ContextConfiguration configuration = new ContextConfiguration();
        configuration.setClassLoaderConfiguration(prepareClassLoaderConfiguration());
        configuration.setSingleSignOnConfiguration(prepareSSOConfiguration());
        return configuration;
    }

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
        ssoConfiguration.setIssuerId(TestConstants.ISSUER_ID);
        ssoConfiguration.setConsumerURL(TestConstants.CONSUMER_URL);
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

        SSOConfiguration.Property property = new SSOConfiguration.Property();
        property.setKey(TestConstants.RELAY_STATE_KEY);
        property.setValue(TestConstants.RELAY_STATE_VALUE);
        List<SSOConfiguration.Property> properties = new ArrayList<>();
        properties.add(property);
        ssoConfiguration.setProperties(properties);

        return ssoConfiguration;
    }

    private static boolean compare(ContextConfiguration actual, ContextConfiguration expected) {
        return ((compareClassloadingConfigs(actual.getClassLoaderConfiguration(),
                expected.getClassLoaderConfiguration())) && (compareSSOConfigurations(
                actual.getSingleSignOnConfiguration(), expected.getSingleSignOnConfiguration())));
    }

    private static boolean compareClassloadingConfigs(ClassLoaderConfiguration actual,
            ClassLoaderConfiguration expected) {
        return ((actual != null) && (expected != null) && (actual.isParentFirst() == expected.isParentFirst())
                && (actual.getEnvironments().trim().equals(expected.getEnvironments())));
    }

    private static boolean compareSSOConfigurations(SSOConfiguration actual, SSOConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            boolean skipURIs = actual.getSkipURIs().getSkipURIs().stream().filter(skipURI ->
                    expected.getSkipURIs().getSkipURIs().stream().filter(uri -> uri.trim().equals(skipURI)).count() > 0)
                    .count() == expected.getSkipURIs().getSkipURIs().size();
            boolean handlingConsumerURLAfterSLO = (actual.handleConsumerURLAfterSLO() == expected.
                    handleConsumerURLAfterSLO());
            boolean queryParams = actual.getQueryParams().trim().equals(expected.getQueryParams());
            boolean appServerURL = actual.getApplicationServerURL().trim().equals(expected.getApplicationServerURL());
            boolean enableSSO = (actual.isSSOEnabled() == expected.isSSOEnabled());
            boolean requestURLPostfix = actual.getRequestURLPostFix().trim().equals(expected.getRequestURLPostFix());
            boolean binding = actual.getHttpBinding().trim().equals(expected.getHttpBinding());
            boolean issuerID = actual.getIssuerId().trim().equals(expected.getIssuerId());
            boolean consumerURL = actual.getConsumerURL().trim().equals(expected.getConsumerURL());
            boolean consumerURLPostfix = actual.getConsumerURLPostFix().trim().equals(expected.getConsumerURLPostFix());
            boolean serviceIndex = actual.getAttributeConsumingServiceIndex().trim().
                    equals(expected.getAttributeConsumingServiceIndex());
            boolean enableSLO = (actual.isSLOEnabled() == expected.isSLOEnabled());
            boolean sloURLPostfix = actual.getSLOURLPostFix().trim().equals(expected.getSLOURLPostFix());
            boolean assertionSigning = (actual.isAssertionSigningEnabled() == expected.isAssertionSigningEnabled());
            boolean assertionEncryption = (actual.isAssertionEncryptionEnabled() == expected.
                    isAssertionEncryptionEnabled());
            boolean requestSigning = (actual.isRequestSigningEnabled() == expected.isRequestSigningEnabled());
            boolean responseSigning = (actual.isResponseSigningEnabled() == expected.isResponseSigningEnabled());
            boolean forceAuthn = (actual.isForceAuthnEnabled() == expected.isForceAuthnEnabled());
            boolean passiveAuthn = (actual.isPassiveAuthnEnabled() == expected.isPassiveAuthnEnabled());

            boolean properties = actual.getProperties().stream().filter(property -> expected.getProperties().stream()
                    .filter(exp -> (property.getKey().trim().equals(exp.getKey()) && property.getValue().trim()
                            .equals(exp.getValue()))).count() > 0).count() == expected.getProperties().size();

            return (skipURIs && handlingConsumerURLAfterSLO && queryParams && appServerURL && enableSSO
                    && requestURLPostfix && binding && issuerID && consumerURL && consumerURLPostfix && serviceIndex
                    && enableSLO && sloURLPostfix && assertionSigning && assertionEncryption && requestSigning
                    && responseSigning && forceAuthn && passiveAuthn && properties);
        } else {
            return false;
        }
    }
}
