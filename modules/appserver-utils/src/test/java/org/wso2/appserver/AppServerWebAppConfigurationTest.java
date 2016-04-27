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

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.configuration.context.WebAppClassLoading;
import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.configuration.context.WebAppStatsPublishing;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;
import org.wso2.appserver.exceptions.ApplicationServerRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class defines unit-tests for context level configurations.
 *
 * @since 6.0.0
 */
public class AppServerWebAppConfigurationTest {
    private static final Path catalina_base = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.CATALINA_BASE);
    private static final Path config_base_webapp_descriptor = Paths.
            get(catalina_base.toString(), Constants.TOMCAT_CONFIGURATION_DIRECTORY,
                    Constants.APP_SERVER_CONFIGURATION_DIRECTORY, Constants.WEBAPP_DESCRIPTOR);
    private static final ContextConfigurationLoader context_configuration_loader = new ContextConfigurationLoader();
    private static final Host host = new StandardHost();
    private static final Context sample_context = new StandardContext();
    private static final Context faulty_sample_context = new StandardContext();

    @BeforeClass
    public void setupCatalinaBaseEnv() throws IOException {
        System.setProperty(Globals.CATALINA_BASE_PROP, catalina_base.toString());
        prepareCatalinaComponents();
    }

    @Test(description = "Attempts to load XML file content of a non-existent webapp descriptor",
            expectedExceptions = { ApplicationServerRuntimeException.class }, priority = 1)
    public void testObjectLoadingFromNonExistentDescriptor() {
        List<Lifecycle> components = new ArrayList<>();
        components.add(host);
        components.add(sample_context);
        components.stream().forEach(component -> {
            context_configuration_loader.
                    lifecycleEvent(new LifecycleEvent(component, Lifecycle.BEFORE_START_EVENT, null));
            context_configuration_loader.
                    lifecycleEvent(new LifecycleEvent(component, Lifecycle.CONFIGURE_START_EVENT, null));
        });
    }

    @Test(description = "Loads the XML file content of a WSO2 App Server specific webapp descriptor", priority = 2)
    public void testObjectLoadingFromDescriptor() throws IOException, ApplicationServerConfigurationException {
        Path source = Paths.get(TestConstants.TEST_RESOURCES, Constants.WEBAPP_DESCRIPTOR);
        Files.copy(source, config_base_webapp_descriptor);

        context_configuration_loader.
                lifecycleEvent(new LifecycleEvent(sample_context, Lifecycle.BEFORE_START_EVENT, null));

        Optional<AppServerWebAppConfiguration> effective = ContextConfigurationLoader.
                getContextConfiguration(sample_context);
        if (effective.isPresent()) {
            Assert.assertTrue(compare(effective.get(), prepareDefault()));
        } else {
            Assert.fail();
        }
    }

    @Test(description = "Loads the XML file content of an erroneous WSO2 App Server specific webapp descriptor",
            expectedExceptions = { ApplicationServerRuntimeException.class }, priority = 3)
    public void testObjectLoadingFromFaultyDescriptor() {
        context_configuration_loader.
                lifecycleEvent(new LifecycleEvent(faulty_sample_context, Lifecycle.BEFORE_START_EVENT, null));
    }

    @Test(description = "Checks the removal of per webapp configurations at Lifecycle.AFTER_STOP_EVENT", priority = 4)
    public void testWebAppConfigurationUnloading() {
        context_configuration_loader.
                lifecycleEvent(new LifecycleEvent(sample_context, Lifecycle.AFTER_STOP_EVENT, null));
        Optional<AppServerWebAppConfiguration> configuration = ContextConfigurationLoader.
                getContextConfiguration(sample_context);
        Assert.assertFalse(configuration.isPresent());
    }

    @AfterClass
    public void destroy() throws IOException {
        Files.delete(config_base_webapp_descriptor);
    }

    private static void prepareCatalinaComponents() {
        host.setAppBase(TestConstants.WEBAPP_BASE);
        sample_context.setParent(host);
        sample_context.setDocBase(TestConstants.SAMPLE_WEBAPP);
        faulty_sample_context.setParent(host);
        faulty_sample_context.setDocBase(TestConstants.FAULTY_SAMPLE_WEBAPP);
    }

    private static AppServerWebAppConfiguration prepareDefault() {
        AppServerWebAppConfiguration configuration = new AppServerWebAppConfiguration();
        configuration.setClassLoaderConfiguration(prepareClassLoaderConfiguration());
        configuration.setSingleSignOnConfiguration(prepareSSOConfiguration());
        configuration.setStatsPublisherConfiguration(prepareStatsPublisherConfiguration());

        return configuration;
    }

    private static WebAppClassLoading prepareClassLoaderConfiguration() {
        WebAppClassLoading classloading = new WebAppClassLoading();
        classloading.setEnvironments(TestConstants.JAXRS_ENV_NAME);
        return classloading;
    }

    private static WebAppSingleSignOn prepareSSOConfiguration() {
        WebAppSingleSignOn ssoConfiguration = new WebAppSingleSignOn();

        WebAppSingleSignOn.SkipURIs skipURIs = new WebAppSingleSignOn.SkipURIs();
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

        WebAppSingleSignOn.Property relayState = new WebAppSingleSignOn.Property();
        relayState.setKey(TestConstants.RELAY_STATE_KEY);
        relayState.setValue(TestConstants.RELAY_STATE_VALUE);
        WebAppSingleSignOn.Property loginURL = new WebAppSingleSignOn.Property();
        loginURL.setKey(TestConstants.LOGIN_URL_KEY);
        loginURL.setValue(TestConstants.LOGIN_URL_VALUE);
        WebAppSingleSignOn.Property tenantId = new WebAppSingleSignOn.Property();
        tenantId.setKey(TestConstants.TENANT_ID_KEY);
        tenantId.setValue(TestConstants.TENANT_ID_VALUE);
        List<WebAppSingleSignOn.Property> properties = new ArrayList<>();
        properties.add(relayState);
        properties.add(loginURL);
        properties.add(tenantId);
        ssoConfiguration.setProperties(properties);

        return ssoConfiguration;
    }

    private static WebAppStatsPublishing prepareStatsPublisherConfiguration() {
        WebAppStatsPublishing configuration = new WebAppStatsPublishing();
        configuration.enableStatsPublisher(true);
        return configuration;
    }

    private static boolean compare(AppServerWebAppConfiguration actual, AppServerWebAppConfiguration expected) {
        return ((compareClassloadingConfigs(actual.getClassLoaderConfiguration(),
                expected.getClassLoaderConfiguration())) && (compareSSOConfigurations(
                actual.getSingleSignOnConfiguration(), expected.getSingleSignOnConfiguration()))
                && (compareStatsPublisherConfigs(actual.getStatsPublisherConfiguration(),
                expected.getStatsPublisherConfiguration())));
    }

    private static boolean compareClassloadingConfigs(WebAppClassLoading actual,
            WebAppClassLoading expected) {
        return ((actual != null) && (expected != null) && (actual.getEnvironments().trim().
                equals(expected.getEnvironments())));
    }

    private static boolean compareSSOConfigurations(WebAppSingleSignOn actual, WebAppSingleSignOn expected) {
        if ((actual != null) && (expected != null)) {
            boolean skipURIs = compareSkipURIs(actual.getSkipURIs(), expected.getSkipURIs());
            boolean handlingConsumerURLAfterSLO = actual.handleConsumerURLAfterSLO().equals(expected.
                    handleConsumerURLAfterSLO());
            boolean queryParams = actual.getQueryParams().trim().equals(expected.getQueryParams());
            boolean appServerURL = actual.getApplicationServerURL().trim().equals(expected.getApplicationServerURL());
            boolean enableSSO = actual.isSSOEnabled().equals(expected.isSSOEnabled());
            boolean binding = actual.getHttpBinding().trim().equals(expected.getHttpBinding());
            boolean issuerID = actual.getIssuerId().trim().equals(expected.getIssuerId());
            boolean consumerURL = actual.getConsumerURL().trim().equals(expected.getConsumerURL());
            boolean serviceIndex = actual.getAttributeConsumingServiceIndex().trim().
                    equals(expected.getAttributeConsumingServiceIndex());
            boolean enableSLO = actual.isSLOEnabled().equals(expected.isSLOEnabled());
            boolean ssl = compareSSLProperties(actual, expected);
            boolean forceAuthn = actual.isForceAuthnEnabled().equals(expected.isForceAuthnEnabled());
            boolean passiveAuthn = actual.isPassiveAuthnEnabled().equals(expected.isPassiveAuthnEnabled());
            boolean postfixes = comparePostfixes(actual, expected);
            boolean properties = compareProperties(actual.getProperties(), expected.getProperties());

            return (skipURIs && handlingConsumerURLAfterSLO && queryParams && appServerURL && enableSSO && postfixes
                    && binding && issuerID && consumerURL && serviceIndex && enableSLO && ssl && forceAuthn
                    && passiveAuthn && properties);
        } else {
            return ((actual == null) && (expected == null));
        }
    }

    private static boolean compareSkipURIs(WebAppSingleSignOn.SkipURIs actual, WebAppSingleSignOn.SkipURIs expected) {
        return actual.getSkipURIs().stream().filter(skipURI -> expected.getSkipURIs().stream().
                filter(uri -> uri.trim().equals(skipURI)).count() > 0).count() == expected.getSkipURIs().size();
    }

    private static boolean compareProperties(List<WebAppSingleSignOn.Property> actual,
            List<WebAppSingleSignOn.Property> expected) {
        return actual.stream().filter(property -> expected.stream().
                filter(exp -> (property.getKey().trim().equals(exp.getKey()) && property.getValue().trim().
                        equals(exp.getValue()))).count() > 0).count() == expected.size();
    }

    private static boolean compareSSLProperties(WebAppSingleSignOn actual, WebAppSingleSignOn expected) {
        boolean assertionSigning = actual.isAssertionSigningEnabled().equals(expected.isAssertionSigningEnabled());
        boolean assertionEncryption = actual.isAssertionEncryptionEnabled().equals(expected.
                isAssertionEncryptionEnabled());
        boolean requestSigning = actual.isRequestSigningEnabled().equals(expected.isRequestSigningEnabled());
        boolean responseSigning = actual.isResponseSigningEnabled().equals(expected.isResponseSigningEnabled());

        return assertionSigning && assertionEncryption && requestSigning && responseSigning;
    }

    private static boolean comparePostfixes(WebAppSingleSignOn actual, WebAppSingleSignOn expected) {
        boolean requestURLPostfix = actual.getRequestURLPostfix().trim().equals(expected.getRequestURLPostfix());
        boolean consumerURLPostfix = actual.getConsumerURLPostfix().trim().equals(expected.getConsumerURLPostfix());
        boolean sloURLPostfix = actual.getSLOURLPostfix().trim().equals(expected.getSLOURLPostfix());

        return requestURLPostfix && consumerURLPostfix && sloURLPostfix;
    }

    private static boolean compareStatsPublisherConfigs(WebAppStatsPublishing actual,
            WebAppStatsPublishing expected) {
        return ((actual != null) && (expected != null) && (actual.isStatsPublisherEnabled().
                equals(expected.isStatsPublisherEnabled())));
    }
}
