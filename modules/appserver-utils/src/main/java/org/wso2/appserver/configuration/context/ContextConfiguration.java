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
package org.wso2.appserver.configuration.context;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * A Java class which models a holder for context level WSO2 specific configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "wso2as-web")
public class ContextConfiguration {
    @XmlElement(name = "class-loader")
    private ClassLoaderConfiguration classLoaderConfiguration;
    @XmlElement(name = "saml2-single-sign-on")
    private SSOConfiguration singleSignOnConfiguration;

    public ClassLoaderConfiguration getClassLoaderConfiguration() {
        return classLoaderConfiguration;
    }

    public void setClassLoaderConfiguration(ClassLoaderConfiguration classLoaderConfiguration) {
        this.classLoaderConfiguration = classLoaderConfiguration;
    }

    public SSOConfiguration getSingleSignOnConfiguration() {
        return singleSignOnConfiguration;
    }

    public void setSingleSignOnConfiguration(SSOConfiguration singleSignOnConfiguration) {
        this.singleSignOnConfiguration = singleSignOnConfiguration;
    }

    /**
     * Merges the globally defined context level configurations and context level configurations overridden at
     * context level.
     *
     * @param newContextConfiguration the locally overridden context level configurations
     */
    public void merge(ContextConfiguration newContextConfiguration) {
        this.classLoaderConfiguration.merge(newContextConfiguration.getClassLoaderConfiguration());
        this.singleSignOnConfiguration.merge(newContextConfiguration.getSingleSignOnConfiguration());
    }



}
