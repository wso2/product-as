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

import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.AppServerClassLoading;
import org.wso2.appserver.configuration.server.AppServerSecurity;
import org.wso2.appserver.configuration.server.AppServerSingleSignOn;
import org.wso2.appserver.configuration.server.AppServerStatsPublishing;
import org.wso2.appserver.configuration.server.ApplicationServerConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;
import org.wso2.appserver.exceptions.ApplicationServerRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines unit-tests for server level configurations.
 *
 * @since 6.0.0
 */
public class ApplicationServerConfigurationTest {
    private static final Path catalina_base = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.CATALINA_BASE);
    private static final Path dist_server_descriptor = Paths.get(
            catalina_base.toString(), Constants.TOMCAT_CONFIGURATION_DIRECTORY,
            Constants.APP_SERVER_CONFIGURATION_DIRECTORY, Constants.APP_SERVER_DESCRIPTOR);
    private static final StrSubstitutor string_sub = new StrSubstitutor(System.getenv());
    private static final List<Lifecycle> lifecycle_components = generateSampleTomcatComponents();
    private static final ServerConfigurationLoader loader = new ServerConfigurationLoader();

    @BeforeClass
    public void setupCatalinaBaseEnv() throws IOException {
        System.setProperty(Globals.CATALINA_BASE_PROP, catalina_base.toString());
    }

    @Test(description = "Attempts to load XML file content of a non-existent server descriptor",
            expectedExceptions = { ApplicationServerRuntimeException.class })
    public void testObjectLoadingFromNonExistentDescriptor() {
        lifecycle_components
                .stream()
                .forEach(component -> loader.lifecycleEvent(
                        new LifecycleEvent(component, Lifecycle.BEFORE_START_EVENT, null)));
    }

    @Test(description = "Tests loading the XML file content from an invalid server descriptor", expectedExceptions =
            {ApplicationServerRuntimeException.class}, priority = 1)
    public void testObjectLoadingFromInvalidDescriptor() throws IOException {
        Path source = Paths.get(TestConstants.TEST_RESOURCES, "faulty-wso2as.xml");
        Files.copy(source, dist_server_descriptor);

        lifecycle_components
                .stream()
                .forEach(component -> loader.lifecycleEvent(
                        new LifecycleEvent(component, Lifecycle.BEFORE_START_EVENT, null)));
    }

    @Test(description = "Loads the XML file content of a valid WSO2 App Server specific server level configuration " +
            "descriptor", priority = 2)
    public void testObjectLoadingFromDescriptor() throws IOException, ApplicationServerConfigurationException {
        //  deletes the existing server descriptor
        Files.delete(dist_server_descriptor);

        Path source = Paths.get(TestConstants.TEST_RESOURCES, Constants.APP_SERVER_DESCRIPTOR);
        Files.copy(source, dist_server_descriptor);

        lifecycle_components
                .stream()
                .forEach(component -> loader.lifecycleEvent(
                        new LifecycleEvent(component, Lifecycle.BEFORE_START_EVENT, null)));

        ApplicationServerConfiguration actual = ServerConfigurationLoader.getServerConfiguration();
        ApplicationServerConfiguration expected = generateDefault();
        Assert.assertTrue(compare(actual, expected));

        //  deletes the existing server descriptor
        Files.delete(dist_server_descriptor);
    }

    private static ApplicationServerConfiguration generateDefault() {
        ApplicationServerConfiguration appServerConfiguration = new ApplicationServerConfiguration();
        appServerConfiguration.setClassLoaderEnvironments(prepareClassLoaderEnv());
        appServerConfiguration.setSingleSignOnConfiguration(prepareSSOConfigs());
        appServerConfiguration.setStatsPublisherConfiguration(prepareStatsPublishingConfigs());
        appServerConfiguration.setSecurityConfiguration(prepareSecurityConfigs());

        return appServerConfiguration;
    }

    private static AppServerClassLoading prepareClassLoaderEnv() {
        AppServerClassLoading classloadingEnvironments = new AppServerClassLoading();

        AppServerClassLoading.Environment customEnv = new AppServerClassLoading.Environment();
        customEnv.setName(TestConstants.CUSTOM_ENV_NAME);
        customEnv.setClasspath(TestConstants.CUSTOM_ENV_CLASSPATH);
        AppServerClassLoading.Environment jaxrs = new AppServerClassLoading.Environment();
        jaxrs.setName(TestConstants.JAXRS_ENV_NAME);
        jaxrs.setClasspath(TestConstants.JAXRS_ENV_CLASSPATH);

        List<AppServerClassLoading.Environment> envList = new ArrayList<>();
        envList.add(customEnv);
        envList.add(jaxrs);

        envList
                .forEach(environment -> environment.setClasspath(string_sub.replace(environment.getClasspath())));
        envList
                .forEach(environment -> environment.
                        setClasspath(StrSubstitutor.replaceSystemProperties(environment.getClasspath())));

        AppServerClassLoading.Environments environments = new AppServerClassLoading.Environments();
        environments.setEnvironments(envList);
        classloadingEnvironments.setEnvironments(environments);

        return classloadingEnvironments;
    }

    private static AppServerSingleSignOn prepareSSOConfigs() {
        AppServerSingleSignOn ssoConfiguration = new AppServerSingleSignOn();

        ssoConfiguration.setIdpURL(TestConstants.IDP_URL);
        ssoConfiguration.setIdpEntityId(TestConstants.IDP_ENTITY_ID);
        ssoConfiguration.setSignatureValidatorImplClass(TestConstants.VALIDATOR_CLASS);
        ssoConfiguration.setIdpCertificateAlias(TestConstants.IDP_CERT_ALIAS);
        ssoConfiguration.setACSBase(TestConstants.APP_SERVER_URL);

        AppServerSingleSignOn.Property loginURL = new AppServerSingleSignOn.Property();
        loginURL.setKey(TestConstants.LOGIN_URL_KEY);
        loginURL.setValue(TestConstants.LOGIN_URL_VALUE);
        AppServerSingleSignOn.Property relayState = new AppServerSingleSignOn.Property();
        relayState.setKey(TestConstants.RELAY_STATE_KEY);
        relayState.setValue(TestConstants.RELAY_STATE_VALUE);
        List<AppServerSingleSignOn.Property> properties = new ArrayList<>();
        properties.add(loginURL);
        properties.add(relayState);
        ssoConfiguration.setProperties(properties);

        return ssoConfiguration;
    }

    private static AppServerStatsPublishing prepareStatsPublishingConfigs() {
        AppServerStatsPublishing configuration = new AppServerStatsPublishing();

        configuration.setUsername(TestConstants.USERNAME);
        configuration.setPassword(TestConstants.PASSWORD);
        configuration.setDataAgentType(TestConstants.DATA_AGENT_TYPE);
        configuration.setAuthenticationURL(TestConstants.AUTHN_URL);
        configuration.setPublisherURL(TestConstants.PUBLISHER_URL);
        configuration.setStreamId(TestConstants.STREAM_ID);

        return configuration;
    }

    private static AppServerSecurity prepareSecurityConfigs() {
        AppServerSecurity configuration = new AppServerSecurity();

        AppServerSecurity.Keystore keystore = new AppServerSecurity.Keystore();
        keystore.setLocation(TestConstants.KEYSTORE_PATH);
        keystore.setPassword(TestConstants.KEYSTORE_PASSWORD);
        keystore.setType(TestConstants.TYPE);
        keystore.setKeyAlias(TestConstants.PRIVATE_KEY_ALIAS);
        keystore.setKeyPassword(TestConstants.PRIVATE_KEY_PASSWORD);

        AppServerSecurity.Truststore truststore = new AppServerSecurity.Truststore();
        truststore.setLocation(TestConstants.TRUSTSTORE_PATH);
        truststore.setType(TestConstants.TYPE);
        truststore.setPassword(TestConstants.TRUSTSTORE_PASSWORD);

        configuration.setKeystore(keystore);
        configuration.setTruststore(truststore);

        configuration.getKeystore().setLocation(string_sub.replace(configuration.getKeystore().getLocation()));
        configuration.getTruststore().setLocation(string_sub.replace(configuration.getTruststore().getLocation()));
        configuration.getKeystore().
                setLocation(StrSubstitutor.replaceSystemProperties(configuration.getKeystore().getLocation()));
        configuration.getTruststore()
                .setLocation(StrSubstitutor.replaceSystemProperties(configuration.getTruststore().getLocation()));

        return configuration;
    }

    private static boolean compare(ApplicationServerConfiguration actual, ApplicationServerConfiguration expected) {
        boolean classloading = compareClassloadingConfigurations(actual.getClassLoaderEnvironments(),
                expected.getClassLoaderEnvironments());
        boolean sso = compareSSOConfigurations(actual.getSingleSignOnConfiguration(),
                expected.getSingleSignOnConfiguration());
        boolean statsPublishing = compareStatsPublishingConfigurations(actual.getStatsPublisherConfiguration(),
                expected.getStatsPublisherConfiguration());
        boolean security = compareSecurityConfigurations(actual.getSecurityConfiguration(),
                expected.getSecurityConfiguration());

        return (classloading && sso && statsPublishing && security);
    }

    private static boolean compareClassloadingConfigurations(AppServerClassLoading actual,
            AppServerClassLoading expected) {
        if ((actual != null) && (expected != null)) {
            return actual.getEnvironments().getEnvironments()
                    .stream()
                    .filter(env -> expected.getEnvironments().getEnvironments()
                            .stream()
                            .filter(expectedEnv -> (expectedEnv.getName().equals(env.getName().trim()) && expectedEnv.
                                    getClasspath().equals(env.getClasspath().trim())))
                            .count() == 1)
                    .count() == expected.getEnvironments().getEnvironments().size();
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareSSOConfigurations(AppServerSingleSignOn actual, AppServerSingleSignOn expected) {
        if ((actual != null) && (expected != null)) {
            boolean idpURL = actual.getIdpURL().trim().equals(expected.getIdpURL());
            boolean idpEntityID = actual.getIdpEntityId().trim().equals(expected.getIdpEntityId());
            boolean validatorClass = actual.getSignatureValidatorImplClass().trim().
                    equals(expected.getSignatureValidatorImplClass());
            boolean idpCertAlias = actual.getIdpCertificateAlias().trim().equals(expected.getIdpCertificateAlias());
            boolean acsBase = actual.getACSBase().trim().equals(expected.getACSBase());
            boolean properties = compareSSOProperties(actual.getProperties(), expected.getProperties());
            return (idpURL && idpEntityID && validatorClass && idpCertAlias && acsBase && properties);
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareSSOProperties(List<AppServerSingleSignOn.Property> actual,
            List<AppServerSingleSignOn.Property> expected) {
        if ((actual != null) && (expected != null)) {
            return actual
                    .stream()
                    .filter(property -> expected
                            .stream()
                            .filter(expProperty -> ((expProperty.getKey().equals(property.getKey())) && (expProperty
                                    .getValue().equals(property.getValue()))))
                            .count() > 0)
                    .count() == expected.size();
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareStatsPublishingConfigurations(AppServerStatsPublishing actual,
            AppServerStatsPublishing expected) {
        if ((actual != null) && (expected != null)) {
            boolean username = actual.getUsername().trim().equals(expected.getUsername());
            boolean password = actual.getPassword().trim().equals(expected.getPassword());
            boolean dataAgent = actual.getDataAgentType().trim().equals(expected.getDataAgentType());
            boolean authnURL = actual.getAuthenticationURL().trim().equals(expected.getAuthenticationURL());
            boolean publisherURL = actual.getPublisherURL().trim().equals(expected.getPublisherURL());
            boolean streamID = actual.getStreamId().trim().equals(expected.getStreamId());
            return (username && password && dataAgent && authnURL && publisherURL && streamID);
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareSecurityConfigurations(AppServerSecurity actual, AppServerSecurity expected) {
        if ((actual != null) && (expected != null)) {
            boolean keystorePath, keystorePassword, type, privateKeyAlias, privateKeyPassword;
            keystorePath = keystorePassword = type = privateKeyAlias = privateKeyPassword = false;
            if ((actual.getKeystore() != null) && (expected.getKeystore() != null)) {
                keystorePath = actual.getKeystore().getLocation().trim().equals(expected.getKeystore().getLocation());
                keystorePassword = actual.getKeystore().getPassword().trim().
                        equals(expected.getKeystore().getPassword());
                type = actual.getKeystore().getType().trim().equals(expected.getKeystore().getType());
                privateKeyAlias = actual.getKeystore().getKeyAlias().trim().
                        equals(expected.getKeystore().getKeyAlias());
                privateKeyPassword = actual.getKeystore().getKeyPassword().trim().
                        equals(expected.getKeystore().getKeyPassword());
            } else if ((actual.getKeystore() == null) && (expected.getKeystore() == null)) {
                keystorePath = true;
                keystorePassword = true;
                type = true;
                privateKeyAlias = true;
                privateKeyPassword = true;
            }

            boolean truststorePath, truststorePassword, truststoreType;
            truststorePath = truststorePassword = truststoreType = false;
            if ((actual.getTruststore() != null) && (expected.getTruststore() != null)) {
                truststorePath = actual.getTruststore().getLocation().trim().
                        equals(expected.getTruststore().getLocation());
                truststorePassword = actual.getTruststore().getPassword().trim().
                        equals(expected.getTruststore().getPassword());
                truststoreType = actual.getTruststore().getType().trim().equals(expected.getTruststore().getType());
            } else if ((actual.getTruststore() == null) && (expected.getTruststore() == null)) {
                truststorePath = true;
                truststorePassword = true;
                truststoreType = true;
            }

            return (keystorePath && keystorePassword && type && privateKeyAlias && privateKeyPassword &&
                    truststorePath && truststorePassword && truststoreType);
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static List<Lifecycle> generateSampleTomcatComponents() {
        List<Lifecycle> components = new ArrayList<>();
        components.add(new StandardHost());
        components.add(new StandardServer());

        return components;
    }
}
