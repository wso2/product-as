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

package org.wso2.appserver.webapp.loader.util;

import org.apache.catalina.Context;
import org.wso2.appserver.configuration.context.ClassLoaderConfiguration;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.ClassLoaderEnvironments;
import org.wso2.appserver.webapp.loader.exceptions.ApplicationServerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebappClassLoaderContext2 {

    private static Map<String, List<String>> definedEnvironments = new ConcurrentHashMap<>();

    private boolean isParentFirst = false;
    private Map<String, List<String>> environments = new ConcurrentHashMap<>();

    static {
        List<ClassLoaderEnvironments.Environment> defEnvs = ServerConfigurationLoader.getServerConfiguration()
                .getClassLoaderEnvironments().getEnvironments().getEnvironments();
        for(ClassLoaderEnvironments.Environment defEnv : defEnvs){
            if(!definedEnvironments.containsKey(defEnv.getName())){
                List<String> repositories = new ArrayList<>();
                repositories.addAll(Utils.generateClasspath(defEnv.getClasspath()));
                definedEnvironments.put(defEnv.getName(),repositories);
            }
        }
    }

    public WebappClassLoaderContext2(Context context) throws ApplicationServerException{


        Optional<ContextConfiguration> cc = ContextConfigurationLoader.retrieveContextConfiguration(context);
        ClassLoaderConfiguration clf = cc.get().getClassLoaderConfiguration();
        this.isParentFirst = clf.isParentFirst();

        List<String> environmentNames = Arrays.asList(clf.getEnvironments().split("\\s*,\\s*"));
        for(String environmentName : environmentNames){
            if(definedEnvironments.containsKey(environmentName)){
                environments.put(environmentName,definedEnvironments.get(environmentName));
            }
        }
    }


    public List<String> getProvidedRepositories() {
        List<String> repositroies = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : environments.entrySet()) {
            repositroies.addAll(entry.getValue());
        }
        return repositroies;
    }

    public boolean isParentFirst() {
        return isParentFirst;
    }

    public boolean isDelegatedPackage(String name) {
        return false;
    }

    public boolean isExcludedPackage(String name) {
        return false;
    }
}
