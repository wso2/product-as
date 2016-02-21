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
package org.wso2.appserver.utils.configuration.server.components;

import org.wso2.appserver.utils.configuration.ConfigurationConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Java class which models a holder for server level statistics publisher configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
public class StatsPublisherConfiguration {
    @XmlElement(name = "Username", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private String username;
    @XmlElement(name = "Password", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private String password;
    @XmlElement(name = "URL", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private String url;
    @XmlElement(name = "StreamId", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private String streamId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}
