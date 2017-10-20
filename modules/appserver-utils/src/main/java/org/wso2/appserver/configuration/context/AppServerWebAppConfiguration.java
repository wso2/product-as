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
public class AppServerWebAppConfiguration {
    @XmlElement(name = "class-loader")
    private WebAppClassLoading classLoaderConfiguration;
    @XmlElement(name = "saml2-single-sign-on")
    private WebAppSingleSignOn singleSignOnConfiguration;
    @XmlElement(name = "statistics-publisher")
    private WebAppStatsPublishing statsPublisherConfiguration;
    @XmlElement(name = "api-everywhere")
    private WebAppApiEverywhere apiEverywhereConfiguration;

    public WebAppClassLoading getClassLoaderConfiguration() {
        return classLoaderConfiguration;
    }

    public void setClassLoaderConfiguration(WebAppClassLoading classLoaderConfiguration) {
        this.classLoaderConfiguration = classLoaderConfiguration;
    }

    public WebAppSingleSignOn getSingleSignOnConfiguration() {
        return singleSignOnConfiguration;
    }

    public void setSingleSignOnConfiguration(WebAppSingleSignOn singleSignOnConfiguration) {
        this.singleSignOnConfiguration = singleSignOnConfiguration;
    }

    public WebAppStatsPublishing getStatsPublisherConfiguration() {
        return statsPublisherConfiguration;
    }

    public void setStatsPublisherConfiguration(WebAppStatsPublishing statsPublisherConfiguration) {
        this.statsPublisherConfiguration = statsPublisherConfiguration;
    }

    /**
     * Merges the globally defined context level configurations and context level configurations overridden at
     * context level.
     *
     * @param webAppConfiguration group of context level configuration capable of being merged with this group
     */
    public void merge(AppServerWebAppConfiguration webAppConfiguration) {
        Optional.ofNullable(webAppConfiguration)
                .ifPresent(configuration -> {
                    Optional.ofNullable(classLoaderConfiguration)
                            .ifPresent(classLoaderConfig ->
                                    classLoaderConfig.merge(configuration.classLoaderConfiguration));

                    classLoaderConfiguration = Optional.ofNullable(classLoaderConfiguration)
                            .orElse(configuration.classLoaderConfiguration);

                    Optional.ofNullable(singleSignOnConfiguration)
                            .ifPresent(ssoConfig -> ssoConfig.merge(configuration.singleSignOnConfiguration));

                    singleSignOnConfiguration = Optional.ofNullable(singleSignOnConfiguration)
                            .orElse(configuration.singleSignOnConfiguration);

                    Optional.ofNullable(statsPublisherConfiguration)
                            .ifPresent(statsPublisherConfig ->
                                    statsPublisherConfig.merge(configuration.statsPublisherConfiguration));

                    statsPublisherConfiguration = Optional.ofNullable(statsPublisherConfiguration)
                            .orElse(configuration.statsPublisherConfiguration);

                    Optional.ofNullable(apiEverywhereConfiguration)
                            .ifPresent(apiEverywhere ->
                                    apiEverywhere.merge(configuration.apiEverywhereConfiguration));

                    apiEverywhereConfiguration = Optional.ofNullable(apiEverywhereConfiguration)
                            .orElse(configuration.apiEverywhereConfiguration);
                });
    }

    public WebAppApiEverywhere getApiEverywhereConfiguration() {
        return apiEverywhereConfiguration;
    }

    public void setApiEverywhereConfiguration(WebAppApiEverywhere apiEverywhereConfiguration) {
        this.apiEverywhereConfiguration = apiEverywhereConfiguration;
    }
}
