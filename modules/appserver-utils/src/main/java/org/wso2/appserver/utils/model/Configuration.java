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
package org.wso2.appserver.utils.model;

import org.wso2.appserver.utils.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class defines the configuration properties for Application Server functions.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = Constants.WEBAPP_DESCRIPTOR_XML_ROOT_ELEMENT, namespace = Constants.WSO2_NAMESPACE)
public class Configuration {
    @XmlElement(name = Constants.SSOConfigurationConstants.SINGLE_SIGN_ON, namespace = Constants.WSO2_NAMESPACE)
    private SSOConfiguration singleSignOnConfiguration;
    @XmlElement(name = Constants.ClassLoadingConfigurationConstants.CLASSLOADING, namespace = Constants.WSO2_NAMESPACE)
    private ClassLoadingConfiguration classLoadingConfiguration;
    @XmlElement(name = "restWebServices", namespace = Constants.WSO2_NAMESPACE)
    private RestWebServicesConfiguration restWebServicesConfiguration;

    public SSOConfiguration getSingleSignOnConfiguration() {
        return singleSignOnConfiguration;
    }

    public void setSingleSignOnConfiguration(SSOConfiguration singleSignOnConfiguration) {
        this.singleSignOnConfiguration = singleSignOnConfiguration;
    }

    public ClassLoadingConfiguration getClassLoadingConfiguration() {
        return classLoadingConfiguration;
    }

    public void setClassLoadingConfiguration(ClassLoadingConfiguration classLoadingConfiguration) {
        this.classLoadingConfiguration = classLoadingConfiguration;
    }

    public RestWebServicesConfiguration getRestWebServicesConfiguration() {
        return restWebServicesConfiguration;
    }

    public void setRestWebServicesConfiguration(RestWebServicesConfiguration restWebServicesConfiguration) {
        this.restWebServicesConfiguration = restWebServicesConfiguration;
    }

    /**
     * A nested class which represents a holder for Application Server REST Web Service configurations.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RestWebServicesConfiguration {
        @XmlElement(name = Constants.RestWebServicesConfigurationConstants.ISMANAGEDAPI,
                namespace = Constants.WSO2_NAMESPACE)
        private Boolean isManagedAPI;

        public Boolean isManagedAPI() {
            return isManagedAPI;
        }
    }
}
