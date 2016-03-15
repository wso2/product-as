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
import org.wso2.appserver.configuration.listeners.Utils;
import org.wso2.appserver.configuration.server.AppServerConfiguration;
import org.wso2.appserver.configuration.server.ClassLoaderEnvironments;
import org.wso2.appserver.configuration.server.SSOConfiguration;
import org.wso2.appserver.configuration.server.SecurityConfiguration;
import org.wso2.appserver.configuration.server.StatsPublisherConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines unit-tests for server level configurations.
 *
 * @since 6.0.0
 */
public class AppServerConfigurationTest {
    @Test(description = "Loads the XML file content of the WSO2 App Server specific server level configuration "
            + "descriptor")
    public void testObjectLoadingFromFilePath() throws IOException, ApplicationServerConfigurationException {
        Path xmlSource = TestUtils.
                getResourceFile(TestConstants.TEST_RESOURCE_SUB_FOLDER + "/" + TestConstants.SAMPLE_XML_FILE);
        Path xmlSchema = TestUtils.
                getResourceFile(TestConstants.TEST_RESOURCE_SUB_FOLDER + "/" + TestConstants.SAMPLE_XSD_FILE);
        AppServerConfiguration actual = Utils.
                getUnmarshalledObject(Files.newInputStream(xmlSource), xmlSchema, AppServerConfiguration.class);
        AppServerConfiguration expected = generateDefault();
        Assert.assertTrue(compare(actual, expected));
    }

    protected static AppServerConfiguration generateDefault() {
        AppServerConfiguration appServerConfiguration = new AppServerConfiguration();
        appServerConfiguration.setClassLoaderEnvironments(prepareClassLoaderEnv());
        appServerConfiguration.setSingleSignOnConfiguration(prepareSSOConfigs());
        appServerConfiguration.setStatsPublisherConfiguration(prepareStatsPublishingConfigs());
        appServerConfiguration.setSecurityConfiguration(prepareSecurityConfigs());

        return appServerConfiguration;
    }

    private static ClassLoaderEnvironments prepareClassLoaderEnv() {
        ClassLoaderEnvironments classloadingEnvironments = new ClassLoaderEnvironments();

        ClassLoaderEnvironments.Environment cxf = new ClassLoaderEnvironments.Environment();
        cxf.setName(TestConstants.CXF_ENV_NAME);
        cxf.setClasspath(TestConstants.CXF_ENV_CLASSPATH);
        ClassLoaderEnvironments.Environment jaxrs = new ClassLoaderEnvironments.Environment();
        jaxrs.setName(TestConstants.JAXRS_ENV_NAME);
        jaxrs.setClasspath(TestConstants.JAXRS_ENV_CLASSPATH);

        List<ClassLoaderEnvironments.Environment> envList = new ArrayList<>();
        envList.add(cxf);
        envList.add(jaxrs);

        ClassLoaderEnvironments.Environments environments = new ClassLoaderEnvironments.Environments();
        environments.setEnvironments(envList);
        classloadingEnvironments.setEnvironments(environments);

        return classloadingEnvironments;
    }

    private static SSOConfiguration prepareSSOConfigs() {
        SSOConfiguration ssoConfiguration = new SSOConfiguration();

        ssoConfiguration.setIdpURL(TestConstants.IDP_URL);
        ssoConfiguration.setIdpEntityId(TestConstants.IDP_ENTITY_ID);
        ssoConfiguration.setSignatureValidatorImplClass(TestConstants.VALIDATOR_CLASS);
        ssoConfiguration.setIdpCertificateAlias(TestConstants.IDP_CERT_ALIAS);

        SSOConfiguration.Property loginURL = new SSOConfiguration.Property();
        loginURL.setKey(TestConstants.LOGIN_URL_KEY);
        loginURL.setValue(TestConstants.LOGIN_URL_VALUE);
        SSOConfiguration.Property relayState = new SSOConfiguration.Property();
        relayState.setKey(TestConstants.RELAY_STATE_KEY);
        relayState.setValue(TestConstants.RELAY_STATE_VALUE);
        List<SSOConfiguration.Property> properties = new ArrayList<>();
        properties.add(loginURL);
        properties.add(relayState);
        ssoConfiguration.setProperties(properties);

        return ssoConfiguration;
    }

