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
package org.wso2.appserver.sample.utils;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.wso2.appserver.configuration.server.AppServerClassLoading;
import org.wso2.appserver.configuration.server.AppServerSecurity;
import org.wso2.appserver.configuration.server.AppServerSingleSignOn;
import org.wso2.appserver.configuration.server.AppServerStatsPublishing;
import org.wso2.appserver.configuration.server.ApplicationServerConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the utility methods for testing server level configuration loading.
 *
 * @since 6.0.0
 */
public class ServerConfigurationUtils {
    private static final StrSubstitutor STRING_SUB = new StrSubstitutor(System.getenv());

    public static ApplicationServerConfiguration generateDefault() {
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
        customEnv.setName(Constants.JAGGERY_ENV_NAME);
        customEnv.setClasspath(Constants.JAGGERY_ENV_CLASSPATH);

        List<AppServerClassLoading.Environment> envList = new ArrayList<>();
        envList.add(customEnv);

        envList
                .forEach(environment -> environment.setClasspath(STRING_SUB.replace(environment.getClasspath())));
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

        ssoConfiguration.setIdpURL(Constants.IDP_URL);
        ssoConfiguration.setIdpEntityId(Constants.IDP_ENTITY_ID);
        ssoConfiguration.setIdpCertificateAlias(Constants.IDP_CERT_ALIAS);

        return ssoConfiguration;
    }

    private static AppServerStatsPublishing prepareStatsPublishingConfigs() {
        AppServerStatsPublishing configuration = new AppServerStatsPublishing();

        configuration.setUsername(Constants.USERNAME);
        configuration.setPassword(Constants.PASSWORD);
        configuration.setDataAgentType(Constants.DATA_AGENT_TYPE);
        configuration.setAuthenticationURL(Constants.AUTHN_URL);
        configuration.setPublisherURL(Constants.PUBLISHER_URL);
        configuration.setStreamId(Constants.STREAM_ID);

        return configuration;
    }

    private static AppServerSecurity prepareSecurityConfigs() {
        AppServerSecurity configuration = new AppServerSecurity();

        AppServerSecurity.Keystore keystore = new AppServerSecurity.Keystore();
        keystore.setLocation(Constants.KEYSTORE_PATH);
        keystore.setPassword(Constants.KEYSTORE_PASSWORD);
        keystore.setType(Constants.TYPE);
        keystore.setKeyAlias(Constants.PRIVATE_KEY_ALIAS);
        keystore.setKeyPassword(Constants.PRIVATE_KEY_PASSWORD);

        AppServerSecurity.Truststore truststore = new AppServerSecurity.Truststore();
        truststore.setLocation(Constants.TRUSTSTORE_PATH);
        truststore.setType(Constants.TYPE);
        truststore.setPassword(Constants.TRUSTSTORE_PASSWORD);

        configuration.setKeystore(keystore);
        configuration.setTruststore(truststore);

        configuration.getKeystore().setLocation(STRING_SUB.replace(configuration.getKeystore().getLocation()));
        configuration.getTruststore().setLocation(STRING_SUB.replace(configuration.getTruststore().getLocation()));
        configuration.getKeystore().
                setLocation(StrSubstitutor.replaceSystemProperties(configuration.getKeystore().getLocation()));
        configuration.getTruststore().
                setLocation(StrSubstitutor.replaceSystemProperties(configuration.getTruststore().getLocation()));

        return configuration;
    }

    public static boolean compare(ApplicationServerConfiguration actual, ApplicationServerConfiguration expected) {
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
            boolean idpCertAlias = actual.getIdpCertificateAlias().trim().equals(expected.getIdpCertificateAlias());
            boolean properties = compareSSOProperties(actual.getProperties(), expected.getProperties());
            return (idpURL && idpEntityID && idpCertAlias && properties);
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
                            .filter(expProperty -> ((expProperty.getKey().equals(property.getKey())) && (expProperty.
                                    getValue().
                                            equals(property.getValue()))))
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
}
