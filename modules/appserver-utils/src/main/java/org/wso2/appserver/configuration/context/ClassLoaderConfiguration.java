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
 */

package org.wso2.appserver.configuration.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class which models context-level classloading configurations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ClassLoaderConfiguration {
    @XmlElement(name = "parent-first")
    private Boolean isParentFirst;
    @XmlElement
    private String environments;

    public Boolean isParentFirst() {
        return isParentFirst;
    }

    public void enableParentFirst(Boolean isParentFirst) {
        this.isParentFirst = isParentFirst;
    }

    public String getEnvironments() {
        return environments;
    }

    public void setEnvironments(String environments) {
        this.environments = environments;
    }

    public void merge(ClassLoaderConfiguration newClassLoaderConfiguration) {

        if (newClassLoaderConfiguration != null) {
            if (newClassLoaderConfiguration.isParentFirst() != null) {
                this.enableParentFirst(newClassLoaderConfiguration.isParentFirst());
            }
            if (newClassLoaderConfiguration.getEnvironments() != null) {
                this.setEnvironments(newClassLoaderConfiguration.getEnvironments());
            }
        }
    }
}