    private static StatsPublisherConfiguration prepareStatsPublishingConfigs() {
        StatsPublisherConfiguration configuration = new StatsPublisherConfiguration();

        configuration.setUsername(TestConstants.USERNAME);
        configuration.setPassword(TestConstants.PASSWORD);
        configuration.setAuthenticationURL(TestConstants.AUTHN_URL);
        configuration.setPublisherURL(TestConstants.PUBLISHER_URL);
        configuration.setStreamId(TestConstants.STREAM_ID);

        return configuration;
    }

    private static SecurityConfiguration prepareSecurityConfigs() {
        SecurityConfiguration configuration = new SecurityConfiguration();

        SecurityConfiguration.Keystore keystore = new SecurityConfiguration.Keystore();
        keystore.setLocation(TestConstants.KEYSTORE_PATH);
        keystore.setPassword(TestConstants.KEYSTORE_PASSWORD);
        keystore.setType(TestConstants.TYPE);
        keystore.setKeyAlias(TestConstants.PRIVATE_KEY_ALIAS);
        keystore.setKeyPassword(TestConstants.PRIVATE_KEY_PASSWORD);

        SecurityConfiguration.Truststore truststore = new SecurityConfiguration.Truststore();
        truststore.setLocation(TestConstants.TRUSTSTORE_PATH);
        truststore.setType(TestConstants.TYPE);
        truststore.setPassword(TestConstants.TRUSTSTORE_PASSWORD);

        configuration.setKeystore(keystore);
        configuration.setTruststore(truststore);

        return configuration;
    }

    protected static boolean compare(AppServerConfiguration actual, AppServerConfiguration expected) {
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

    private static boolean compareClassloadingConfigurations(ClassLoaderEnvironments actual,
            ClassLoaderEnvironments expected) {
        if ((actual != null) && (expected != null)) {
            return actual.getEnvironments().getEnvironments().stream().
                    filter(env -> expected.getEnvironments().getEnvironments().stream().
                            filter(expectedEnv -> (expectedEnv.getName().equals(env.getName().trim()) && expectedEnv.
                                    getClasspath().equals(env.getClasspath().trim()))).count() == 1).
                    count() == expected.getEnvironments().getEnvironments().size();
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareSSOConfigurations(SSOConfiguration actual, SSOConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            boolean idpURL = actual.getIdpURL().trim().equals(expected.getIdpURL());
            boolean idpEntityID = actual.getIdpEntityId().trim().equals(expected.getIdpEntityId());
            boolean validatorClass = actual.getSignatureValidatorImplClass().trim().
                    equals(expected.getSignatureValidatorImplClass());
            boolean idpCertAlias = actual.getIdpCertificateAlias().trim().equals(expected.getIdpCertificateAlias());
            boolean properties = compareSSOProperties(actual.getProperties(), expected.getProperties());
            return (idpURL && idpEntityID && validatorClass && idpCertAlias && properties);
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareSSOProperties(List<SSOConfiguration.Property> actual,
            List<SSOConfiguration.Property> expected) {
        if ((actual != null) && (expected != null)) {
            return actual.stream().filter(property -> expected.stream().
                    filter(expProperty -> ((expProperty.getKey().equals(property.getKey())) && (expProperty.getValue().
                            equals(property.getValue())))).count() > 0).count() == expected.size();
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareStatsPublishingConfigurations(StatsPublisherConfiguration actual,
            StatsPublisherConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            boolean username = actual.getUsername().trim().equals(expected.getUsername());
            boolean password = actual.getPassword().trim().equals(expected.getPassword());
            boolean authnURL = actual.getAuthenticationURL().trim().equals(expected.getAuthenticationURL());
            boolean publisherURL = actual.getPublisherURL().trim().equals(expected.getPublisherURL());
            boolean streamID = actual.getStreamId().trim().equals(expected.getStreamId());
            return (username && password && authnURL && publisherURL && streamID);
        } else {
            return (actual == null) && (expected == null);
        }
    }

    private static boolean compareSecurityConfigurations(SecurityConfiguration actual, SecurityConfiguration expected) {
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
}
