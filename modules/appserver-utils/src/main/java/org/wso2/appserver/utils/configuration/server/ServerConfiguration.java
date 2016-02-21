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
package org.wso2.appserver.utils.configuration.server;

import org.wso2.appserver.utils.configuration.ConfigurationConstants;
import org.wso2.appserver.utils.configuration.server.components.ClassloadingConfiguration;
import org.wso2.appserver.utils.configuration.server.components.SSOConfiguration;
import org.wso2.appserver.utils.configuration.server.components.StatsPublisherConfiguration;

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
@XmlRootElement(name = "AppServer", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
public class ServerConfiguration {
    @XmlElement(name = "Classloading", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private ClassloadingConfiguration classloadingConfiguration;
    @XmlElement(name = "SingleSignOn", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private SSOConfiguration singleSignOnConfiguration;
    @XmlElement(name = "StatisticsPublisher", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private StatsPublisherConfiguration statsPublisherConfiguration;

    public ClassloadingConfiguration getClassloadingConfiguration() {
        return classloadingConfiguration;
    }

    public void setClassloadingConfiguration(ClassloadingConfiguration classloadingConfiguration) {
        this.classloadingConfiguration = classloadingConfiguration;
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
}
