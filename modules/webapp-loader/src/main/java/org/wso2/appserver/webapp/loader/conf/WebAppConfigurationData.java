/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.appserver.webapp.loader.conf;

import java.util.ArrayList;
import java.util.List;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * An instance of this class holds configuration information of a web application.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", namespace = "http://wso2as-web-config/xsd")
@XmlRootElement(name = "wso2as-web", namespace = WebAppConfigurationConstants.NAMESPACE)
public class WebAppConfigurationData {

    @XmlElement(name = "single-sign-on", namespace = WebAppConfigurationConstants.NAMESPACE)
    private boolean singleSignOn;

    @XmlElement(name = "statistics-publisher", namespace = WebAppConfigurationConstants.NAMESPACE)
    private StatisticsPublisher statisticsPublisher;

    @XmlElement(name = "classloading", namespace = WebAppConfigurationConstants.NAMESPACE)
    private Classloading classloading;

    @XmlElement(name = "web-service-discovery", namespace = WebAppConfigurationConstants.NAMESPACE)
    private boolean webServiceDiscovery;

    @XmlElement(name = "rest-web-services", namespace = WebAppConfigurationConstants.NAMESPACE)
    private RestWebServices restWebServices;

    public boolean isSingleSignOnEnabled() {
        return singleSignOn;
    }

    public void setSingleSignOnEnabled(boolean value) {
        singleSignOn = value;
    }

    public StatisticsPublisher getStatisticsPublisher() {
        return statisticsPublisher;
    }

    public void setStatisticsPublisher(StatisticsPublisher value) {
        this.statisticsPublisher = value;
    }

    public Classloading getClassloading() {
        return classloading;
    }

    public void setClassloading(Classloading value) {
        this.classloading = value;
    }

    public boolean isWebServiceDiscoveryEnabled() {
        return webServiceDiscovery;
    }

    public RestWebServices getRestWebServices() {
        return restWebServices;
    }

    public void setRestWebServices(RestWebServices value) {
        this.restWebServices = value;
    }



    public void setParentFirst(boolean parentFirst) {
        getClassloading().setParentFirst(parentFirst);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "parentFirst",
            "environments"
    })
    public static class Classloading {

        @XmlElement(name = "parent-first", namespace = WebAppConfigurationConstants.NAMESPACE)
        private Boolean parentFirst;

        @XmlElement(name = "environments", namespace = WebAppConfigurationConstants.NAMESPACE)
        private Environments environments;

        public Boolean isParentFirst() {
            return parentFirst;
        }

        public void setParentFirst(Boolean value) {
            this.parentFirst = value;
        }

        public Environments getEnvironments() {
            return environments;
        }

        public void setEnvironments(Environments env) {
            environments = new Environments();
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "environment"
        })
        public static class Environments {

            @XmlElement(name = "environment", required = true, namespace = WebAppConfigurationConstants.NAMESPACE)
            private List<String> environment;

            public List<String> getEnvironment() {
                if (environment == null) {
                    environment = new ArrayList<>();
                }
                return environment;
            }

            public void setEnvironment(List<String> environmentList) {
                environment = environmentList;
            }

        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "isManagedApi"
    })
    public static class RestWebServices {

        @XmlElement(name = "is-managed-api", namespace = WebAppConfigurationConstants.NAMESPACE)
        private boolean isManagedApi;

        public boolean isManagedApi() {
            return isManagedApi;
        }

        public void setIsManagedApi(boolean value) {
            isManagedApi = value;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "enabled",
            "streamId"
    })
    public static class StatisticsPublisher {
        @XmlElement(name = "enabled", required = true, namespace = WebAppConfigurationConstants.NAMESPACE)
        private boolean enabled;
        @XmlElement(name = "stream-id", required = true, namespace = WebAppConfigurationConstants.NAMESPACE)
        private String streamId;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean value) {
            enabled = value;
        }

        public String getStreamId() {
            return streamId;
        }


    }

}
