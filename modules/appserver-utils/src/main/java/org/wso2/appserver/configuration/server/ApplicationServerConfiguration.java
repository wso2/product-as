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
package org.wso2.appserver.configuration.server;

import org.apache.commons.lang3.text.StrSubstitutor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Java class which models a holder for server level WSO2 specific configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AppServer")
public class ApplicationServerConfiguration {
    @XmlElement(name = "ClassLoader")
    private AppServerClassLoading classLoaderEnvironments;
    @XmlElement(name = "SAML2SingleSignOn")
    private AppServerSingleSignOn singleSignOnConfiguration;
    @XmlElement(name = "StatisticsPublisher")
    private AppServerStatsPublishing statsPublisherConfiguration;
    @XmlElement(name = "Security")
    private AppServerSecurity securityConfiguration;
    @XmlElement(name = "ApiEverywhere")
    private AppServerApiEverywhere apiEverywhereConfiguration;

    public AppServerClassLoading getClassLoaderEnvironments() {
        return classLoaderEnvironments;
    }

    public void setClassLoaderEnvironments(AppServerClassLoading classLoaderEnvironments) {
        this.classLoaderEnvironments = classLoaderEnvironments;
    }

    public AppServerSingleSignOn getSingleSignOnConfiguration() {
        return singleSignOnConfiguration;
    }

    public void setSingleSignOnConfiguration(AppServerSingleSignOn singleSignOnConfiguration) {
        this.singleSignOnConfiguration = singleSignOnConfiguration;
    }

    public AppServerStatsPublishing getStatsPublisherConfiguration() {
        return statsPublisherConfiguration;
    }

    public void setStatsPublisherConfiguration(AppServerStatsPublishing statsPublisherConfiguration) {
        this.statsPublisherConfiguration = statsPublisherConfiguration;
    }

    public AppServerSecurity getSecurityConfiguration() {
        return securityConfiguration;
    }

    public void setSecurityConfiguration(AppServerSecurity securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Resolves the environmental and system variable placeholders specified among the configurations.
     */
    public void resolveVariables() {
        resolveEnvVariables();
        resolveSystemProperties();
    }

    /**
     * Resolves the environmental variable placeholders specified among the configurations.
     */
    private void resolveEnvVariables() {
        StrSubstitutor strSubstitutor = new StrSubstitutor(System.getenv());
        classLoaderEnvironments.getEnvironments().getEnvironments().
                forEach(environment -> environment.setClasspath(strSubstitutor.replace(environment.getClasspath())));
        securityConfiguration.getKeystore().
                setLocation(strSubstitutor.replace(securityConfiguration.getKeystore().getLocation()));
        securityConfiguration.getTruststore().
                setLocation(strSubstitutor.replace(securityConfiguration.getTruststore().getLocation()));
    }

    /**
     * Resolves the system variable placeholders specified among the configurations.
     */
    private void resolveSystemProperties() {
        classLoaderEnvironments.getEnvironments().getEnvironments().forEach(environment -> environment.
                setClasspath(StrSubstitutor.replaceSystemProperties(environment.getClasspath())));
        securityConfiguration.getKeystore().
                setLocation(StrSubstitutor.replaceSystemProperties(securityConfiguration.getKeystore().getLocation()));
        securityConfiguration.getTruststore().setLocation(
                StrSubstitutor.replaceSystemProperties(securityConfiguration.getTruststore().getLocation()));
    }

    public AppServerApiEverywhere getApiEverywhereConfiguration() {
        return apiEverywhereConfiguration;
    }

    public void setApiEverywhereConfiguration(AppServerApiEverywhere apiEverywhereConfiguration) {
        this.apiEverywhereConfiguration = apiEverywhereConfiguration;
    }
}
