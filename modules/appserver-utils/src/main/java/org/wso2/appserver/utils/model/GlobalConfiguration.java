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
@XmlRootElement(name = ConfigurationConstants.SERVER_CONFIGURATION_XML_ROOT_ELEMENT,
        namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
public class GlobalConfiguration {
    @XmlElement(name = ConfigurationConstants.ClassloadingConstants.WSO2AS_CLASSLOADING,
            namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private ServerClassloadingConfiguration classloadingConfiguration;
    @XmlElement(name = ConfigurationConstants.SSOConstants.WSO2AS_SINGLE_SIGN_ON,
            namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private ServerSSOConfiguration singleSignOnConfiguration;

    public ServerClassloadingConfiguration getClassloadingConfiguration() {
        return classloadingConfiguration;
    }

    public void setClassloadingConfiguration(ServerClassloadingConfiguration classloadingConfiguration) {
        this.classloadingConfiguration = classloadingConfiguration;
    }

    public ServerSSOConfiguration getSingleSignOnConfiguration() {
        return singleSignOnConfiguration;
    }

    public void setSingleSignOnConfiguration(ServerSSOConfiguration singleSignOnConfiguration) {
        this.singleSignOnConfiguration = singleSignOnConfiguration;
    }
}
