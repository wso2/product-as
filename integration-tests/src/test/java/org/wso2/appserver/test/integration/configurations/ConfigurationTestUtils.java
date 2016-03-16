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
package org.wso2.appserver.test.integration.configurations;

import org.wso2.appserver.Constants;
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.configuration.context.ClassLoaderConfiguration;
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
import java.nio.file.Paths;
import java.util.List;

/**
 * This class defines utility functions for appserver-utils integration tests.
 *
 * @since 6.0.0
 */
public class ConfigurationTestUtils {
    private static final String BUILD_DIRECTORY = "target";
    private static final String DESCRIPTOR_DIRECTORY = "descriptors";

    protected static AppServerConfiguration loadDefaultServerConfiguration()
            throws IOException, ApplicationServerConfigurationException {
        Path defaultServerDescriptor = Paths.
                get(BUILD_DIRECTORY, DESCRIPTOR_DIRECTORY, Constants.APP_SERVER_DESCRIPTOR);
        Path defaultServerDescriptorSchema = Paths.
                get(BUILD_DIRECTORY, DESCRIPTOR_DIRECTORY, Constants.APP_SERVER_DESCRIPTOR_SCHEMA);
        return Utils.getUnmarshalledObject(Files.newInputStream(defaultServerDescriptor), defaultServerDescriptorSchema,
                AppServerConfiguration.class);
    }

    protected static AppServerWebAppConfiguration loadDefaultGlobalContextConfiguration()
            throws IOException, ApplicationServerConfigurationException {
        Path defaultDescriptor = Paths.get(BUILD_DIRECTORY, DESCRIPTOR_DIRECTORY, Constants.WEBAPP_DESCRIPTOR);
        Path defaultDescriptorSchema = Paths.
                get(BUILD_DIRECTORY, DESCRIPTOR_DIRECTORY, Constants.WEBAPP_DESCRIPTOR_SCHEMA);
        return Utils.
                getUnmarshalledObject(defaultDescriptor, defaultDescriptorSchema, AppServerWebAppConfiguration.class);
    }

    protected static boolean compareServerDescriptors(AppServerConfiguration actual, AppServerConfiguration expected) {
        boolean classloading = compareServerClassloading(actual.getClassLoaderEnvironments(),
                expected.getClassLoaderEnvironments());
        boolean sso = compareServerSSO(actual.getSingleSignOnConfiguration(), expected.getSingleSignOnConfiguration());
        boolean statsPublishing = compareServerStatsPublishing(actual.getStatsPublisherConfiguration(),
                expected.getStatsPublisherConfiguration());
        boolean security = compareServerSecurity(actual.getSecurityConfiguration(),
                expected.getSecurityConfiguration());

        return (classloading && sso && statsPublishing && security);
    }

    private static boolean compareServerClassloading(ClassLoaderEnvironments actual, ClassLoaderEnvironments expected) {
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

    private static boolean compareServerSSO(SSOConfiguration actual, SSOConfiguration expected) {
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

    private static boolean compareServerStatsPublishing(StatsPublisherConfiguration actual,
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

    private static boolean compareServerSecurity(SecurityConfiguration actual, SecurityConfiguration expected) {
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

    protected static boolean compareWebAppDescriptors(AppServerWebAppConfiguration actual,
            AppServerWebAppConfiguration expected) {
        return ((compareWebAppClassloading(actual.getClassLoaderConfiguration(),
                expected.getClassLoaderConfiguration())) && (compareWebAppSSO(actual.getSingleSignOnConfiguration(),
                expected.getSingleSignOnConfiguration())));
    }

    private static boolean compareWebAppClassloading(ClassLoaderConfiguration actual,
            ClassLoaderConfiguration expected) {
        if ((actual != null) && (expected != null)) {
            return actual.getEnvironments().trim().equals(expected.getEnvironments());
        } else {
            return ((actual == null) && (expected == null));
        }
    }

    private static boolean compareWebAppSSO(org.wso2.appserver.configuration.context.SSOConfiguration actual,
            org.wso2.appserver.configuration.context.SSOConfiguration expected) {
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
            return ((actual == null) && (expected == null));
        }
    }

    private static boolean compareSkipURIs(org.wso2.appserver.configuration.context.SSOConfiguration.SkipURIs actual,
            org.wso2.appserver.configuration.context.SSOConfiguration.SkipURIs expected) {
        return actual.getSkipURIs().stream().filter(skipURI -> expected.getSkipURIs().stream().
                filter(uri -> uri.trim().equals(skipURI)).count() > 0).count() == expected.getSkipURIs().size();
    }

    private static boolean compareProperties(
            List<org.wso2.appserver.configuration.context.SSOConfiguration.Property> actual,
            List<org.wso2.appserver.configuration.context.SSOConfiguration.Property> expected) {
        return actual.stream().filter(property -> expected.stream().
                filter(exp -> (property.getKey().trim().equals(exp.getKey()) && property.getValue().trim().
                        equals(exp.getValue()))).count() > 0).count() == expected.size();
    }

    private static boolean compareSSLProperties(org.wso2.appserver.configuration.context.SSOConfiguration actual,
            org.wso2.appserver.configuration.context.SSOConfiguration expected) {
        boolean assertionSigning = (actual.isAssertionSigningEnabled() == expected.isAssertionSigningEnabled());
        boolean assertionEncryption = (actual.isAssertionEncryptionEnabled() == expected.
                isAssertionEncryptionEnabled());
        boolean requestSigning = (actual.isRequestSigningEnabled() == expected.isRequestSigningEnabled());
        boolean responseSigning = (actual.isResponseSigningEnabled() == expected.isResponseSigningEnabled());

        return assertionSigning && assertionEncryption && requestSigning && responseSigning;
    }

    private static boolean comparePostfixes(org.wso2.appserver.configuration.context.SSOConfiguration actual,
            org.wso2.appserver.configuration.context.SSOConfiguration expected) {
        boolean requestURLPostfix = actual.getRequestURLPostfix().trim().equals(expected.getRequestURLPostfix());
        boolean consumerURLPostfix = actual.getConsumerURLPostfix().trim().equals(expected.getConsumerURLPostfix());
        boolean sloURLPostfix = actual.getSLOURLPostfix().trim().equals(expected.getSLOURLPostfix());

        return requestURLPostfix && consumerURLPostfix && sloURLPostfix;
    }
}
