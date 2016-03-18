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
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.configuration.context.ClassLoaderConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;
import org.wso2.appserver.configuration.listeners.Utils;
import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines unit-tests for context level configurations.
 *
 * @since 6.0.0
 */
public class AppServerWebAppConfigurationTest {
    @Test(description = "Loads the XML file content of the WSO2 App Server specific webapp descriptor")
    public void loadObjectFromFilePath() throws ApplicationServerConfigurationException {
        Path xmlSchema = Paths.get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER,
                TestConstants.WEBAPP_DESCRIPTOR_XSD_FILE);
        Path parent = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.PARENT_DESCRIPTOR);
        Path child = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.CHILD_DESCRIPTOR);
        AppServerWebAppConfiguration parentConfig = Utils.
                getUnmarshalledObject(parent, xmlSchema, AppServerWebAppConfiguration.class);
        AppServerWebAppConfiguration childConfig = Utils.
                getUnmarshalledObject(child, xmlSchema, AppServerWebAppConfiguration.class);
        parentConfig.merge(childConfig);
        Assert.assertTrue(compare(parentConfig, prepareDefault()));
    }

    private static AppServerWebAppConfiguration prepareDefault() {
        AppServerWebAppConfiguration configuration = new AppServerWebAppConfiguration();
        configuration.setClassLoaderConfiguration(prepareClassLoaderConfiguration());
        configuration.setSingleSignOnConfiguration(prepareSSOConfiguration());
        return configuration;
    }

    private static ClassLoaderConfiguration prepareClassLoaderConfiguration() {
        ClassLoaderConfiguration classloading = new ClassLoaderConfiguration();
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
        ssoConfiguration.setRequestURLPostfix(TestConstants.REQUEST_URL_POSTFIX);
        ssoConfiguration.setHttpBinding(TestConstants.SAML_BINDING);
        ssoConfiguration.setIssuerId(TestConstants.ISSUER_ID);
        ssoConfiguration.setConsumerURL(TestConstants.CONSUMER_URL);
        ssoConfiguration.setConsumerURLPostfix(TestConstants.CONSUMER_URL_POSTFIX);
        ssoConfiguration.setAttributeConsumingServiceIndex(TestConstants.ATTR_CONSUMER_SERVICE_INDEX);
        ssoConfiguration.enableSLO(true);
        ssoConfiguration.setSLOURLPostfix(TestConstants.SLO_URL_POSTFIX);
        ssoConfiguration.enableAssertionSigning(true);
        ssoConfiguration.enableAssertionEncryption(true);
        ssoConfiguration.enableRequestSigning(true);
        ssoConfiguration.enableResponseSigning(true);
        ssoConfiguration.enableForceAuthn(false);
        ssoConfiguration.enablePassiveAuthn(false);

        SSOConfiguration.Property relayState = new SSOConfiguration.Property();
        relayState.setKey(TestConstants.RELAY_STATE_KEY);
        relayState.setValue(TestConstants.RELAY_STATE_VALUE);
        SSOConfiguration.Property loginURL = new SSOConfiguration.Property();
        loginURL.setKey(TestConstants.LOGIN_URL_KEY);
        loginURL.setValue(TestConstants.LOGIN_URL_VALUE);
        SSOConfiguration.Property tenantId = new SSOConfiguration.Property();
        tenantId.setKey(TestConstants.TENANT_ID_KEY);
        tenantId.setValue(TestConstants.TENANT_ID_VALUE);
        List<SSOConfiguration.Property> properties = new ArrayList<>();
        properties.add(relayState);
        properties.add(loginURL);
        properties.add(tenantId);
        ssoConfiguration.setProperties(properties);

        return ssoConfiguration;
    }

    private static boolean compare(AppServerWebAppConfiguration actual, AppServerWebAppConfiguration expected) {
        return ((compareClassloadingConfigs(actual.getClassLoaderConfiguration(),
                expected.getClassLoaderConfiguration())) && (compareSSOConfigurations(
                actual.getSingleSignOnConfiguration(), expected.getSingleSignOnConfiguration())));
    }

    private static boolean compareClassloadingConfigs(ClassLoaderConfiguration actual,
            ClassLoaderConfiguration expected) {
        return ((actual != null) && (expected != null)
                && (actual.getEnvironments().trim().equals(expected.getEnvironments())));
    }

    private static boolean compareSSOConfigurations(SSOConfiguration actual, SSOConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            boolean skipURIs = compareSkipURIs(actual.getSkipURIs(), expected.getSkipURIs());
            boolean handlingConsumerURLAfterSLO = (actual.handleConsumerURLAfterSLO() == expected.
                    handleConsumerURLAfterSLO());
            boolean queryParams = actual.getQueryParams().trim().equals(expected.getQueryParams());
            boolean appServerURL = actual.getApplicationServerURL().trim().equals(expected.getApplicationServerURL());
            boolean enableSSO = (actual.isSSOEnabled() == expected.isSSOEnabled());
            boolean binding = actual.getHttpBinding().trim().equals(expected.getHttpBinding());
            boolean issuerID = actual.getIssuerId().trim().equals(expected.getIssuerId());
            boolean consumerURL = actual.getConsumerURL().trim().equals(expected.getConsumerURL());
            boolean serviceIndex = actual.getAttributeConsumingServiceIndex().trim().
                    equals(expected.getAttributeConsumingServiceIndex());
            boolean enableSLO = (actual.isSLOEnabled() == expected.isSLOEnabled());
            boolean ssl = compareSSLProperties(actual, expected);
            boolean forceAuthn = (actual.isForceAuthnEnabled() == expected.isForceAuthnEnabled());
            boolean passiveAuthn = (actual.isPassiveAuthnEnabled() == expected.isPassiveAuthnEnabled());
            boolean postfixes = comparePostfixes(actual, expected);
            boolean properties = compareProperties(actual.getProperties(), expected.getProperties());

            return (skipURIs && handlingConsumerURLAfterSLO && queryParams && appServerURL && enableSSO && postfixes
                    && binding && issuerID && consumerURL && serviceIndex && enableSLO && ssl && forceAuthn
                    && passiveAuthn && properties);
        } else {
            return false;
        }
    }

    private static boolean compareSkipURIs(SSOConfiguration.SkipURIs actual, SSOConfiguration.SkipURIs expected) {
        return actual.getSkipURIs().stream().filter(skipURI -> expected.getSkipURIs().stream().
                filter(uri -> uri.trim().equals(skipURI)).count() > 0).count() == expected.getSkipURIs().size();
    }

    private static boolean compareProperties(List<SSOConfiguration.Property> actual,
            List<SSOConfiguration.Property> expected) {
        return actual.stream().filter(property -> expected.stream().
                filter(exp -> (property.getKey().trim().equals(exp.getKey()) && property.getValue().trim().
                                equals(exp.getValue()))).count() > 0).count() == expected.size();
    }

    private static boolean compareSSLProperties(SSOConfiguration actual, SSOConfiguration expected) {
        boolean assertionSigning = (actual.isAssertionSigningEnabled() == expected.isAssertionSigningEnabled());
        boolean assertionEncryption = (actual.isAssertionEncryptionEnabled() == expected.
                isAssertionEncryptionEnabled());
        boolean requestSigning = (actual.isRequestSigningEnabled() == expected.isRequestSigningEnabled());
        boolean responseSigning = (actual.isResponseSigningEnabled() == expected.isResponseSigningEnabled());

        return assertionSigning && assertionEncryption && requestSigning && responseSigning;
    }

    private static boolean comparePostfixes(SSOConfiguration actual, SSOConfiguration expected) {
        boolean requestURLPostfix = actual.getRequestURLPostfix().trim().equals(expected.getRequestURLPostfix());
        boolean consumerURLPostfix = actual.getConsumerURLPostfix().trim().equals(expected.getConsumerURLPostfix());
        boolean sloURLPostfix = actual.getSLOURLPostfix().trim().equals(expected.getSLOURLPostfix());

        return requestURLPostfix && consumerURLPostfix && sloURLPostfix;
    }
}
