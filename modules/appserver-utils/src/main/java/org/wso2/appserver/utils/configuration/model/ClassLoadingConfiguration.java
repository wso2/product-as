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
package org.wso2.appserver.utils.configuration.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A class which represents a holder for Application Server class-loading configurations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassLoadingConfiguration {
    @XmlElement(name = ConfigurationConstants.ClassLoadingConfigurationConstants.ENVIRONMENTS,
            namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private Environments environments;

    public Environments getEnvironments() {
        return environments;
    }

    /**
     * A nested class which models a group of class-loading environments for Application Server.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Environments {
        @XmlElement(name = ConfigurationConstants.ClassLoadingConfigurationConstants.ENVIRONMENT,
                namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private List<Environment> environments;

        public List<Environment> getEnvironments() {
            return environments;
        }
    }

    /**
     * A nested class which models a class-loading environment for Application Server.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Environment {
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String name;
        @XmlElement(name = ConfigurationConstants.ClassLoadingConfigurationConstants.CLASSPATH,
                namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private List<String> classpaths;

        public String getName() {
            return name;
        }

        public List<String> getClasspaths() {
            return classpaths;
        }
    }
}
