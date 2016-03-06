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
import org.wso2.appserver.configuration.server.ClassLoaderEnvironments;
import org.wso2.appserver.configuration.server.SSOConfiguration;
import org.wso2.appserver.configuration.server.SecurityConfiguration;
import org.wso2.appserver.configuration.server.ServerConfiguration;
import org.wso2.appserver.configuration.server.StatsPublisherConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerException;
import org.wso2.appserver.utils.XMLUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines unit-tests for XML utilities.
 *
 * @since 6.0.0
 */
public class XMLUtilTest {
    private static final Logger logger = Logger.getLogger(XMLUtilTest.class.getName());

    @Test
    public void loadObjectFromFilePathTest() {
        Path xmlSource = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.SAMPLE_XML_FILE);
        Path xmlSchema = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.SAMPLE_XSD_FILE);
        try {
            ServerConfiguration actual = XMLUtils.
                    getUnmarshalledObject(xmlSource, xmlSchema, ServerConfiguration.class);
            ServerConfiguration expected = generateDefault();
            Assert.assertTrue(compare(actual, expected));
        } catch (ApplicationServerException e) {
            logger.log(Level.INFO, "Error when unmarshalling the XML source", e);
            Assert.fail();
        }
    }

    @Test
    public void loadObjectFromFileInputStreamTest() {
        Path xmlSource = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.SAMPLE_XML_FILE);
        Path xmlSchema = Paths.
                get(TestConstants.BUILD_DIRECTORY, TestConstants.TEST_RESOURCE_FOLDER, TestConstants.SAMPLE_XSD_FILE);
        try {
            ServerConfiguration actual = XMLUtils.
                    getUnmarshalledObject(Files.newInputStream(xmlSource), xmlSchema, ServerConfiguration.class);
            ServerConfiguration expected = generateDefault();
            Assert.assertTrue(compare(actual, expected));
        } catch (ApplicationServerException | IOException e) {
            logger.log(Level.INFO, "Error when unmarshalling the XML source", e);
            Assert.fail();
        }
    }

    protected static ServerConfiguration generateDefault() {
        ServerConfiguration serverConfiguration = new ServerConfiguration();
        serverConfiguration.setClassLoaderEnvironments(prepareClassLoaderEnv());
        serverConfiguration.setSingleSignOnConfiguration(prepareSSOConfigs());
        serverConfiguration.setStatsPublisherConfiguration(prepareStatsPublishingConfigs());
        serverConfiguration.setSecurityConfiguration(prepareSecurityConfigs());

        return serverConfiguration;
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
        keystore.setKeystorePath(TestConstants.KEYSTORE_PATH);
        keystore.setKeystorePassword(TestConstants.KEYSTORE_PASSWORD);
        keystore.setIdpCertificateAlias(TestConstants.IDP_CERT_ALIAS);
        keystore.setPrivateKeyAlias(TestConstants.PRIVATE_KEY_ALIAS);
        keystore.setPrivateKeyPassword(TestConstants.PRIVATE_KEY_PASSWORD);

        SecurityConfiguration.Truststore truststore = new SecurityConfiguration.Truststore();
        truststore.setTruststorePath(TestConstants.TRUSTSTORE_PATH);
        truststore.setTrustStorePassword(TestConstants.TRUSTSTORE_PASSWORD);

        configuration.setKeystore(keystore);
        configuration.setTruststore(truststore);

        return configuration;
    }

    protected static boolean compare(ServerConfiguration actual, ServerConfiguration expected) {
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

    private static boolean compareSecurityConfigurations(SecurityConfiguration actual, SecurityConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            boolean keystorePath, keystorePassword, certAlias, privateKeyAlias, privateKeyPassword;
            keystorePath = keystorePassword = certAlias = privateKeyAlias = privateKeyPassword = false;
            if ((actual.getKeystore() != null) && (expected.getKeystore() != null)) {
                keystorePath = actual.getKeystore().getKeystorePath().trim().
                        equals(expected.getKeystore().getKeystorePath());
                keystorePassword = actual.getKeystore().getKeystorePassword().trim().
                        equals(expected.getKeystore().getKeystorePassword());
                certAlias = actual.getKeystore().getIdpCertificateAlias().trim().
                        equals(expected.getKeystore().getIdpCertificateAlias());
                privateKeyAlias = actual.getKeystore().getPrivateKeyAlias().trim().
                        equals(expected.getKeystore().getPrivateKeyAlias());
                privateKeyPassword = actual.getKeystore().getPrivateKeyPassword().trim().
                        equals(expected.getKeystore().getPrivateKeyPassword());
            }

            boolean truststorePath, truststorePassword;
            truststorePath = truststorePassword = false;
            if ((actual.getTruststore() != null) && (expected.getTruststore() != null)) {
                truststorePath = actual.getTruststore().getTruststorePath().trim().
                        equals(expected.getTruststore().getTruststorePath());
                truststorePassword = actual.getTruststore().getTrustStorePassword().trim().
                        equals(expected.getTruststore().getTrustStorePassword());
            }

            return (keystorePath && keystorePassword && certAlias && privateKeyAlias && privateKeyPassword &&
                    truststorePath && truststorePassword);
        } else {
            return false;
        }
    }

    private static boolean compareSSOConfigurations(SSOConfiguration actual, SSOConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            boolean idpURL = actual.getIdpURL().trim().equals(expected.getIdpURL());
            boolean idpEntityID = actual.getIdpEntityId().trim().equals(expected.getIdpEntityId());
            boolean validatorClass = actual.getSignatureValidatorImplClass().trim().
                    equals(expected.getSignatureValidatorImplClass());
            return (idpURL && idpEntityID && validatorClass);
        } else {
            return false;
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
            return false;
        }
    }

    private static boolean compareClassloadingConfigurations(ClassLoaderEnvironments actual,
            ClassLoaderEnvironments expected) {
        return (actual != null) && (expected != null) && actual.getEnvironments().getEnvironments().stream().
                filter(env -> expected.getEnvironments().getEnvironments().stream().
                        filter(expectedEnv -> (expectedEnv.getName().equals(env.getName().trim()) && expectedEnv
                                .getClasspath().equals(env.getClasspath().trim()))).count() > 0).count() == expected
                .getEnvironments().getEnvironments().size();
    }
}
