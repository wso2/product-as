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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains information about the class loading behaviour of a webapp.
 */
public class WebappClassLoaderContext {

    private static final Log log = LogFactory.getLog(WebappClassLoaderContext.class); // TODO: ASK log requirement

    private boolean parentFirst = false;

    private List<String> delegatedPackages = new ArrayList<>();
    private List<String> delegatedPackageStems = new ArrayList<>();
    private List<String> excludedPackages = new ArrayList<>();
    private List<String> excludedPackageStems = new ArrayList<>();
    private List<String> delegatedResources = new ArrayList<>();
    private List<String> delegatedResourceStems = new ArrayList<>();
    private List<String> excludedResources = new ArrayList<>();
    private List<String> excludedResourceStems = new ArrayList<>();

    private List<String> repositories = new ArrayList<>();
    private List<String> environments = new ArrayList<>();

    private boolean noExcludedPackages = true;
    private boolean noExcludedResources = true;
    private boolean delegateAllPackages = false;
    private boolean delegateAllResources = false;

    public WebappClassLoaderContext() {
    }

    /**
     * Check whether the specific package is a delegated package.
     *
     * @param name full name of the package
     * @return true if the package is marked as a delegated package.Otherwise false.
     */
    public boolean isDelegatedPackage(String name) {

        if (delegateAllPackages) {
            return true;
        }

        Optional<String> optionalName = Optional.ofNullable(name);

        if (!optionalName.isPresent()) {
            return false;
        } else {

            // Looking up the package
            String packageName;
            int pos = optionalName.get().lastIndexOf('.');
            if (pos != -1) {
                packageName = optionalName.get().substring(0, pos);
            } else {
                return false;
            }

            return (delegatedPackageStems.stream().anyMatch(packageName::startsWith)
                    || delegatedPackages.stream().anyMatch(packageName::equals));
        }


    }

    /**
     * Check whether the specific package is excluded.
     *
     * @param name full name of the package
     * @return true if the package is excluded.Otherwise false.
     */
    public boolean isExcludedPackage(String name) {

        if (noExcludedPackages) {
            return false;
        }

        Optional<String> optionalName = Optional.ofNullable(name);

        if (!optionalName.isPresent()) {
            return false;
        } else {
            // Looking up the package
            String packageName;
            int pos = optionalName.get().lastIndexOf('.');
            if (pos != -1) {
                packageName = name.substring(0, pos);
            } else {
                return false;
            }

            return (excludedPackageStems.stream().anyMatch(packageName::startsWith)
                    || excludedPackages.stream().anyMatch(packageName::equals));
        }


    }

    /**
     * Check whether the specific resources is excluded.
     *
     * @param name full name of the resource
     * @return true if the resource is excluded.Otherwise false.
     */
    public boolean isExcludedResources(String name) {
        if (noExcludedResources) {
            return false;
        }

        Optional<String> optionalName = Optional.ofNullable(name);

        return optionalName.isPresent()
                && (excludedResourceStems.stream().anyMatch(optionalName.get()::startsWith)
                || excludedResources.stream().anyMatch(optionalName.get()::equals));

    }

    /**
     * Check whether the specific resources is a delegated resource.
     *
     * @param name full name of the resource
     * @return true if the resource is belongs to the delegated resource list.Otherwise false.
     */
    public boolean isDelegatedResource(String name) {

        if (delegateAllResources) {
            return true;
        }

        Optional<String> optionalName = Optional.ofNullable(name);

        return optionalName.isPresent()
                && (delegatedResourceStems.stream().anyMatch(optionalName.get()::startsWith)
                || delegatedResources.stream().anyMatch(optionalName.get()::equals));


    }

    /**
     * Build the delegated and excluded packages to the webapp.
     *
     * @param delegatedPkgList a list containing the paths to packages
     */
    public void setDelegatedPackages(List<String> delegatedPkgList) {

        excludedPackageStems = delegatedPkgList.stream()
                .filter(pkg -> pkg.startsWith("!"))
                .map(s -> s.substring(1))
                .filter(s -> s.endsWith(".*"))
                .map(s -> s.substring(0, s.length() - 2))
                .collect(Collectors.toList());

        excludedPackages = delegatedPkgList.stream()
                .filter(pkg -> pkg.startsWith("!"))
                .map(s -> s.substring(1))
                .filter(s -> !s.endsWith(".*"))
                .collect(Collectors.toList());

        delegatedPackageStems = delegatedPkgList.stream()
                .filter(pkg -> (!pkg.startsWith("!") && pkg.endsWith(".*")))
                .map(s -> s.substring(0, s.length() - 2))
                .collect(Collectors.toList());
        delegatedPackages = delegatedPkgList.stream()
                .filter(pkg -> (!pkg.startsWith("!") && !pkg.equals("*") && !pkg.endsWith(".*")))
                .collect(Collectors.toList());

        delegateAllPackages = delegatedPkgList.stream()
                .anyMatch(pkg -> pkg.equals("*"));

        noExcludedPackages = !delegatedPkgList.stream()
                .anyMatch(pkg -> pkg.startsWith("!"));

    }

    /**
     * Build the delegated and excluded resources to the webapp.
     *
     * @param delegatedResourceList a list containing the paths to resources
     */
    public void setDelegatedResources(List<String> delegatedResourceList) {

        excludedResourceStems = delegatedResourceList.stream()
                .filter(res -> res.startsWith("!"))
                .map(s -> s.substring(1))
                .filter(s -> s.endsWith("/*"))
                .map(s -> s.substring(0, s.length() - 2))
                .collect(Collectors.toList());

        excludedResources = delegatedResourceList.stream()
                .filter(res -> res.startsWith("!"))
                .map(s -> s.substring(1))
                .filter(s -> !s.endsWith("/*"))
                .collect(Collectors.toList());

        delegatedResourceStems = delegatedResourceList.stream()
                .filter(res -> (!res.startsWith("!") && res.endsWith("/*")))
                .map(s -> s.substring(0, s.length() - 2))
                .collect(Collectors.toList());

        delegatedResources = delegatedResourceList.stream()
                .filter(res -> (!res.startsWith("!") && !res.equals("*") && !res.endsWith("/*")))
                .collect(Collectors.toList());

        delegateAllResources = delegatedResourceList.stream()
                .anyMatch(res -> res.equals("*"));

        noExcludedResources = !delegatedResourceList.stream()
                .anyMatch(res -> res.startsWith("!"));

    }


    public String[] getProvidedRepositories() {
        return repositories.toArray(new String[repositories.size()]);
    }

    public void setProvidedRepositories(List<String> repositories) {
        this.repositories.addAll(repositories);
    }

    public boolean isParentFirst() {
        return parentFirst;
    }

    public void setParentFirst(boolean parentFirst) {
        this.parentFirst = parentFirst;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }

    public String[] getEnvironments() {
        return environments.toArray(new String[environments.size()]);
    }


}
