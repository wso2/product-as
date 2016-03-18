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
public class AppServerConfiguration {
    @XmlElement(name = "ClassLoader")
    private ClassLoaderEnvironments classLoaderEnvironments;
    @XmlElement(name = "SAML2SingleSignOn")
    private SSOConfiguration singleSignOnConfiguration;
    @XmlElement(name = "StatisticsPublisher")
    private StatsPublisherConfiguration statsPublisherConfiguration;
    @XmlElement(name = "Security")
    private SecurityConfiguration securityConfiguration;

    public ClassLoaderEnvironments getClassLoaderEnvironments() {
        return classLoaderEnvironments;
    }

    public void setClassLoaderEnvironments(ClassLoaderEnvironments classLoaderEnvironments) {
        this.classLoaderEnvironments = classLoaderEnvironments;
    }

    public SSOConfiguration getSingleSignOnConfiguration() {
        return singleSignOnConfiguration;
    }

    public void setSingleSignOnConfiguration(SSOConfiguration singleSignOnConfiguration) {
        this.singleSignOnConfiguration = singleSignOnConfiguration;
    }

    public StatsPublisherConfiguration getStatsPublisherConfiguration() {
        return statsPublisherConfiguration;
    }

    public void setStatsPublisherConfiguration(StatsPublisherConfiguration statsPublisherConfiguration) {
        this.statsPublisherConfiguration = statsPublisherConfiguration;
    }

    public SecurityConfiguration getSecurityConfiguration() {
        return securityConfiguration;
    }

    public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }
}
