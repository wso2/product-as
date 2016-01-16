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
package org.wso2.appserver.webapp.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Global ClassLoaderConfiguration. Contains information specified in the webapp-classloader-environments.xml and
 * webapp-classloader.xml. Individual webapps build their own Contexts based on this global configuration.
 */
public class ClassLoaderConfiguration {

    private boolean parentFirst = false;
    private List<String> defaultEnvironmentNames = new ArrayList<>();

    private Map<String, ClassLoaderEnvironment> environments = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void addEnvironment(ClassLoaderEnvironment environment) {
        environments.put(environment.getEnvironmentName(), environment);
    }

    public ClassLoaderEnvironment getEnvironment(String environmentName) {
        return environments.get(environmentName);
    }

    public boolean isParentFirst() {
        return parentFirst;
    }

    public void setParentFirstBehaviour(boolean parentFirst) {
        this.parentFirst = parentFirst;
    }

    public void setDefaultEnvironmentNames(List<String> defaultEnvironmentNames) {
        if (defaultEnvironmentNames != null) {
            this.defaultEnvironmentNames = defaultEnvironmentNames;
        }
    }

    public List<String> getDefaultEnvironmentNames() {
        return defaultEnvironmentNames;
    }
}
